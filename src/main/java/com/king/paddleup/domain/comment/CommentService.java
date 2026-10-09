package com.king.paddleup.domain.comment;

import com.king.paddleup.domain.auction.Auction;
import com.king.paddleup.domain.auction.AuctionRepository;
import com.king.paddleup.domain.auction.enums.AuctionStatus;
import com.king.paddleup.domain.comment.dto.CommentCreatedEvent;
import com.king.paddleup.domain.comment.dto.CreateCommentRequest;
import com.king.paddleup.domain.user.User;
import com.king.paddleup.domain.user.UserRepository;
import com.king.paddleup.shared.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher events;

    @Transactional
    public Comment create(UUID auctionId, UUID userId, CreateCommentRequest payload) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException("Auction not found"));

        if (auction.getStatus() == AuctionStatus.DRAFT) {
            throw new InvalidCommentOperationException("Cannot comment on a draft auction");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (Boolean.TRUE.equals(user.getIsSuspended())) {
            throw new UserIsSuspendedException("Suspended users cannot post comments");
        }

        Comment parent = null;
        if (payload.parentId() != null) {
            parent = commentRepository.findById(payload.parentId())
                    .orElseThrow(() -> new CommentNotFoundException("Parent comment not found"));

            if (!parent.getAuction().getId().equals(auctionId)) {
                throw new InvalidCommentOperationException("Parent comment does not belong to this auction");
            }

            if (parent.getParent() != null) {
                throw new InvalidCommentOperationException("Replies can only be one level deep");
            }
        }

        Comment comment = Comment.builder()
                .auction(auction)
                .author(user)
                .parent(parent)
                .content(payload.content())
                .build();

        Comment saved = commentRepository.save(comment);

        UUID sellerId = auction.getSeller().getId();

        CommentCreatedEvent event = new CommentCreatedEvent(
                auctionId,
                saved.getId(),
                userId,
                user.getUsername(),
                saved.getContent(),
                payload.parentId(),
                userId.equals(sellerId),
                saved.getCreatedAt()
        );

        events.publishEvent(event);

        return saved;
    }

    @Transactional(readOnly = true)
    public Page<Comment> findTopLevelComments(UUID auctionId, Pageable pageable) {
        if (!auctionRepository.existsById(auctionId)) {
            throw new AuctionNotFoundException("Auction not found");
        }

        return commentRepository.findByAuctionIdAndParentIsNull(auctionId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Comment> findReplies(UUID auctionId, UUID commentId) {
        if (!auctionRepository.existsById(auctionId)) {
            throw new AuctionNotFoundException("Auction not found");
        }

        Comment parent = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));

        if (!parent.getAuction().getId().equals(auctionId)) {
            throw new CommentNotFoundException("Comment not found in this auction");
        }

        return commentRepository.findByParentIdOrderByCreatedAtAsc(commentId);
    }

    @Transactional
    public void delete(UUID auctionId, UUID commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));

        if (!comment.getAuction().getId().equals(auctionId)) {
            throw new CommentNotFoundException("Comment not found in this auction");
        }

        UUID authorId = comment.getAuthor().getId();
        UUID sellerId = comment.getAuction().getSeller().getId();

        if (!authorId.equals(userId) && !sellerId.equals(userId)) {
            throw new UserUnauthorizedException("You are not authorized to delete this comment");
        }

        // Soft delete: keep the node so replies aren't orphaned
        comment.setIsDeleted(true);
        comment.setContent("");
        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public long countByAuction(UUID auctionId) {
        return commentRepository.countByAuctionIdAndIsDeletedFalse(auctionId);
    }
}
