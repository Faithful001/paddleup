package com.king.paddleup.domain.user;

import com.king.paddleup.domain.auction.AuctionService;
import com.king.paddleup.domain.followers.FollowerService;
import com.king.paddleup.domain.user.dto.UserProfileResponse;
import com.king.paddleup.shared.exception.UserIsSuspendedException;
import com.king.paddleup.shared.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FollowerService followerService;
    private final AuctionService auctionService;

    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(UUID currentUserId) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        long followersCount = followerService.getFollowersCount(currentUserId);
        long followingCount = followerService.getFollowingCount(currentUserId);
        long auctionsCount = auctionService.countAuctionsBySeller(currentUserId, true);

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getIsEmailVerified(),
                user.getCreatedAt(),
                followersCount,
                followingCount,
                auctionsCount,
                null
        );
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(UUID currentUserId, UUID targetUserId) {
        if (currentUserId != null && currentUserId.equals(targetUserId)) {
            return getMyProfile(currentUserId);
        }

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (Boolean.TRUE.equals(user.getIsSuspended())) {
            throw new UserIsSuspendedException("User is suspended");
        }

        long followersCount = followerService.getFollowersCount(targetUserId);
        long followingCount = followerService.getFollowingCount(targetUserId);
        long auctionsCount = auctionService.countAuctionsBySeller(targetUserId, false);
        boolean isFollowing = currentUserId != null && followerService.isFollowing(currentUserId, targetUserId);

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                null,
                null,
                user.getCreatedAt(),
                followersCount,
                followingCount,
                auctionsCount,
                isFollowing
        );
    }

    @Transactional(readOnly = true)
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
