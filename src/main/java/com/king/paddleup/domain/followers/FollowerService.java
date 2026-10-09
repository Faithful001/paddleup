package com.king.paddleup.domain.followers;

import com.king.paddleup.domain.followers.dto.FollowUserResponse;
import com.king.paddleup.domain.user.User;
import com.king.paddleup.domain.user.UserRepository;
import com.king.paddleup.shared.exception.CannotFollowSelfException;
import com.king.paddleup.shared.exception.UserIsSuspendedException;
import com.king.paddleup.shared.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FollowerService {

    private final FollowerRepository followerRepository;
    private final UserRepository userRepository;

    @Transactional
    public void follow(UUID currentUserId, UUID targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new CannotFollowSelfException("You cannot follow yourself");
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (Boolean.TRUE.equals(targetUser.getIsSuspended())) {
            throw new UserIsSuspendedException("User is suspended");
        }

        if (followerRepository.existsByFollowerIdAndFollowingId(currentUserId, targetUserId)) {
            return;
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Follower follower = Follower.builder()
                .follower(currentUser)
                .following(targetUser)
                .build();

        followerRepository.save(follower);
    }

    @Transactional
    public void unfollow(UUID currentUserId, UUID targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new CannotFollowSelfException("You cannot unfollow yourself");
        }

        if (!userRepository.existsById(targetUserId)) {
            throw new UserNotFoundException("User not found");
        }

        followerRepository.deleteByFollowerIdAndFollowingId(currentUserId, targetUserId);
    }

    @Transactional(readOnly = true)
    public Page<FollowUserResponse> getFollowers(UUID targetUserId, Pageable pageable) {
        if (!userRepository.existsById(targetUserId)) {
            throw new UserNotFoundException("User not found");
        }

        return followerRepository.findAllByFollowingId(targetUserId, pageable)
                .map(f -> new FollowUserResponse(
                        f.getFollower().getId(),
                        f.getFollower().getUsername(),
                        f.getFollower().getFirstName(),
                        f.getFollower().getLastName(),
                        f.getCreatedAt()
                ));
    }

    @Transactional(readOnly = true)
    public Page<FollowUserResponse> getFollowing(UUID targetUserId, Pageable pageable) {
        if (!userRepository.existsById(targetUserId)) {
            throw new UserNotFoundException("User not found");
        }

        return followerRepository.findAllByFollowerId(targetUserId, pageable)
                .map(f -> new FollowUserResponse(
                        f.getFollowing().getId(),
                        f.getFollowing().getUsername(),
                        f.getFollowing().getFirstName(),
                        f.getFollowing().getLastName(),
                        f.getCreatedAt()
                ));
    }

    @Transactional(readOnly = true)
    public long getFollowersCount(UUID userId) {
        return followerRepository.countByFollowingId(userId);
    }

    @Transactional(readOnly = true)
    public long getFollowingCount(UUID userId) {
        return followerRepository.countByFollowerId(userId);
    }

    @Transactional(readOnly = true)
    public boolean isFollowing(UUID followerId, UUID followingId) {
        if (followerId == null || followingId == null) {
            return false;
        }
        return followerRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }
}
