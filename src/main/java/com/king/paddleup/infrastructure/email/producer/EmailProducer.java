package com.king.paddleup.infrastructure.email.producer;

import com.king.paddleup.infrastructure.email.dto.EmailMessage;
import com.king.paddleup.infrastructure.rabbitmq.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendEmail(EmailMessage message) {
        log.info("Publishing email message to RabbitMQ for recipient: {}", message.to());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_EXCHANGE,
                RabbitMQConfig.EMAIL_ROUTING_KEY,
                message
        );
    }
}
