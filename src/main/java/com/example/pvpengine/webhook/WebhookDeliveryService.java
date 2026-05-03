package com.example.pvpengine.webhook;

import com.example.pvpengine.match.Match;
import com.example.pvpengine.player.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebhookDeliveryService {
    private static final int MAX_ATTEMPTS = 5;
    private static final long[] BACKOFF_SECONDS = {30 , 60 , 120 , 240 , 480};
    private final WebhookDeliveryRepository deliveryRepository;
    private final WebhookSigner webhookSigner;
    private final ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Transactional
    public void scheduleMatchFoundWebhook(Match match , Player player1 , Player player2 , String webhookUrl) {
        try{
            MatchFoundPayload payload = MatchFoundPayload.builder()
                    .eventType("match.found")
                    .matchId(match.getId())
                    .gameId(match.getGameId())
                    .players(List.of(
                            MatchFoundPayload.PlayerInfo.builder()
                                    .playerId(player1.getId())
                                    .rating(player1.getRating())
                                    .build(),
                            MatchFoundPayload.PlayerInfo.builder()
                                    .playerId(player2.getId())
                                    .rating(player2.getRating())
                                    .build()
                    ))
                    .createdAt(match.getCreatedAt())
                    .build();

            String body = objectMapper.writeValueAsString(payload);

            WebhookDelivery delivery = WebhookDelivery.builder()
                    .matchId(match.getId())
                    .gameId(match.getGameId())
                    .requestBody(body)
                    .targetUrl(webhookUrl)
                    .status(WebhookDeliveryStatus.PENDING)
                    .nextRetryAt(OffsetDateTime.now())
                    .eventType("match.found")
                    .build();

            deliveryRepository.save(delivery);
            log.info("Webhook delivery scheduled for matchId={}", match.getId());

        } catch (Exception e) {
            log.error("Failed to schedule webhook for matchId={}", match.getId(), e);
        }
    }

    @Scheduled(fixedDelay = 30000)
    public void retryPendingDeliveries() {
        List<WebhookDelivery> due = deliveryRepository.findPendingDue(OffsetDateTime.now());
        if (due.isEmpty()) return;

        log.info("Retrying {} pending webhook deliveries", due.size());
        for (WebhookDelivery delivery : due) {
            attempt(delivery);
        }
    }

    @Transactional
    public void attempt(WebhookDelivery delivery) {
        delivery.setAttemptCount(delivery.getAttemptCount() + 1);
        delivery.setLastAttemptAt(OffsetDateTime.now());

        try {
            String signature = webhookSigner.sign(delivery.getRequestBody());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(delivery.getTargetUrl()))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type" , "application/json")
                    .header("X-PVP-Signature" , signature)
                    .header("X-PVP-Event", delivery.getEventType())
                    .POST(HttpRequest.BodyPublishers.ofString(delivery.getRequestBody()))
                    .build();

            HttpResponse<String> response = httpClient.send(request , HttpResponse.BodyHandlers.ofString());

            delivery.setResponseCode(response.statusCode());
            delivery.setResponseBody(response.body());

            if(response.statusCode() >= 200 && response.statusCode() < 300){
                delivery.setStatus(WebhookDeliveryStatus.SUCCESS);
                log.info("Webhook delivered successfully: deliveryId={}, matchId={}, status={}",
                        delivery.getId(), delivery.getMatchId(), response.statusCode());
            } else {
                scheduleRetryOrFail(delivery);
                log.warn("Webhook delivery failed: deliveryId={}, status={}", delivery.getId(), response.statusCode());
            }
        }
        catch (Exception e){
            delivery.setResponseBody(e.getMessage());
            scheduleRetryOrFail(delivery);
            log.error("Webhook delivery exception: deliveryId={}, error={}", delivery.getId(), e.getMessage());
        }

        deliveryRepository.save(delivery);
    }

    private void scheduleRetryOrFail(WebhookDelivery delivery) {
        int attempt = delivery.getAttemptCount();
        if(attempt >= MAX_ATTEMPTS){
            delivery.setStatus(WebhookDeliveryStatus.FAILED);
            log.error("Webhook delivery permanently failed after {} attempts: deliveryId={}, matchId={}",
                    MAX_ATTEMPTS, delivery.getId(), delivery.getMatchId());
        }
        long backoffSeconds = BACKOFF_SECONDS[Math.min(attempt - 1, BACKOFF_SECONDS.length - 1)];
        delivery.setNextRetryAt(OffsetDateTime.now().plusSeconds(backoffSeconds));
    }

}
