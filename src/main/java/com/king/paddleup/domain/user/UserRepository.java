package com.king.paddleup.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT u.isSuspended FROM User u WHERE u.id = :id")
    boolean isSuspended(@Param("id") UUID id);
}
