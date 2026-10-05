package com.king.paddleup.domain.auction;

import com.king.paddleup.domain.bid.BidService;
import com.king.paddleup.domain.bid.dto.CreateBidRequest;
import com.king.paddleup.shared.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auctions")
public class AuctionController {
    private final BidService bidService;

    @PostMapping("/{id}/bids")
    public ResponseEntity<Response<BidResponse>> create(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID id,
            @Valid @RequestBody CreateBidRequest request) {

        Bid bid = bidService.create(request, id, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Response.success(BidResponse.from(bid)));
    }
}
