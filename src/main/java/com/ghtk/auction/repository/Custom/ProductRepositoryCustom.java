package com.ghtk.auction.repository.Custom;

import com.ghtk.auction.entity.Product;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepositoryCustom {

    List<Product> findProductByName(String key, Pageable pageable);
}
