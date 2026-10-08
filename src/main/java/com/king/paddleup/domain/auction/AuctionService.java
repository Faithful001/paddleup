package com.king.paddleup.domain.auction;

import com.king.paddleup.domain.auction.dto.CreateAuctionRequest;
import com.king.paddleup.domain.auction.dto.GetAuctionResponse;
import com.king.paddleup.domain.user.User;
import com.king.paddleup.domain.user.UserRepository;
import com.king.paddleup.shared.exception.AuctionNotFoundException;
import com.king.paddleup.shared.exception.UserIsSuspendedException;
import com.king.paddleup.shared.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuctionService {
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
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
                .imageUrls(payload.imageUrls())
                .seller(user)
                .endsAt(payload.endsAt())
                .build();

        return auctionRepository.save(auction);
    }

    @Transactional(readOnly = true)
    public List<Auction> findAll() {
        return auctionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Auction findById(UUID id) {
        return auctionRepository.findWithSellerById(id)
                .orElseThrow(() -> new AuctionNotFoundException("Auction not found"));
    }


}
