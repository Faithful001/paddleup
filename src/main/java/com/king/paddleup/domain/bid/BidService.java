package com.king.paddleup.domain.bid;

import com.king.paddleup.domain.auction.Auction;
import com.king.paddleup.domain.auction.AuctionRepository;
import com.king.paddleup.domain.auction.enums.AuctionStatus;
import com.king.paddleup.domain.bid.dto.BidPlacedEvent;
import com.king.paddleup.domain.bid.dto.CreateBidRequest;
import com.king.paddleup.domain.user.User;
import com.king.paddleup.domain.user.UserRepository;
import com.king.paddleup.shared.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BidService {
    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher events;

    @Transactional
    public Bid create(CreateBidRequest payload, UUID auctionId, UUID bidderId) {
        Auction auction = auctionRepository.findByIdForUpdate(auctionId)
                .orElseThrow(()-> new AuctionNotFoundException("Auction not found"));

        User user = userRepository.findById(bidderId)
                .orElseThrow(()-> new UserNotFoundException("User not found"));

        if (auction.getStatus() != AuctionStatus.ACTIVE) {
            throw new AuctionClosedException("Auction is closed");
        }

        if (auction.getSeller().getId().equals(bidderId)) {
            throw new InvalidBidException("You cannot bid on your own auction");
        }

        BigDecimal minimumBid = bidRepository.findHighestAmount(auctionId)
                .map(highest -> highest.add(auction.getMinIncrement()))
                .orElse(auction.getStartingPrice());

        if (payload.amount().compareTo(minimumBid) < 0) {
            throw new BidTooLowException("Bid must be at least " + minimumBid);
        }

        Bid builtBid = Bid.builder()
                .auction(auction)
                .bidder(user)
                .amount(payload.amount())
                .build();

        Bid bid = bidRepository.save(builtBid);
        BidPlacedEvent bidPlacedEvent = new BidPlacedEvent(
                auctionId,
                bid.getId(),
                bidderId,
                bid.getAmount(),
                bid.getCreatedAt());

        events.publishEvent(bidPlacedEvent);

        return bid;
    }

    @Transactional(readOnly = true)
    public Page<Bid> findByAuctionId(UUID auctionId, Pageable pageable) {
        if (!auctionRepository.existsById(auctionId)) {
            throw new AuctionNotFoundException("Auction not found");
        }

        return bidRepository.findByAuctionId(auctionId, pageable);
    }
}
