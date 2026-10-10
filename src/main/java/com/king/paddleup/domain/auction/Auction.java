package com.king.paddleup.domain.auction;

import com.king.paddleup.domain.auction.enums.AuctionStatus;
import com.king.paddleup.domain.media.dto.MediaItem;
import com.king.paddleup.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "auctions", check = {
        @CheckConstraint(
                name = "chk_auctions_status",
                constraint = "status in ('DRAFT', 'ACTIVE', 'CLOSED', 'ENDED', 'AWAITING_PAYMENT', 'COMPLETED')"
        ),
        @CheckConstraint(
                name = "chk_min_increment",
                constraint = "min_increment > 0"
        )
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal startingPrice;

    @Column(precision = 12, scale = 2)
    private BigDecimal reservePrice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal minIncrement = BigDecimal.ONE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuctionStatus status = AuctionStatus.ACTIVE;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    @Builder.Default
    private List<MediaItem> media = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column
    private BigDecimal highestBid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private User winner;

    @Column
    private Instant paymentDeadline;

    @Column(nullable = false)
    private Instant endsAt;


    @Column(nullable = false)
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = Instant.now();
    }
}
