package com.ghtk.auction.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ghtk.auction.enums.AuctionStatus;
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
@Table(name = "auction")
public class Auction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	@Column(nullable = false ,name = "title")
	String title;

	@Column(name = "description", length = 100000)
	String description;
	
	@Column(nullable = false, name = "created_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime createdAt; // ngay nguoi ban tao
	
	@Column(nullable = false, name = "confirm_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime confirmDate; // ngay mo dang ky
	
	@Column(nullable = false, name = "end_registration")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime endRegistration; // ngay dong dang ky

	@Column(nullable = false, name = "start_time")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime startTime; // ngay bat dau phien dau gia

	@Column(nullable = false, name = "end_time")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime endTime;

	@Column(nullable = false, name = "start_bid")
	Long startBid; // gia khoi diem

	@Column(nullable = false, name = "price_per_step")
	Long pricePerStep; // buoc nhay gia

	@Column(name = "end_bid")
	Long endBid; // gia da chot
	
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	AuctionStatus status;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "product_id", nullable = false)
	Product product;
}
