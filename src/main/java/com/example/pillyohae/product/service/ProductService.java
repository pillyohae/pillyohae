package com.example.pillyohae.product.service;

import com.example.pillyohae.global.S3.S3Service;
import com.example.pillyohae.global.dto.UploadFileInfo;
import com.example.pillyohae.global.entity.FileStorage;
import com.example.pillyohae.global.exception.CustomResponseStatusException;
import com.example.pillyohae.global.exception.code.ErrorCode;
import com.example.pillyohae.product.dto.*;
import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.product.repository.ImageStorageRepository;
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
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageStorageRepository imageStorageRepository;
    private final UserService userService;
    private final S3Service s3Service;

    /**
     * 상품 생성
     *
     * @param requestDto 상품 생성시 필요한 요청사항
     * @param email      사용자 이메일
     * @return 정상처리 시 ProductCreateResponseDto
     */
    @Transactional
    public ProductCreateResponseDto createProduct(ProductCreateRequestDto requestDto, String email) {

        User findUser = userService.findByEmail(email);

        Product savedProduct = productRepository.save(requestDto.toEntity(findUser));

        return new ProductCreateResponseDto(
            savedProduct.getProductId(),
            savedProduct.getProductName(),
            savedProduct.getCategory(),
            savedProduct.getCompanyName(),
            savedProduct.getDescription(),
            savedProduct.getPrice(),
            savedProduct.getStatus());
    }

    /**
     * 상품정보 수정
     *
     * @param productId  상품 id
     * @param requestDto 상품정보 수정시 필요한 요청사항
     * @return 정상 처리 시 ProductUpdateResponseDto
     */
    @Transactional
    public ProductUpdateResponseDto updateProduct(Long productId, ProductUpdateRequestDto requestDto) {

        Product findProduct = findById(productId);
        findProduct.updateProduct(
            requestDto.getProductName(),
            requestDto.getCategory(),
            requestDto.getDescription(),
            requestDto.getCompanyName(),
            requestDto.getPrice(),
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
            updatedProduct.getStatus()
        );
    }

    /**
     * 상품정보 상세조회(단건 조회)
     *
     * @param productId 상품 id
     * @return 정상 처리 시 ProductGetResponseDto
     */
    @Transactional
    public ProductGetResponseDto getProduct(Long productId) {
        Product findProduct = findById(productId);

        return new ProductGetResponseDto(
            findProduct.getProductId(),
            findProduct.getProductName(),
            findProduct.getCategory(),
            findProduct.getDescription(),
            findProduct.getCompanyName(),
            findProduct.getPrice(),
            findProduct.getStatus()
        );
    }


    /**
     * 상품 삭제
     *
     * @param productId 상품 id
     * @param email     사용자 이메일
     */
    @Transactional
    public void deleteProduct(Long productId, String email) {

        Product findProduct = findById(productId);

        User user = userService.findByEmail(email);

        if (!findProduct.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        findProduct.deleteProduct();
    }

    /**
     * 상품 전체 조회(로그인 없이도 사용가능, 조건별 검색 가능)
     *
     * @param productName 상품 이름(검색 조건)
     * @param companyName 판매사 이름(검색 조건)
     * @param category    상품 분류(검색조건)
     * @param page        페이지 번호
     * @param size        한페이지 게시글 수
     * @param sortBy      상품 정렬 조건(ex. productId, price)
     * @param isAsc       상품 정렬 순서(true: 오름차순, false: 내림차순)
     * @return 정상 처리 시 Page<ProductSearchResponseDto> (페이지로 반환된 dto)
     */
    @Transactional
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

    /**
     * 판매자 상품 조회(자체 조회기능, 판매자에게 권한 한정)
     *
     * @param email  사용자 이메일
     * @param page   페이지 번호
     * @param size   한페이지 게시글 수
     * @param sortBy 상품 정렬 조건(ex. productId)
     * @param isAsc  상품 정렬 순서(true: 오름차순, false: 내림차순)
     * @return 정상 처리 시 Page<ProductSearchResponseDto> (페이지로 반환된 dto)
     */
    @Transactional
    public Page<ProductSearchResponseDto> findSellersProducts(String email, int page, int size, String sortBy, Boolean isAsc) {

        User user = userService.findByEmail(email);

        //정렬 방향과 속성 지정
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
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

    public UploadFileInfo uploadImages(Long productId, MultipartFile image) {

        // Product 조회
        Product findProduct = findById(productId);

        // 파일 업로드 로직 호출
        UploadFileInfo imageInfo = s3Service.uploadFile(image);

        // FileStorage 객체 생성
        FileStorage saveImage = new FileStorage(imageInfo.fileUrl(), imageInfo.fileKey(), image.getContentType(), image.getSize(), findProduct);

        // DB에 저장
        imageStorageRepository.save(saveImage);

        // 업로드 결과 반환
        return new UploadFileInfo(saveImage.getFileUrl(), saveImage.getFileKey());
    }

    public Product findById(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new CustomResponseStatusException(ErrorCode.NOT_FOUND_PRODUCT));
    }


}

