package com.king.paddleup.infrastructure.email;

import com.king.paddleup.shared.exception.EmailDeliveryException;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.SendEmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ResendEmailSender implements EmailSender {

    private final Resend resend;
    private final String from;

    public ResendEmailSender(Resend resend, @Value("${resend.from}") String from) {
        this.resend = resend;
        this.from = from;
    }

    @Override
    public void send(String to, String subject, String html) {
        SendEmailRequest request = SendEmailRequest.builder()
                .from(from)
                .to(to)
                .subject(subject)
                .html(html)
                .build();
        try {
            resend.emails().send(request);
        } catch (ResendException e) {
            throw new EmailDeliveryException("Failed to send email to " + to + " " + e.getCause().getMessage());
        }
    }
}