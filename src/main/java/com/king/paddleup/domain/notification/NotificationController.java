package com.king.paddleup.domain.notification;

import com.king.paddleup.domain.notification.dto.NotificationResponse;
import com.king.paddleup.infrastructure.sse.UserNotificationSseService;
import com.king.paddleup.shared.response.PageResponse;
import com.king.paddleup.shared.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;
    private final UserNotificationSseService userNotificationSseService;

    @GetMapping
    public ResponseEntity<Response<PageResponse<NotificationResponse>>> getNotifications(
            @AuthenticationPrincipal UUID userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<Notification> notifications = notificationService.findForUser(userId, pageable);

        return ResponseEntity.ok(
                Response.success(PageResponse.from(notifications.map(notificationMapper::toResponse)))
        );
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Response<Long>> getUnreadCount(
            @AuthenticationPrincipal UUID userId
    ) {
        long count = notificationService.countUnread(userId);

        return ResponseEntity.ok(Response.success(count));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Response<Void>> markAsRead(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID id
    ) {
        notificationService.markAsRead(id, userId);

        return ResponseEntity.ok(Response.message("Notification marked as read"));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Response<Void>> markAllAsRead(
            @AuthenticationPrincipal UUID userId
    ) {
        notificationService.markAllAsRead(userId);

        return ResponseEntity.ok(Response.message("All notifications marked as read"));
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(@AuthenticationPrincipal UUID userId) {
        return userNotificationSseService.subscribe(userId);
    }
}
