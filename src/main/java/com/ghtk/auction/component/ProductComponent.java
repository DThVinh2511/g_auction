package com.ghtk.auction.component;

import com.ghtk.auction.entity.Product;
import com.ghtk.auction.exception.NotFoundException;
import com.ghtk.auction.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("productComponent")
@RequiredArgsConstructor
public class ProductComponent {
    
    private  final ProductRepository productRepository;
    
    public boolean isProductOwner(Long productId, Jwt principal) {
        Long userId = (Long)principal.getClaims().get("id");
//		return productRepository.findById(productId)
//				.map(product -> product.getOwnerId().equals(userId))
//				.orElse(false);
        Product product = productRepository.findById(productId).
              orElseThrow(() ->new NotFoundException("Product not found"));
        
        return userId.equals(product.getOwnerId());
    }
}
