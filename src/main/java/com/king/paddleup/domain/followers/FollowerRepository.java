package com.king.paddleup.domain.followers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FollowerRepository extends JpaRepository<Follower, UUID> {

    boolean existsByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

    Optional<Follower> findByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

    void deleteByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

    long countByFollowingId(UUID followingId);

    long countByFollowerId(UUID followerId);

    @EntityGraph(attributePaths = {"follower"})
    Page<Follower> findAllByFollowingId(UUID followingId, Pageable pageable);

    @EntityGraph(attributePaths = {"following"})
    Page<Follower> findAllByFollowerId(UUID followerId, Pageable pageable);
}
