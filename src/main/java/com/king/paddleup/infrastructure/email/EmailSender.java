package com.king.paddleup.infrastructure.email;

public interface EmailSender {
    void send(String to, String subject, String html);
}
