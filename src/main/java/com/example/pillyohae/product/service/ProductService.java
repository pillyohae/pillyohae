package com.example.pillyohae.product.service;

import com.example.pillyohae.global.exception.CustomResponseStatusException;
import com.example.pillyohae.global.exception.code.ErrorCode;
import com.example.pillyohae.product.dto.*;
import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.product.repository.ProductRepository;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public ProductCreateResponseDto createProduct(ProductCreateRequestDto requestDto, String email) {

        User findUser = userService.findByEmail(email);


        Product savedProduct = productRepository.save(new Product(
            findUser,
            requestDto.getProductName(),
            requestDto.getCategory(),
            requestDto.getDescription(),
            requestDto.getCompanyName(),
            requestDto.getPrice(),
            requestDto.getImageUrl(),
            requestDto.getStatus()
        ));

        return new ProductCreateResponseDto(
            savedProduct.getProductId(),
            savedProduct.getProductName(),
            savedProduct.getCategory(),
            savedProduct.getCompanyName(),
            savedProduct.getDescription(),
            savedProduct.getPrice(),
            savedProduct.getImageUrl(),
            savedProduct.getStatus());
    }

    public ProductUpdateResponseDto updateProduct(Long productId, ProductUpdateRequestDto requestDto) {

        Product findProduct = findById(productId);
        findProduct.updateProduct(
            requestDto.getProductName(),
            requestDto.getCategory(),
            requestDto.getDescription(),
            requestDto.getCompanyName(),
            requestDto.getPrice(),
            requestDto.getImageUrl(),
            requestDto.getStatus()
        );

        Product updatedProduct = productRepository.save(findProduct);

        return new ProductUpdateResponseDto(
            updatedProduct.getProductId(),
            updatedProduct.getProductName(),
            updatedProduct.getCategory(),
            updatedProduct.getDescription(),
            updatedProduct.getCompanyName(),
            updatedProduct.getPrice(),
            updatedProduct.getImageUrl(),
            updatedProduct.getStatus()
        );
    }

    public ProductGetResponseDto getProduct(Long productId) {
        Product findProduct = findById(productId);

        return new ProductGetResponseDto(
            findProduct.getProductId(),
            findProduct.getProductName(),
            findProduct.getCategory(),
            findProduct.getDescription(),
            findProduct.getCompanyName(),
            findProduct.getPrice(),
            findProduct.getImageUrl(),
            findProduct.getStatus()
        );
    }

    public Product findById(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new CustomResponseStatusException(ErrorCode.NOT_FOUND_PRODUCT));
    }

    @Transactional
    public void deleteProduct(Long productId, String email) {

        Product findProduct = findById(productId);

        User user = userService.findByEmail(email);

        if (!findProduct.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        findProduct.deleteProduct();
    }
}
