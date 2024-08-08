package com.ghtk.auction.service;


import com.ghtk.auction.dto.request.product.ProductCreationRequest;
import com.ghtk.auction.dto.request.product.ProductFilterRequest;
import com.ghtk.auction.dto.response.product.ProductResponse;
import com.ghtk.auction.entity.Product;
import com.ghtk.auction.enums.ProductCategory;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface ProductService {
	
	Product createProduct(ProductCreationRequest request);
	
	List<ProductResponse> getAllMyProduct();
	
	Product getById(Long id);
	
	List<ProductResponse> getMyByCategory(Jwt principle, ProductFilterRequest category);
	
	ProductResponse deleteProduct(Jwt principal, Long id);
	
	void interestProduct(Jwt principal, Long id);
	
	//so luong
	Long getInterestProduct(Long id);
	
	List<ProductResponse> getMyInterestProduct(Jwt principal);
	
	List<ProductResponse> searchProductbyCategory(ProductFilterRequest request);
	
}