package com.ghtk.auction.controller.product;


import com.ghtk.auction.dto.request.product.ProductCreationRequest;
import com.ghtk.auction.dto.request.product.ProductFilterRequest;
import com.ghtk.auction.dto.response.ApiResponse;
import com.ghtk.auction.dto.response.product.ProductDeletedResponse;
import com.ghtk.auction.dto.response.product.ProductResponse;
import com.ghtk.auction.entity.Product;
import com.ghtk.auction.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/products")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProductController {
	
	final ProductService productService;
	
	
	@PostMapping
	public ResponseEntity<ApiResponse<Product>> create(@RequestBody ProductCreationRequest request) {
		return ResponseEntity.ok(ApiResponse.success(productService.createProduct(request)));
	}
	
	@GetMapping("/get-my-all")
	public ResponseEntity<ApiResponse<List<ProductResponse>>> getAll() {
		return ResponseEntity.ok(ApiResponse.success(productService.getAllMyProduct()));
	}

	@GetMapping("/get-my-by-category")
	public ResponseEntity<ApiResponse<List<ProductResponse>>> getMyAllByCategory(
			@AuthenticationPrincipal Jwt principal,
			@RequestBody ProductFilterRequest request) {
		return ResponseEntity.ok(ApiResponse.success(productService.getMyByCategory(principal,request)));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<ProductDeletedResponse>> delete(
			@AuthenticationPrincipal Jwt principal,
			@PathVariable Long id) {
		ProductResponse deleted = productService.deleteProduct(principal, id);
		return ResponseEntity.ok(ApiResponse.success(ProductDeletedResponse.builder().message("Product was deleted!").product(deleted).build()));
	}
	
	@PostMapping("/interest/{id}")
	public ResponseEntity<ApiResponse<Void>> interest(@AuthenticationPrincipal Jwt principal , @PathVariable Long id) {
		productService.interestProduct(principal, id);
		return ResponseEntity.ok(ApiResponse.success("Da thich"));
	}
	
	@GetMapping("/interest/{id}")
	public ResponseEntity<ApiResponse<Long>> getAllInterest(@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.success(productService.getInterestProduct(id)));
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/getAllMyInterest")
	public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllMyInterest(@AuthenticationPrincipal Jwt principal) {
		return ResponseEntity.ok(ApiResponse.success(productService.getMyInterestProduct(principal)));
	}
	
	@GetMapping("/")
	public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProductByCategory(@RequestBody ProductFilterRequest category) {
		return ResponseEntity.ok(ApiResponse.success(productService.searchProductbyCategory(category)));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.success(productService.getById(id)));
	}
}
