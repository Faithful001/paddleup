package com.king.paddleup.domain.payment.repository;

import com.king.paddleup.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByReference(String reference);
    Optional<Payment> findByPaymentId(String paymentId);
}
