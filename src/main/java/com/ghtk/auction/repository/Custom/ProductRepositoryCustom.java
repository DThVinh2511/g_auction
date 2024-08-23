package com.ghtk.auction.repository.Custom;

import com.ghtk.auction.dto.response.product.ProductListResponse;
import com.ghtk.auction.enums.ProductCategory;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepositoryCustom {

    List<ProductListResponse> findProduct(String key, Pageable pageable, ProductCategory category);

    List<ProductListResponse> getInterestProductTop(Long limit);
    
    public long countTotalProducts(String key, ProductCategory category);
}
