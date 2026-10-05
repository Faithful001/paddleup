package com.king.paddleup.domain.auction;

import com.king.paddleup.domain.bid.Bid;
import com.king.paddleup.domain.bid.BidMapper;
import com.king.paddleup.domain.bid.BidService;
import com.king.paddleup.domain.bid.dto.CreateBidRequest;
import com.king.paddleup.domain.bid.dto.CreateBidResponse;
import com.king.paddleup.domain.bid.dto.GetBidResponse;
import com.king.paddleup.shared.response.PageResponse;
import com.king.paddleup.shared.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auctions")
public class AuctionController {

    private final BidService bidService;
    private final BidMapper bidMapper;

    @PostMapping("/{id}/bids")
    public ResponseEntity<Response<CreateBidResponse>> create(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID id,
            @Valid @RequestBody CreateBidRequest request) {

        Bid bid = bidService.create(request, id, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Response.success(bidMapper.toCreateResponse(bid)));
    }

    @GetMapping("/{id}/bids")
    public ResponseEntity<Response<PageResponse<GetBidResponse>>> findAll(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID id,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
            ) {

        Page<Bid> bids = bidService.findByAuctionId(id, pageable);

        return ResponseEntity.ok(
                Response.success(PageResponse.from(bids.map(bidMapper::toGetResponse))));
    }
}
