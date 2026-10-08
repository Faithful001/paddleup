package com.king.paddleup.domain.auction;

import com.king.paddleup.domain.auction.dto.CreateAuctionRequest;
import com.king.paddleup.domain.auction.dto.GetAuctionResponse;
import com.king.paddleup.domain.bid.Bid;
import com.king.paddleup.domain.bid.BidMapper;
import com.king.paddleup.domain.bid.BidService;
import com.king.paddleup.domain.bid.dto.CreateBidRequest;
import com.king.paddleup.domain.bid.dto.CreateBidResponse;
import com.king.paddleup.domain.bid.dto.GetBidResponse;
import com.king.paddleup.infrastructure.sse.BidSseService;
import com.king.paddleup.shared.response.PageResponse;
import com.king.paddleup.shared.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auctions")
public class AuctionController {

    private final BidService bidService;
    private final BidMapper bidMapper;
    private final AuctionService auctionService;
    private final AuctionMapper auctionMapper;
    private final BidSseService bidSseService;

    @PostMapping
    public ResponseEntity<Response<GetAuctionResponse>> create(@AuthenticationPrincipal UUID id, @Valid @RequestBody CreateAuctionRequest payload) {
        Auction auction = auctionService.create(id, payload);

        return ResponseEntity.ok(
        Response.success(auctionMapper.toGetResponse(auction))
        );
    }

    @GetMapping
    public ResponseEntity<Response<PageResponse<GetAuctionResponse>>> getAll(
            @PageableDefault(size = 10, sort = "createAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<Auction> auction = auctionService.findAll(pageable);

        return ResponseEntity.ok(
        Response.success(PageResponse.from(auction.map(auctionMapper::toGetResponse)))
        );
    }

    @GetMapping("/me")
    public ResponseEntity<Response<PageResponse<GetAuctionResponse>>> getMyAuctions(
            @AuthenticationPrincipal UUID userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<Auction> auction = auctionService.findMyAuctions(userId, pageable);

        return ResponseEntity.ok(
                Response.success(PageResponse.from(auction.map(auctionMapper::toGetResponse)))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<GetAuctionResponse>> getById(@PathVariable UUID id) {
        Auction auction = auctionService.findById(id);

        return ResponseEntity.ok(
        Response.success(auctionMapper.toGetResponse(auction))
        );
    }

    @PostMapping("/{id}/bids")
    public ResponseEntity<Response<CreateBidResponse>> createBid(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID id,
            @Valid @RequestBody CreateBidRequest request) {

        Bid bid = bidService.create(request, id, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Response.success(bidMapper.toCreateResponse(bid)));
    }

    @GetMapping("/{id}/bids")
    public ResponseEntity<Response<PageResponse<GetBidResponse>>> findAllBids(
            @PathVariable UUID id,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
            ) {

        Page<Bid> bids = bidService.findByAuctionId(id, pageable);

        return ResponseEntity.ok(
                Response.success(PageResponse.from(bids.map(bidMapper::toGetResponse))));
    }

    @GetMapping(value = "/{id}/bids/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamBids(@PathVariable UUID id) {
        return bidSseService.subscribe(id);
    }
}
