package com.ghtk.auction.dto.request.product;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ghtk.auction.enums.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCreationRequest {
	@NotBlank
	String name;
	
	@NotEmpty
	ProductCategory category;
	
	@NotEmpty
	String description;
	
	@NotEmpty
	String image;
	
}
