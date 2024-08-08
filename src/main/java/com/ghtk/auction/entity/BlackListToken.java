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
@Table(name = "blacklist_tokens")
public class BlackListToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "token", nullable = false)
    String token;

    @Column(name = "expiry_time", nullable = false)
    LocalDateTime expiryTime;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;
}
