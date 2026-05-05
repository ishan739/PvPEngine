package com.example.pvpengine.kafka;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MatchEventProducer {

    public static final String MATCH_COMPLETED_TOPIC = "match.completed.v1";

    private final KafkaTemplate<String, MatchCompletedEvent> kafkaTemplate;

    public void publishMatchCompleted(MatchCompletedEvent event) {
        kafkaTemplate.send(MATCH_COMPLETED_TOPIC , event.getMatchId().toString(), event)
                .whenComplete((result, ex) -> {
                    if(ex != null) {
                        log.error("Failed to publish match.completed.v1 for matchId={}: {}",
                                event.getMatchId(), ex.getMessage(), ex);
                    } else {
                        log.info("Published match.completed.v1 for matchId={}, offset={}",
                                event.getMatchId(),
                                result.getRecordMetadata().offset());
                }
                });
    }
}
