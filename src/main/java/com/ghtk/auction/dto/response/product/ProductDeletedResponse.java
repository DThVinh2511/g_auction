package com.ghtk.auction.dto.response.product;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDeletedResponse {
	String message;
	ProductResponse product;
	
}
