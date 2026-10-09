package com.king.paddleup.domain.like;

import com.king.paddleup.domain.auction.Auction;
import com.king.paddleup.domain.auction.AuctionRepository;
import com.king.paddleup.domain.auction.enums.AuctionStatus;
import com.king.paddleup.domain.like.dto.AuctionLikedEvent;
import com.king.paddleup.domain.like.dto.AuctionLikeResponse;
import com.king.paddleup.domain.user.User;
import com.king.paddleup.domain.user.UserRepository;
import com.king.paddleup.shared.exception.AuctionNotFoundException;
import com.king.paddleup.shared.exception.InvalidLikeOperationException;
import com.king.paddleup.shared.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuctionLikeService {

    private final AuctionLikeRepository auctionLikeRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher events;

    @Transactional
    public AuctionLikeResponse like(UUID auctionId, UUID userId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException("Auction not found"));

        if (auction.getStatus() == AuctionStatus.DRAFT) {
            throw new InvalidLikeOperationException("Cannot like a draft auction");
        }

        if (auction.getSeller().getId().equals(userId)) {
            throw new InvalidLikeOperationException("You cannot like your own auction");
        }

        if (auctionLikeRepository.existsByUserIdAndAuctionId(userId, auctionId)) {
            AuctionLike existing = auctionLikeRepository.findByUserIdAndAuctionId(userId, auctionId)
                    .orElse(null);
            long totalLikes = auctionLikeRepository.countByAuctionId(auctionId);

            return new AuctionLikeResponse(
                    existing != null ? existing.getId() : null,
                    auctionId,
                    userId,
                    existing != null ? existing.getCreatedAt() : null,
                    totalLikes
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        AuctionLike auctionLike = AuctionLike.builder()
                .user(user)
                .auction(auction)
                .build();

        AuctionLike saved = auctionLikeRepository.save(auctionLike);
        long totalLikes = auctionLikeRepository.countByAuctionId(auctionId);

        events.publishEvent(new AuctionLikedEvent(
                auctionId,
                auction.getTitle(),
                user.getId(),
                user.getUsername(),
                auction.getSeller().getId(),
                saved.getCreatedAt()
        ));

        return new AuctionLikeResponse(
                saved.getId(),
                auctionId,
                userId,
                saved.getCreatedAt(),
                totalLikes
        );
    }

    @Transactional
    public void unlike(UUID auctionId, UUID userId) {
        if (!auctionRepository.existsById(auctionId)) {
            throw new AuctionNotFoundException("Auction not found");
        }

        auctionLikeRepository.deleteByUserIdAndAuctionId(userId, auctionId);
    }

    @Transactional(readOnly = true)
    public long countByAuction(UUID auctionId) {
        if (!auctionRepository.existsById(auctionId)) {
            throw new AuctionNotFoundException("Auction not found");
        }

        return auctionLikeRepository.countByAuctionId(auctionId);
    }

    @Transactional(readOnly = true)
    public boolean isLikedByUser(UUID auctionId, UUID userId) {
        return auctionLikeRepository.existsByUserIdAndAuctionId(userId, auctionId);
    }

    @Transactional(readOnly = true)
    public Page<AuctionLike> findLikedByUser(UUID userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        return auctionLikeRepository.findAllByUserId(userId, pageable);
    }
}
