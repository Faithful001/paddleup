package com.king.paddleup.domain.comment;

import com.king.paddleup.domain.bid.BidRepository;
import com.king.paddleup.domain.comment.dto.CommentAuthorDto;
import com.king.paddleup.domain.comment.dto.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private static final String DELETED_CONTENT_PLACEHOLDER = "[This comment was deleted]";

    private final CommentRepository commentRepository;
    private final BidRepository bidRepository;

    public CommentResponse toResponse(Comment comment) {
        UUID auctionId = comment.getAuction().getId();
        UUID authorId = comment.getAuthor().getId();
        UUID sellerId = comment.getAuction().getSeller().getId();

        boolean isSeller = authorId.equals(sellerId);
        boolean isBidder = bidRepository.existsByBidderIdAndAuctionId(authorId, auctionId);

        CommentAuthorDto author = new CommentAuthorDto(
                authorId,
                comment.getAuthor().getUsername(),
                isSeller,
                isBidder
        );

        String displayContent = Boolean.TRUE.equals(comment.getIsDeleted())
                ? DELETED_CONTENT_PLACEHOLDER
                : comment.getContent();

        long replyCount = comment.getParent() == null
                ? commentRepository.countReplies(comment.getId())
                : 0;

        return new CommentResponse(
                comment.getId(),
                auctionId,
                author,
                displayContent,
                comment.getParent() != null ? comment.getParent().getId() : null,
                replyCount,
                comment.getIsDeleted(),
                comment.getCreatedAt()
        );
    }
}
