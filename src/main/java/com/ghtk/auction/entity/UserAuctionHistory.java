package com.ghtk.auction.entity;

import com.ghtk.auction.dto.response.auction.AuctionBidResponse;
import com.ghtk.auction.dto.response.auction.AuctionListResponse;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "user_auction_history")
@SqlResultSetMapping(
    name = "BidListProjectionMapping",
    classes = @ConstructorResult(
        targetClass = AuctionBidResponse.class,
        columns = {
            @ColumnResult(name = "userName", type = String.class),
            @ColumnResult(name = "auctionId", type = Long.class),
            @ColumnResult(name = "userId", type = Long.class),
            @ColumnResult(name = "bid", type = Long.class),
            @ColumnResult(name = "createdAt", type = LocalDateTime.class)
        }
    )
)
public class UserAuctionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "bid", nullable = false)
    Long bid;

    @Column(name = "time", nullable = false)
    LocalDateTime time;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_auction_id")
    UserAuction userAuction;
}
