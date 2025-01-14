package com.example.pillyohae.product.service;

import com.example.pillyohae.product.dto.ProductCreateRequestDto;
import com.example.pillyohae.product.dto.ProductCreateResponseDto;
import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.product.entity.type.ProductStatus;
import com.example.pillyohae.product.repository.ProductRepository;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.entity.type.Role;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

//    @Autowired
//    private UserService userService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void testCreateProduct() {
        // Given
        String email = "Test@tester.com";
        User testUser = new User("TestUser", email, "password123", "Test Address", Role.SELLER);
        entityManager.persist(testUser); // 사용자 데이터 저장

        ProductCreateRequestDto requestDto = new ProductCreateRequestDto(
            "Test Product", "Category", "Description", "Company", 1000L, ProductStatus.SELLING
        );

        // When: 상품 생성 호출
        ProductCreateResponseDto responseDto = productService.createProduct(requestDto, email);

        // Then: 생성된 상품 검증
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getProductName()).isEqualTo("Test Product");
        assertThat(responseDto.getCategory()).isEqualTo("Category");
        assertThat(responseDto.getPrice()).isEqualTo(1000L);
        assertThat(responseDto.getStatus()).isEqualTo(ProductStatus.SELLING);

        // 데이터베이스에서 실제로 저장된 데이터 검증
        Product savedProduct = productRepository.findById(responseDto.getProductId()).orElseThrow();

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getProductName()).isEqualTo("Test Product");
        assertThat(savedProduct.getUser().getId()).isEqualTo(testUser.getId());
    }
}
