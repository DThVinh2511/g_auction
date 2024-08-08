package com.ghtk.auction.service.impl;

import com.ghtk.auction.dto.request.product.ProductCreationRequest;
import com.ghtk.auction.dto.request.product.ProductFilterRequest;
import com.ghtk.auction.dto.response.product.ProductResponse;
import com.ghtk.auction.entity.Product;
import com.ghtk.auction.entity.User;
import com.ghtk.auction.entity.UserProduct;
import com.ghtk.auction.enums.ProductCategory;
import com.ghtk.auction.exception.NotFoundException;
import com.ghtk.auction.repository.ProductRepository;
import com.ghtk.auction.repository.UserProductRepository;
import com.ghtk.auction.repository.UserRepository;
import com.ghtk.auction.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
	final ProductRepository productRepository;
	final UserRepository userRepository;
	final UserProductRepository userProductRepository;
	
	@Override
	public Product createProduct(ProductCreationRequest request) {
		var context = SecurityContextHolder.getContext();
		String email = context.getAuthentication().getName();
		
		User user = userRepository.findByEmail(email);
		
		Product product = new Product();
		product.setName(request.getName());
		product.setOwnerId(user.getId());
		product.setCategory(request.getCategory());
		product.setDescription(request.getDescription());
		product.setImage(request.getImage());
		
		return productRepository.save(product);
		
	}
	
	
	
	@Override
	public List<ProductResponse> getAllMyProduct() {
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(currentUser);
		Long userId = user.getId();
		
		List<Object[]> products = productRepository.findByOwnerId(userId);
		return products.stream()
				.map(product -> new ProductResponse(
						(String) product[0],
						(String) product[1],
						(ProductCategory.valueOf((String) product[2])),
						(String) product[3],
						(String) product[4]
				)).collect(Collectors.toList());
	}
	
	@Override
	public Product getById(Long id) {
		return productRepository.findById(id).orElseThrow(
				() -> new NotFoundException("Khong tim thay san pham")
		);
	}
	
	
	@Override
	public List<ProductResponse> getMyByCategory(Jwt principal , ProductFilterRequest request) {
		
		Long userId = (Long)principal.getClaims().get("id");
		
		List<Product> products = productRepository.findAllByOwnerIdAndCategory(userId,request.getProductCategory());
		
		Map<Long,String> buyerMap = new HashMap<>();
		buyerMap.put(null, null);
		products.forEach(product -> {
			if (!buyerMap.containsKey(product.getBuyerId())) {
				buyerMap.put(product.getBuyerId()
						,userRepository.findById(product.getBuyerId()).get().getFullName());
			}
		});
		
		return products.stream().map(
				product -> ProductResponse.builder()
						.name(product.getName())
						.category(product.getCategory())
						.description(product.getDescription())
						.image(product.getImage())
//						.buyer(buyerMap.get(product.getBuyerId()))
						.build()
		).collect(Collectors.toList());
		
	}
	
	@PreAuthorize("@productComponent.isProductOwner(#id, principal)")
	@Override
	public ProductResponse deleteProduct(Jwt principal, Long id) {
		Product product = productRepository.findById(id).get();
		User user = userRepository.findByEmail(principal.getClaims().get("sub").toString());
		
		productRepository.delete(product);
		return ProductResponse.builder()
				.owner(user.getFullName())
				.name(product.getName())
				.category(product.getCategory())
				.description(product.getDescription())
				.image(product.getImage())
				.build();
	}
	
	@Override
	public void interestProduct(Jwt principal, Long id) {
		
		Long userId = (Long)principal.getClaims().get("id");
		Product productId = productRepository.findById(id).orElseThrow(
				() -> new  NotFoundException("Product not found")
		);
		
		userProductRepository.save(UserProduct.builder()
						.userID(userRepository.findById(userId).get())
						.productID(productId)
						.build());
	}
	
	@Override
	public Long getInterestProduct(Long id) {
		return userProductRepository.countByProductID(productRepository.findById(id).get());
	}
	
	@Override
	public List<ProductResponse> getMyInterestProduct(Jwt principal) {
		
		Long userId = (Long)principal.getClaims().get("id");
		
		List<Object[]> products = userProductRepository.findMyInterestByUserID(userId);
		return products.stream()
				.map(product ->new ProductResponse(
						(String) product[0],
						(String) product[1],
						(ProductCategory.valueOf((String) product[2])),
						(String) product[3],
						(String) product[4]
				)).collect(Collectors.toList());
		
	}
	
	@Override
	public List<ProductResponse> searchProductbyCategory(ProductFilterRequest request) {
		
		List<Product> products = productRepository.findAllByCategory(request.getProductCategory());
		
		products = products.stream().filter(product ->
			product.getBuyerId() == null
		).toList();
		
		Map<Long,String> ownerMap = new HashMap<>();
		products.forEach(product -> {
			if (!ownerMap.containsKey(product.getOwnerId())) {
				ownerMap.put(product.getOwnerId()
						,userRepository.findById(product.getOwnerId()).get().getFullName());
			}
		});
		
		return products.stream().map(
				product -> ProductResponse.builder()
						.owner(ownerMap.get(product.getOwnerId()))
						.name(product.getName())
						.category(product.getCategory())
						.description(product.getDescription())
						.image(product.getImage())
						.build()
		).collect(Collectors.toList());
		
	}
}
