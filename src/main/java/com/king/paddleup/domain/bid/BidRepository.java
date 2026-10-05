package com.king.paddleup.domain.bid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface BidRepository extends JpaRepository<Bid, UUID> {
        @Query("SELECT max(b.amount) from Bid b WHERE b.auction.id = :auctionId")
        Optional<BigDecimal> findHighestAmount(@Param("auctionId") UUID auctionId);
}
