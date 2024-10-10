package com.ghtk.auction.entity;

import com.ghtk.auction.dto.response.auction.AuctionCommentResponse;
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
@SqlResultSetMapping(
    name = "CommentListProjectionMapping",
    classes = @ConstructorResult(
        targetClass = AuctionCommentResponse.class,
        columns = {
            @ColumnResult(name = "commentId", type = Long.class),
            @ColumnResult(name = "userName", type = String.class),
            @ColumnResult(name = "content", type = String.class),
            @ColumnResult(name = "createdAt", type = LocalDateTime.class)
        }
    )
)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @Column(name = "content", nullable = false)
    String content;
    
    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;
    
    @Column(name = "auction_id", nullable = false)
    Long auctionId;

    @Column(name = "user_id", nullable = false)
    Long userId;
}
