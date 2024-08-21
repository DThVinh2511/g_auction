package com.ghtk.auction.dto.response.product;


import com.ghtk.auction.enums.ProductCategory;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
	String owner;
	
	String name;
	
	ProductCategory category;
	
	String description;
	
	String image;
	
	Long productId;
	
	Long quantity;
	
//	public ProductResponse(String owner, String name, ProductCategory category, String description, String image, Long productId) {
//		this.owner = owner;
//		this.name = name;
//		this.category = category;
//		this.description = description;
//		this.image = image;
//		this.productId = productId;
//	}
}
