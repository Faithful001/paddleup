package com.king.paddleup.infrastructure.email.consumer;

import com.king.paddleup.infrastructure.email.EmailSender;
import com.king.paddleup.infrastructure.email.dto.EmailMessage;
import com.king.paddleup.infrastructure.rabbitmq.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailConsumer {

    private final EmailSender emailSender;

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void consumeEmailMessage(EmailMessage message) {
        log.info("Received email task from RabbitMQ for recipient: {}", message.to());
        try {
            emailSender.send(message.to(), message.subject(), message.html());
            log.info("Email successfully sent to: {}", message.to());
        } catch (Exception e) {
            log.error("Failed to process email message for recipient {}: {}", message.to(), e.getMessage());
            throw e;
        }
    }
}
