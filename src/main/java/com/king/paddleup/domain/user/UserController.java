package com.king.paddleup.domain.user;

import com.king.paddleup.domain.auction.Auction;
import com.king.paddleup.domain.auction.AuctionMapper;
import com.king.paddleup.domain.auction.AuctionService;
import com.king.paddleup.domain.auction.dto.CreateAuctionRequest;
import com.king.paddleup.domain.auction.dto.GetAuctionResponse;
import com.king.paddleup.domain.followers.FollowerService;
import com.king.paddleup.domain.followers.dto.FollowUserResponse;
import com.king.paddleup.domain.user.dto.UserProfileResponse;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FollowerService followerService;
    private final AuctionService auctionService;
    private final AuctionMapper auctionMapper;

    @GetMapping("/me")
    public ResponseEntity<Response<UserProfileResponse>> getMyProfile(@AuthenticationPrincipal UUID currentUserId) {
        UserProfileResponse profile = userService.getMyProfile(currentUserId);
        return ResponseEntity.ok(Response.success(profile));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<UserProfileResponse>> getUserProfile(
            @AuthenticationPrincipal UUID currentUserId,
            @PathVariable UUID id
    ) {
        UserProfileResponse profile = userService.getUserProfile(currentUserId, id);
        return ResponseEntity.ok(Response.success(profile));
    }

    @GetMapping("/me/followers")
    public ResponseEntity<Response<PageResponse<FollowUserResponse>>> getMyFollowers(
            @AuthenticationPrincipal UUID currentUserId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<FollowUserResponse> followers = followerService.getFollowers(currentUserId, pageable);
        return ResponseEntity.ok(Response.success(PageResponse.from(followers)));
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<Response<PageResponse<FollowUserResponse>>> getUserFollowers(
            @PathVariable UUID id,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<FollowUserResponse> followers = followerService.getFollowers(id, pageable);
        return ResponseEntity.ok(Response.success(PageResponse.from(followers)));
    }

    @GetMapping("/me/following")
    public ResponseEntity<Response<PageResponse<FollowUserResponse>>> getMyFollowing(
            @AuthenticationPrincipal UUID currentUserId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<FollowUserResponse> following = followerService.getFollowing(currentUserId, pageable);
        return ResponseEntity.ok(Response.success(PageResponse.from(following)));
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<Response<PageResponse<FollowUserResponse>>> getUserFollowing(
            @PathVariable UUID id,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<FollowUserResponse> following = followerService.getFollowing(id, pageable);
        return ResponseEntity.ok(Response.success(PageResponse.from(following)));
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<Response<Void>> follow(
            @AuthenticationPrincipal UUID currentUserId,
            @PathVariable UUID id
    ) {
        followerService.follow(currentUserId, id);
        return ResponseEntity.ok(Response.message("User followed successfully"));
    }

    @DeleteMapping("/{id}/follow")
    public ResponseEntity<Response<Void>> unfollow(
            @AuthenticationPrincipal UUID currentUserId,
            @PathVariable UUID id
    ) {
        followerService.unfollow(currentUserId, id);
        return ResponseEntity.ok(Response.message("User unfollowed successfully"));
    }

    @PostMapping("/{id}/unfollow")
    public ResponseEntity<Response<Void>> unfollowAlias(
            @AuthenticationPrincipal UUID currentUserId,
            @PathVariable UUID id
    ) {
        followerService.unfollow(currentUserId, id);
        return ResponseEntity.ok(Response.message("User unfollowed successfully"));
    }

    @PostMapping("/me/auctions")
    public ResponseEntity<Response<GetAuctionResponse>> createPost(
            @AuthenticationPrincipal UUID currentUserId,
            @Valid @RequestBody CreateAuctionRequest payload
    ) {
        Auction auction = auctionService.create(currentUserId, payload);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Response.success("Auction post created successfully", auctionMapper.toGetResponse(auction)));
    }

    @GetMapping("/{id}/auctions")
    public ResponseEntity<Response<PageResponse<GetAuctionResponse>>> getUserAuctions(
            @PathVariable UUID id,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Auction> auctions = auctionService.findAuctionsBySellerId(id, false, pageable);
        return ResponseEntity.ok(Response.success(PageResponse.from(auctions.map(auctionMapper::toGetResponse))));
    }

    @GetMapping("/{userId}/auctions/{auctionId}")
    public ResponseEntity<Response<GetAuctionResponse>> getUserAuctionPost(
            @PathVariable UUID userId,
            @PathVariable UUID auctionId
    ) {
        Auction auction = auctionService.findUserAuction(userId, auctionId, false);
        return ResponseEntity.ok(Response.success(auctionMapper.toGetResponse(auction)));
    }
}
