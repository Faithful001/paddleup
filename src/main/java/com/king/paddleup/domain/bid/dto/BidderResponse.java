package com.king.paddleup.domain.bid.dto;

import org.hibernate.validator.constraints.UUID;

public record BidderResponse(
        UUID id,
        String username,
        String firstName,
        String lastName
) {}
