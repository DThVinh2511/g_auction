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
@Table(name = "time_history")
public class TimeHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @Column(nullable = false, name = "join_time")
    LocalDateTime joinTime;
    
    @Column(nullable = false, name = "out_time")
    LocalDateTime outTime;
    
    @ManyToOne
    @JoinColumn(name = "user_auction_id")
    UserAuction userAuction;
}
