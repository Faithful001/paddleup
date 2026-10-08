package com.king.paddleup.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT u.is_suspended FROM User WHERE a.id = :id")
    boolean isSuspended(@Param("id") UUID id);
}
