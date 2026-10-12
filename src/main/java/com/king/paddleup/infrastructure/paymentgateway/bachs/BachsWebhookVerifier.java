package com.king.paddleup.infrastructure.paymentgateway.bachs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Slf4j
@Component
public class BachsWebhookVerifier {

    private static final String HMAC_SHA256 = "HmacSHA256";

    private final String webhookSecret;

    public BachsWebhookVerifier(@Value("${bachs.webhook-secret:}") String webhookSecret) {
        this.webhookSecret = webhookSecret != null ? webhookSecret.trim() : "";
    }

    public boolean verifySignature(String rawBody, String signatureHeader, String timestampHeader) {
        if (webhookSecret.isEmpty()) {
            log.warn("Bachs webhook secret is not configured; skipping signature verification for development");
            return true;
        }

        if (signatureHeader == null || signatureHeader.isBlank()) {
            log.warn("Missing Bachs webhook signature header");
            return false;
        }

        String expectedSignature = null;
        String timestamp = timestampHeader;

        if (signatureHeader.contains("v1=")) {
            String[] parts = signatureHeader.split(",");
            for (String part : parts) {
                String trimmed = part.trim();
                if (trimmed.startsWith("t=")) {
                    timestamp = trimmed.substring(2);
                } else if (trimmed.startsWith("v1=")) {
                    expectedSignature = trimmed.substring(3);
                }
            }
        } else {
            expectedSignature = signatureHeader.trim();
        }

        if (expectedSignature == null || expectedSignature.isBlank()) {
            log.warn("Unable to extract signature from header: {}", signatureHeader);
            return false;
        }

        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKey = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(secretKey);

            String messageToSign = (timestamp != null && !timestamp.isBlank())
                    ? timestamp + "." + rawBody
                    : rawBody;

            byte[] computedHash = mac.doFinal(messageToSign.getBytes(StandardCharsets.UTF_8));
            String computedHex = HexFormat.of().formatHex(computedHash);

            return MessageDigest.isEqual(
                    computedHex.getBytes(StandardCharsets.UTF_8),
                    expectedSignature.toLowerCase().getBytes(StandardCharsets.UTF_8)
            );
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            log.error("Failed to verify Bachs webhook HMAC-SHA256 signature", ex);
            return false;
        }
    }
}
