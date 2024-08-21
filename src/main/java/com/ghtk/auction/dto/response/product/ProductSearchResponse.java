package com.ghtk.auction.dto.response.product;

import com.ghtk.auction.enums.ProductCategory;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSearchResponse {
    Long id;

    String owner;

    String name;

    ProductCategory category;

    String description;

    String image;
}
