package com.king.paddleup.domain.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID> {

    Optional<Token> findByToken(String token);

    Optional<Token> findByTokenAndTokenType(String token, TokenType tokenType);

    @Query("SELECT t FROM Token t WHERE t.user.id = :userId AND t.revoked = false AND t.expired = false")
    List<Token> findAllValidTokensByUserId(@Param("userId") UUID userId);

    @Query("SELECT t FROM Token t WHERE t.user.id = :userId AND t.tokenType = :tokenType AND t.revoked = false AND t.expired = false")
    List<Token> findAllValidTokensByUserIdAndTokenType(@Param("userId") UUID userId, @Param("tokenType") TokenType tokenType);

    @Modifying
    @Query("UPDATE Token t SET t.revoked = true, t.expired = true WHERE t.user.id = :userId")
    void revokeAllUserTokens(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE Token t SET t.revoked = true, t.expired = true WHERE t.user.id = :userId AND t.tokenType = :tokenType")
    void revokeAllUserTokensByType(@Param("userId") UUID userId, @Param("tokenType") TokenType tokenType);
}
