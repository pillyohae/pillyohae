package com.example.pillyohae.product.service;

import com.example.pillyohae.global.exception.CustomResponseStatusException;
import com.example.pillyohae.global.exception.code.ErrorCode;
import com.example.pillyohae.product.dto.*;
import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.product.repository.ProductRepository;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
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

    public Page<ProductSearchResponseDto> searchAndConvertProducts(String productName, String companyName, String category, int page, int size, String sortBy, Boolean isAsc) {

        //정렬 방향과 속성 지정
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        //페이징 객체 생성
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> productsPage = productRepository.getAllProduct(productName, companyName, category, pageable);

        return productsPage.map(product -> new ProductSearchResponseDto(
            product.getProductId(),
            product.getProductName(),
            product.getCompanyName(),
            product.getCategory(),
            product.getPrice()
        ));
    }

    public Page<ProductSearchResponseDto> findSellersProducts(String email, int page, int size, String sortBy, Boolean isAsc) {

        User user = userService.findByEmail(email);

        //정렬 방향과 속성 지정
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
//        if (sortBy == null || sortBy.isEmpty()) {
//            Sort.by(direction, "productId");
//        }
        Sort sort = Sort.by(direction, sortBy);
        //페이징 객체 생성
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> productsPage = productRepository.findProductsByUserId(user.getId(), pageable);

        return productsPage
            .map(product -> new ProductSearchResponseDto(
                product.getProductId(),
                product.getProductName(),
                product.getCompanyName(),
                product.getCategory(),
                product.getPrice()
            ));

    }
}

