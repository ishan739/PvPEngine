package com.example.pvpengine.webhook;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
@Slf4j
public class WebhookSigner {

    private static final String ALGORITHM = "HmacSHA256";

    @Value("${pvpengine.webhook.secret}")
    private String webHookSecret;

    public String sign(String payload) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(webHookSecret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            mac.init(keySpec);
            byte[] hmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            return "sha256" + HexFormat.of().formatHex(hmac);
        } catch (Exception e) {
            log.error("Failed to sign webhook payload", e);
            throw new RuntimeException("Webhook signing failed", e);
        }
    }
}
