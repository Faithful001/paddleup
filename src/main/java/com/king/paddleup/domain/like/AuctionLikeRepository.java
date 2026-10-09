package com.king.paddleup.domain.like;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuctionLikeRepository extends JpaRepository<AuctionLike, UUID> {

    boolean existsByUserIdAndAuctionId(UUID userId, UUID auctionId);

    java.util.Optional<AuctionLike> findByUserIdAndAuctionId(UUID userId, UUID auctionId);

    void deleteByUserIdAndAuctionId(UUID userId, UUID auctionId);

    long countByAuctionId(UUID auctionId);

    @EntityGraph(attributePaths = "auction")
    Page<AuctionLike> findAllByUserId(UUID userId, Pageable pageable);
}
