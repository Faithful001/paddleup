package com.king.paddleup.domain.bid;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BidRepository extends JpaRepository<Bid, UUID> {
}
