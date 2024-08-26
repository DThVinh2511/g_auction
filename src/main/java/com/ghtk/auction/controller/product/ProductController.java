package com.ghtk.auction.controller.product;


import com.ghtk.auction.dto.request.product.ProductCreationRequest;
import com.ghtk.auction.dto.request.product.ProductFilterRequest;
import com.ghtk.auction.dto.response.ApiResponse;
import com.ghtk.auction.dto.response.product.ProductDeletedResponse;
import com.ghtk.auction.dto.response.product.ProductResponse;
import com.ghtk.auction.dto.response.product.ProductListResponse;
import com.ghtk.auction.dto.response.user.PageResponse;
import com.ghtk.auction.entity.Product;
import com.ghtk.auction.enums.ProductCategory;
import com.ghtk.auction.service.ProductService;
import com.ghtk.auction.utils.AppConstants;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/v1/products")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProductController {
	
	final ProductService productService;
	
	@PreAuthorize("@userComponent.isActiveUser(#principal)")
	@PostMapping
	public ResponseEntity<ApiResponse<Product>> create(@AuthenticationPrincipal Jwt principal,@RequestBody ProductCreationRequest request) {
		return ResponseEntity.ok(ApiResponse.success(productService.createProduct(principal,request)));
	}
	
	@GetMapping("/get-my-all")
	public ResponseEntity<ApiResponse<List<ProductResponse>>> getAll(
			@RequestParam(value = "page_no", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
			@RequestParam(value = "page_size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize
	) {
		return ResponseEntity.ok(ApiResponse.success(productService.getAllMyProduct(pageNo, pageSize)));
	}
	
	@GetMapping("/get-my-by-category")
	public ResponseEntity<ApiResponse<List<ProductResponse>>> getMyAllByCategory(
			@AuthenticationPrincipal Jwt principal,
			@RequestBody ProductFilterRequest request) {
		return ResponseEntity.ok(ApiResponse.success(productService.getMyByCategory(principal, request)));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<ProductDeletedResponse>> delete(
			@AuthenticationPrincipal Jwt principal,
			@PathVariable Long id) {
		ProductResponse deleted = productService.deleteProduct(principal, id);
		return ResponseEntity.ok(ApiResponse.success(ProductDeletedResponse.builder().message("Product was deleted!").product(deleted).build()));
	}
	
	@PostMapping("/interest/{id}")
	public ResponseEntity<ApiResponse<Void>> interest(@AuthenticationPrincipal Jwt principal, @PathVariable Long id) {
		productService.interestProduct(principal, id);
		return ResponseEntity.ok(ApiResponse.ok("Da thich"));
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
	
	@GetMapping("/top-popular")
	public ResponseEntity<ApiResponse<List<ProductResponse>>> getTop5MostPopularProducts(
			@RequestParam(value = "limit", defaultValue = AppConstants.DEFAULT_LIMIT, required = false) Long limit
	) {
		return ResponseEntity.ok(ApiResponse.success(productService.getTopMostPopularProducts(limit)));
	}
	
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<PageResponse<ProductListResponse>>> searchProduct(
			@RequestParam(value = "key", required = false) String key,
			@RequestParam(value = "page_no", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
			@RequestParam(value = "page_size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize
	) {
		return ResponseEntity.ok(ApiResponse.success(productService.searchProduct(key, pageNo, pageSize)));
	}
	
	@GetMapping("/get-all-product-by-category")
	public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getProductsByCategory(
			@RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
			@RequestParam(value = "category") ProductCategory category
	) {
		return ResponseEntity.ok(ApiResponse.success(productService.getAllProductByCategory(category, pageNo, pageSize)));
	}
	
	@GetMapping("/get-all-product")
	public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getAllProduct(
			@RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize
	) {
		return ResponseEntity.ok(ApiResponse.success(productService.getAllProduct(pageNo, pageSize)));
	}

	@GetMapping("/list-product-favorite")
	public ResponseEntity<ApiResponse<List<Integer>>> getProduct(@AuthenticationPrincipal Jwt principal) {
		return ResponseEntity.ok(ApiResponse.success(productService.listFavoriteProduct(principal)));
	}
}
