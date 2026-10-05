package com.king.paddleup.domain.auction;

import com.king.paddleup.domain.auction.enums.AuctionStatus;
import com.king.paddleup.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auctions")
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

    @Column(nullable = false)
    private int startingPrice;

    @Column
    private int reservePrice;

    @Column
    private int minIncrement;

    @Column(nullable = false)
    private Instant endTime;

    @Column
    private AuctionStatus status = AuctionStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(nullable = false)
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PrePersist
    protected void onUpdate(){
        this.updatedAt = Instant.now();
    }
}
