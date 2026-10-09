package com.king.paddleup.domain.followers;

import com.king.paddleup.domain.followers.dto.FollowUserResponse;
import com.king.paddleup.domain.user.User;
import com.king.paddleup.domain.user.UserRepository;
import com.king.paddleup.shared.exception.CannotFollowSelfException;
import com.king.paddleup.shared.exception.UserIsSuspendedException;
import com.king.paddleup.shared.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowerServiceTest {

    @Mock
    private FollowerRepository followerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FollowerService followerService;

    private UUID currentUserId;
    private UUID targetUserId;
    private User currentUser;
    private User targetUser;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        targetUserId = UUID.randomUUID();

        currentUser = User.builder()
                .id(currentUserId)
                .username("currentUser")
                .firstName("Current")
                .lastName("User")
                .email("current@example.com")
                .build();

        targetUser = User.builder()
                .id(targetUserId)
                .username("targetUser")
                .firstName("Target")
                .lastName("User")
                .email("target@example.com")
                .build();
    }

    @Test
    void follow_Success() {
        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));
        when(followerRepository.existsByFollowerIdAndFollowingId(currentUserId, targetUserId)).thenReturn(false);
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));

        followerService.follow(currentUserId, targetUserId);

        verify(followerRepository).save(any(Follower.class));
    }

    @Test
    void follow_CannotFollowSelf() {
        assertThatThrownBy(() -> followerService.follow(currentUserId, currentUserId))
                .isInstanceOf(CannotFollowSelfException.class)
                .hasMessage("You cannot follow yourself");

        verifyNoInteractions(followerRepository);
    }

    @Test
    void follow_TargetNotFound() {
        when(userRepository.findById(targetUserId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> followerService.follow(currentUserId, targetUserId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");

        verify(followerRepository, never()).save(any());
    }

    @Test
    void follow_TargetSuspended() {
        targetUser.setIsSuspended(true);
        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));

        assertThatThrownBy(() -> followerService.follow(currentUserId, targetUserId))
                .isInstanceOf(UserIsSuspendedException.class);

        verify(followerRepository, never()).save(any());
    }

    @Test
    void follow_AlreadyFollowing_IsIdempotent() {
        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));
        when(followerRepository.existsByFollowerIdAndFollowingId(currentUserId, targetUserId)).thenReturn(true);

        followerService.follow(currentUserId, targetUserId);

        verify(followerRepository, never()).save(any());
    }

    @Test
    void unfollow_Success() {
        when(userRepository.existsById(targetUserId)).thenReturn(true);

        followerService.unfollow(currentUserId, targetUserId);

        verify(followerRepository).deleteByFollowerIdAndFollowingId(currentUserId, targetUserId);
    }

    @Test
    void unfollow_CannotUnfollowSelf() {
        assertThatThrownBy(() -> followerService.unfollow(currentUserId, currentUserId))
                .isInstanceOf(CannotFollowSelfException.class);
    }

    @Test
    void getFollowers_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Follower follower = Follower.builder()
                .id(UUID.randomUUID())
                .follower(currentUser)
                .following(targetUser)
                .createdAt(Instant.now())
                .build();

        when(userRepository.existsById(targetUserId)).thenReturn(true);
        when(followerRepository.findAllByFollowingId(targetUserId, pageable))
                .thenReturn(new PageImpl<>(List.of(follower)));

        Page<FollowUserResponse> result = followerService.getFollowers(targetUserId, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).username()).isEqualTo("currentUser");
    }

    @Test
    void getFollowing_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Follower follower = Follower.builder()
                .id(UUID.randomUUID())
                .follower(currentUser)
                .following(targetUser)
                .createdAt(Instant.now())
                .build();

        when(userRepository.existsById(currentUserId)).thenReturn(true);
        when(followerRepository.findAllByFollowerId(currentUserId, pageable))
                .thenReturn(new PageImpl<>(List.of(follower)));

        Page<FollowUserResponse> result = followerService.getFollowing(currentUserId, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).username()).isEqualTo("targetUser");
    }
}
