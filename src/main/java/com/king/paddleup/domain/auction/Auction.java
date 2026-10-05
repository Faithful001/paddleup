package com.king.paddleup.domain.auction;

import com.king.paddleup.domain.auction.enums.AuctionStatus;
import com.king.paddleup.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
                name = "chk_image_urls_max",
                constraint = "cardinality(image_urls) <= 10"
        ),
        @CheckConstraint(
                name = "chk_auctions_status",
                constraint = "status in ('DRAFT', 'ACTIVE', 'CLOSED', 'CANCELLED')"
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

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Size(max = 10)
    @Column(name = "image_urls", columnDefinition = "varchar(500)[]", nullable = false)
    private List<String> imageUrls = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

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
