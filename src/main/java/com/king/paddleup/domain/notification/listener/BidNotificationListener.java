package com.king.paddleup.domain.notification.listener;

import com.king.paddleup.domain.auction.Auction;
import com.king.paddleup.domain.auction.AuctionRepository;
import com.king.paddleup.domain.bid.Bid;
import com.king.paddleup.domain.bid.BidRepository;
import com.king.paddleup.domain.bid.dto.BidPlacedEvent;
import com.king.paddleup.domain.notification.NotificationService;
import com.king.paddleup.domain.notification.NotificationType;
import com.king.paddleup.domain.user.User;
import com.king.paddleup.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class BidNotificationListener {

    private final NotificationService notificationService;
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onBidPlaced(BidPlacedEvent event) {
        Optional<Auction> auctionOpt = auctionRepository.findById(event.auctionId());
        Optional<User> bidderOpt = userRepository.findById(event.bidderId());

        if (auctionOpt.isEmpty() || bidderOpt.isEmpty()) {
            log.warn("Could not send bid notification: auction or bidder not found for event {}", event);
            return;
        }

        Auction auction = auctionOpt.get();
        User bidder = bidderOpt.get();

        // 1. Notify the seller of the auction
        notificationService.create(
                auction.getSeller().getId(),
                NotificationType.BID_PLACED,
                "New bid on your auction",
                "@" + bidder.getUsername() + " placed a bid of $" + event.bidAmount() + " on '" + auction.getTitle() + "'",
                auction.getId()
        );

        // 2. Notify the previous highest bidder if they were outbid
        bidRepository.findFirstByAuctionIdAndIdNotOrderByAmountDesc(event.auctionId(), event.bidId())
                .ifPresent(prevBid -> {
                    if (!prevBid.getBidder().getId().equals(event.bidderId())) {
                        notificationService.create(
                                prevBid.getBidder().getId(),
                                NotificationType.OUTBID,
                                "You've been outbid",
                                "You've been outbid on '" + auction.getTitle() + "'. Current bid: $" + event.bidAmount(),
                                auction.getId()
                        );
                    }
                });
    }
}
