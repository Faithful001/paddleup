package com.king.paddleup.domain.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    @EntityGraph(attributePaths = "author")
    Page<Comment> findByAuctionIdAndParentIsNull(UUID auctionId, Pageable pageable);

    @EntityGraph(attributePaths = "author")
    List<Comment> findByParentIdOrderByCreatedAtAsc(UUID parentId);

    long countByAuctionIdAndIsDeletedFalse(UUID auctionId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.parent.id = :parentId")
    long countReplies(@Param("parentId") UUID parentId);
}
