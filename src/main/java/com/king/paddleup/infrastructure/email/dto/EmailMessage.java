package com.king.paddleup.infrastructure.email.dto;

import java.io.Serializable;

public record EmailMessage(
        String to,
        String subject,
        String html
) implements Serializable {}
