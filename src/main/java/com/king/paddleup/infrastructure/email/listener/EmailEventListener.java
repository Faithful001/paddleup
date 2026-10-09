package com.king.paddleup.infrastructure.email.listener;

import com.king.paddleup.infrastructure.email.dto.EmailMessage;
import com.king.paddleup.infrastructure.email.producer.EmailProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailEventListener {

    private final EmailProducer emailProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handleEmailEvent(EmailMessage emailMessage) {
        log.info("Transaction committed successfully. Forwarding email event to RabbitMQ for: {}", emailMessage.to());
        emailProducer.sendEmail(emailMessage);
    }
}
