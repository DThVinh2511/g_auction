package com.ghtk.auction.dto.stomp;

import java.time.LocalDateTime;

import com.ghtk.auction.entity.UserAuction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentMessage {
    Long id;

    String content;
    
    LocalDateTime createdAt;
    
    Long userId;
}
