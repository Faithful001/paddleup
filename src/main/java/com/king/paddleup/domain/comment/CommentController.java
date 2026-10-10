package com.king.paddleup.domain.comment;

import com.king.paddleup.domain.comment.dto.CommentResponse;
import com.king.paddleup.domain.comment.dto.CreateCommentRequest;
import com.king.paddleup.infrastructure.sse.CommentSseService;
import com.king.paddleup.shared.response.PageResponse;
import com.king.paddleup.shared.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auctions/{auctionId}/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final CommentSseService commentSseService;

    @PostMapping
    public ResponseEntity<Response<CommentResponse>> create(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID auctionId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        Comment comment = commentService.create(auctionId, userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Response.success(commentMapper.toResponse(comment)));
    }

    @GetMapping
    public ResponseEntity<Response<PageResponse<CommentResponse>>> getTopLevelComments(
            @PathVariable UUID auctionId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<Comment> comments = commentService.findTopLevelComments(auctionId, pageable);

        return ResponseEntity.ok(
                Response.success(PageResponse.from(comments.map(commentMapper::toResponse)))
        );
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<Response<List<CommentResponse>>> getReplies(
            @PathVariable UUID auctionId,
            @PathVariable UUID commentId
    ) {
        List<Comment> replies = commentService.findReplies(auctionId, commentId);

        List<CommentResponse> response = replies.stream()
                .map(commentMapper::toResponse)
                .toList();

        return ResponseEntity.ok(Response.success(response));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Response<Void>> delete(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID auctionId,
            @PathVariable UUID commentId
    ) {
        commentService.delete(auctionId, commentId, userId);

        return ResponseEntity.ok(Response.message("Comment deleted successfully"));
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamComments(@PathVariable UUID auctionId) {
        return commentSseService.subscribe(auctionId);
    }
}
