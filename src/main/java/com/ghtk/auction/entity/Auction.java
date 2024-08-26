package com.ghtk.auction.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ghtk.auction.dto.response.auction.AuctionListResponse;
import com.ghtk.auction.enums.AuctionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "auction")
@SqlResultSetMapping(
		name = "AuctionListProjectionMapping",
		classes = @ConstructorResult(
				targetClass = AuctionListResponse.class,
				columns = {
						@ColumnResult(name = "id", type = Long.class),
						@ColumnResult(name = "productId", type = Long.class),
						@ColumnResult(name = "title", type = String.class),
						@ColumnResult(name = "description", type = String.class),
						@ColumnResult(name = "image", type = String.class),
						@ColumnResult(name = "createdAt", type = LocalDateTime.class),
						@ColumnResult(name = "confirmDate", type = LocalDateTime.class),
						@ColumnResult(name = "endRegistration", type = LocalDateTime.class),
						@ColumnResult(name = "startTime", type = LocalDateTime.class),
						@ColumnResult(name = "endTime", type = LocalDateTime.class),
						@ColumnResult(name = "startBid", type = Long.class),
						@ColumnResult(name = "pricePerStep", type = Long.class),
						@ColumnResult(name = "endBid", type = Long.class),
						@ColumnResult(name = "status", type = String.class)
				}
		)
)
@Entity
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
