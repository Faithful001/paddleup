package com.king.paddleup.domain.bid;

import com.king.paddleup.domain.bid.dto.CreateBidRequest;
import com.king.paddleup.domain.bid.dto.CreateBidResponse;
import com.king.paddleup.shared.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bids")
@RequiredArgsConstructor
public class BidController {
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
                .body(Response.success(bidMapper.toResponse(bid)));
    }
}
