package com.king.paddleup.domain.idempotency;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.king.paddleup.domain.idempotency.enums.IdempotencyStatus;
import com.king.paddleup.domain.idempotency.exception.IdempotencyConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyRepository idempotencyRepository;
    private final ObjectMapper objectMapper;

    public String computeHash(Object payload) {
        if (payload == null) {
            return "empty";
        }
        try {
            String json;
            if (payload instanceof String str) {
                json = str;
            } else {
                json = objectMapper.writeValueAsString(payload);
            }
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(json.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException | JacksonException ex) {
            log.error("Failed to compute SHA-256 hash for payload", ex);
            throw new IllegalStateException("Failed to calculate idempotency hash", ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<Idempotency> checkAndLock(String key, String target, String requestHash) {
        Optional<Idempotency> existingOpt = idempotencyRepository.findByIdempotencyKey(key);

        if (existingOpt.isPresent()) {
            Idempotency existing = existingOpt.get();

            if (!existing.getRequestHash().equals(requestHash)) {
                throw new IdempotencyConflictException(
                        "Idempotency key '" + key + "' was previously used with a different request payload"
                );
            }

            if (existing.getStatus() == IdempotencyStatus.PENDING) {
                throw new IdempotencyConflictException(
                        "A request with idempotency key '" + key + "' is currently in progress"
                );
            }

            if (existing.getStatus() == IdempotencyStatus.COMPLETED) {
                return Optional.of(existing);
            }

            // If previously failed, allow retry by updating status to PENDING
            existing.setStatus(IdempotencyStatus.PENDING);
            existing.setResponseBody(null);
            existing.setResponseStatusCode(null);
            existing.setUpdatedAt(Instant.now());
            idempotencyRepository.save(existing);
            return Optional.empty();
        }

        Idempotency newRecord = Idempotency.builder()
                .idempotencyKey(key)
                .target(target)
                .requestHash(requestHash)
                .status(IdempotencyStatus.PENDING)
                .expiresAt(Instant.now().plus(24, ChronoUnit.HOURS))
                .build();

        idempotencyRepository.save(newRecord);
        return Optional.empty();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markCompleted(String key, int statusCode, Object responsePayload) {
        idempotencyRepository.findByIdempotencyKey(key).ifPresent(record -> {
            record.setStatus(IdempotencyStatus.COMPLETED);
            record.setResponseStatusCode(statusCode);
            try {
                String body = (responsePayload instanceof String str)
                        ? str
                        : objectMapper.writeValueAsString(responsePayload);
                record.setResponseBody(body);
            } catch (JacksonException e) {
                log.error("Failed to serialize response body for idempotency key {}", key, e);
                record.setResponseBody(String.valueOf(responsePayload));
            }
            record.setUpdatedAt(Instant.now());
            idempotencyRepository.save(record);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(String key, int statusCode, String message) {
        idempotencyRepository.findByIdempotencyKey(key).ifPresent(record -> {
            record.setStatus(IdempotencyStatus.FAILED);
            record.setResponseStatusCode(statusCode);
            record.setResponseBody(message);
            record.setUpdatedAt(Instant.now());
            idempotencyRepository.save(record);
        });
    }

    public <T> T executeIdempotently(
            String key,
            String target,
            Object requestPayload,
            Class<T> responseClass,
            Supplier<T> action
    ) {
        if (key == null || key.isBlank()) {
            return action.get();
        }

        String hash = computeHash(requestPayload);
        Optional<Idempotency> completed = checkAndLock(key, target, hash);

        if (completed.isPresent()) {
            Idempotency record = completed.get();
            try {
                return objectMapper.readValue(record.getResponseBody(), responseClass);
            } catch (JacksonException ex) {
                log.error("Failed to deserialize cached idempotent response for key {}", key, ex);
                throw new IllegalStateException("Failed to parse cached response for idempotency key: " + key, ex);
            }
        }

        try {
            T result = action.get();
            markCompleted(key, 200, result);
            return result;
        } catch (Exception ex) {
            markFailed(key, 500, ex.getMessage());
            throw ex;
        }
    }
}
