package com.king.paddleup.domain.bid.dto;

import java.math.BigDecimal;

public record CreateBidRequest(
        BigDecimal amount
) {
}
