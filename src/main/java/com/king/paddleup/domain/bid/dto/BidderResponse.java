package com.king.paddleup.domain.bid.dto;

import java.util.UUID;

public record BidderResponse(
        UUID id,
        String username,
        String firstName,
        String lastName
) {}
