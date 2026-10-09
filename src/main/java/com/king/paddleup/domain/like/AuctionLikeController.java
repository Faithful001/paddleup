package com.king.paddleup.domain.like;

import com.king.paddleup.domain.like.dto.AuctionLikeResponse;
import com.king.paddleup.shared.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auctions/{auctionId}/likes")
public class AuctionLikeController {

    private final AuctionLikeService auctionLikeService;

    @PostMapping
    public ResponseEntity<Response<AuctionLikeResponse>> like(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID auctionId
    ) {
        AuctionLikeResponse response = auctionLikeService.like(auctionId, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Response.success(response));
    }

    @DeleteMapping
    public ResponseEntity<Response<Void>> unlike(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID auctionId
    ) {
        auctionLikeService.unlike(auctionId, userId);

        return ResponseEntity.ok(Response.message("Auction unliked successfully"));
    }

    @GetMapping("/count")
    public ResponseEntity<Response<Long>> getLikeCount(@PathVariable UUID auctionId) {
        long count = auctionLikeService.countByAuction(auctionId);

        return ResponseEntity.ok(Response.success(count));
    }

    @GetMapping("/status")
    public ResponseEntity<Response<Boolean>> isLiked(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID auctionId
    ) {
        boolean liked = auctionLikeService.isLikedByUser(auctionId, userId);

        return ResponseEntity.ok(Response.success(liked));
    }
}
