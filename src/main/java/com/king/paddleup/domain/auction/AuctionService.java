package com.king.paddleup.domain.auction;

import com.king.paddleup.domain.auction.dto.CreateAuctionRequest;
import com.king.paddleup.domain.auction.dto.GetAuctionResponse;
import com.king.paddleup.domain.auction.dto.SaveAsDraftRequest;
import com.king.paddleup.domain.auction.dto.UpdateAuctionRequest;
import com.king.paddleup.domain.auction.enums.AuctionStatus;
import com.king.paddleup.domain.user.User;
import com.king.paddleup.domain.user.UserRepository;
import com.king.paddleup.shared.exception.AuctionNotFoundException;
import com.king.paddleup.shared.exception.AuctionNotUpdatableException;
import com.king.paddleup.shared.exception.UserIsSuspendedException;
import com.king.paddleup.shared.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuctionService {
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = false)
    public Auction create(UUID userId, CreateAuctionRequest payload) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Auction auction = Auction.builder()
                .title(payload.title())
                .description(payload.description())
                .startingPrice(payload.startingPrice())
                .reservePrice(payload.reservePrice())
                .minIncrement(payload.minIncrement())
                .status(payload.status())
                .media(payload.media())
                .seller(user)
                .endsAt(payload.endsAt())
                .build();

        return auctionRepository.save(auction);
    }

    @Transactional(readOnly = false)
    public Auction saveAsDraft(UUID userId, SaveAsDraftRequest payload) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Auction auction = Auction.builder()
                .title(payload.title())
                .description(payload.description())
                .startingPrice(payload.startingPrice())
                .reservePrice(payload.reservePrice())
                .minIncrement(payload.minIncrement())
                .status(payload.status())
                .media(payload.media())
                .seller(user)
                .endsAt(payload.endsAt())
                .build();

        return auctionRepository.save(auction);
    }

    @Transactional(readOnly = true)
    public Page<Auction> findMyAuctions(UUID userId, Pageable pageable) {
        return auctionRepository.findAllBySellerId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Auction> findAll(Pageable pageable) {
        return auctionRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Auction findById(UUID id) {
        return auctionRepository.findWithSellerById(id)
                .orElseThrow(() -> new AuctionNotFoundException("Auction not found"));
    }

    @Transactional
    public Auction update(UUID id, UpdateAuctionRequest payload) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new AuctionNotFoundException("Auction not found"));

        if (auction.getStatus() != AuctionStatus.DRAFT) {
            throw new AuctionNotUpdatableException(
                    "Auction cannot be updated because it is not a draft"
            );
        }

        auction.setTitle(payload.title());
        auction.setDescription(payload.description());
        auction.setStartingPrice(payload.startingPrice());
        auction.setReservePrice(payload.reservePrice());
        auction.setMinIncrement(payload.minIncrement());
        auction.setMedia(payload.media());
        auction.setEndsAt(payload.endsAt());

        return auction;
    }

    @Transactional(readOnly = true)
    public Page<Auction> findAuctionsBySellerId(UUID sellerId, boolean includeDrafts, Pageable pageable) {
        if (!userRepository.existsById(sellerId)) {
            throw new UserNotFoundException("User not found");
        }
        if (includeDrafts) {
            return auctionRepository.findAllBySellerId(sellerId, pageable);
        }
        return auctionRepository.findAllBySellerIdAndStatusNot(sellerId, AuctionStatus.DRAFT, pageable);
    }

    @Transactional(readOnly = true)
    public Auction findUserAuction(UUID userId, UUID auctionId, boolean includeDrafts) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }
        Auction auction = auctionRepository.findByIdAndSellerId(auctionId, userId)
                .orElseThrow(() -> new AuctionNotFoundException("Auction not found for user"));

        if (!includeDrafts && auction.getStatus() == AuctionStatus.DRAFT) {
            throw new AuctionNotFoundException("Auction not found for user");
        }

        return auction;
    }

    @Transactional(readOnly = true)
    public long countAuctionsBySeller(UUID sellerId, boolean includeDrafts) {
        if (includeDrafts) {
            return auctionRepository.countBySellerId(sellerId);
        }
        return auctionRepository.countBySellerIdAndStatusNot(sellerId, AuctionStatus.DRAFT);
    }
}
