package com.king.paddleup.domain.user;

import com.king.paddleup.domain.auction.AuctionService;
import com.king.paddleup.domain.followers.FollowerService;
import com.king.paddleup.domain.user.dto.UserProfileResponse;
import com.king.paddleup.shared.exception.UserIsSuspendedException;
import com.king.paddleup.shared.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FollowerService followerService;

    @Mock
    private AuctionService auctionService;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private UUID otherUserId;
    private User user;
    private User otherUser;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .username("faithful")
                .firstName("Faith")
                .lastName("Dev")
                .email("faith@example.com")
                .isEmailVerified(true)
                .build();

        otherUser = User.builder()
                .id(otherUserId)
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .isEmailVerified(false)
                .build();
    }

    @Test
    void getMyProfile_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(followerService.getFollowersCount(userId)).thenReturn(10L);
        when(followerService.getFollowingCount(userId)).thenReturn(5L);
        when(auctionService.countAuctionsBySeller(userId, true)).thenReturn(3L);

        UserProfileResponse response = userService.getMyProfile(userId);

        assertThat(response.id()).isEqualTo(userId);
        assertThat(response.username()).isEqualTo("faithful");
        assertThat(response.email()).isEqualTo("faith@example.com");
        assertThat(response.isEmailVerified()).isTrue();
        assertThat(response.followersCount()).isEqualTo(10L);
        assertThat(response.followingCount()).isEqualTo(5L);
        assertThat(response.auctionsCount()).isEqualTo(3L);
        assertThat(response.isFollowing()).isNull();
    }

    @Test
    void getUserProfile_Success_NotFollowing() {
        when(userRepository.findById(otherUserId)).thenReturn(Optional.of(otherUser));
        when(followerService.getFollowersCount(otherUserId)).thenReturn(20L);
        when(followerService.getFollowingCount(otherUserId)).thenReturn(15L);
        when(auctionService.countAuctionsBySeller(otherUserId, false)).thenReturn(7L);
        when(followerService.isFollowing(userId, otherUserId)).thenReturn(false);

        UserProfileResponse response = userService.getUserProfile(userId, otherUserId);

        assertThat(response.id()).isEqualTo(otherUserId);
        assertThat(response.username()).isEqualTo("john_doe");
        assertThat(response.email()).isNull(); // Hidden for privacy
        assertThat(response.followersCount()).isEqualTo(20L);
        assertThat(response.followingCount()).isEqualTo(15L);
        assertThat(response.auctionsCount()).isEqualTo(7L);
        assertThat(response.isFollowing()).isFalse();
    }

    @Test
    void getUserProfile_Success_Following() {
        when(userRepository.findById(otherUserId)).thenReturn(Optional.of(otherUser));
        when(followerService.getFollowersCount(otherUserId)).thenReturn(20L);
        when(followerService.getFollowingCount(otherUserId)).thenReturn(15L);
        when(auctionService.countAuctionsBySeller(otherUserId, false)).thenReturn(7L);
        when(followerService.isFollowing(userId, otherUserId)).thenReturn(true);

        UserProfileResponse response = userService.getUserProfile(userId, otherUserId);

        assertThat(response.isFollowing()).isTrue();
    }

    @Test
    void getUserProfile_WhenViewingSelf_ReturnsMyProfile() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(followerService.getFollowersCount(userId)).thenReturn(10L);
        when(followerService.getFollowingCount(userId)).thenReturn(5L);
        when(auctionService.countAuctionsBySeller(userId, true)).thenReturn(3L);

        UserProfileResponse response = userService.getUserProfile(userId, userId);

        assertThat(response.email()).isEqualTo("faith@example.com");
        assertThat(response.isFollowing()).isNull();
    }

    @Test
    void getUserProfile_WhenSuspended_ThrowsException() {
        otherUser.setIsSuspended(true);
        when(userRepository.findById(otherUserId)).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> userService.getUserProfile(userId, otherUserId))
                .isInstanceOf(UserIsSuspendedException.class);
    }

    @Test
    void getMyProfile_NotFound_ThrowsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getMyProfile(userId))
                .isInstanceOf(UserNotFoundException.class);
    }
}
