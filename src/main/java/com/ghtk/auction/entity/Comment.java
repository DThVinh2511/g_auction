package com.ghtk.auction.entity;

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
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @Column(nullable = false, name = "content")
    String content;
    
    @Column(nullable = false, name = "created_at")
    LocalDateTime createdAt;
    
    @ManyToOne
    @JoinColumn(name = "user_auction_id")
    UserAuction userAuctionId;
}
