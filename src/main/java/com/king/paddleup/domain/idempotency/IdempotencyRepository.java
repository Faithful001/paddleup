package com.king.paddleup.domain.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IdempotencyRepository extends JpaRepository<Idempotency, UUID> {
    Optional<Idempotency> findByIdempotencyKey(String idempotencyKey);
}
