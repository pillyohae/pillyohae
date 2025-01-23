package com.example.pillyohae.product.service;


import com.example.pillyohae.global.S3.S3Service;
import com.example.pillyohae.global.dto.UploadFileInfo;
import com.example.pillyohae.global.exception.CustomResponseStatusException;
import com.example.pillyohae.global.exception.code.ErrorCode;
import com.example.pillyohae.persona.PersonaService;
import com.example.pillyohae.product.dto.*;
import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.product.entity.ProductImage;
import com.example.pillyohae.product.entity.type.ProductStatus;
import com.example.pillyohae.product.repository.ImageStorageRepository;
import com.example.pillyohae.product.repository.ProductRepository;
import com.example.pillyohae.recommendation.dto.RecommendationKeywordDto;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.service.UserService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageStorageRepository imageStorageRepository;
    private final UserService userService;
    private final S3Service s3Service;
    private final PersonaService personaService;
    private final EntityManager entityManager;

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
            requestDto.getPrice()

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

        List<String> imageUrls = findProduct.getImages()
            .stream()
            .map(ProductImage::getFileUrl)
            .toList();

        return new ProductGetResponseDto(
            findProduct.getProductId(),
            findProduct.getProductName(),
            findProduct.getCategory(),
            findProduct.getDescription(),
            findProduct.getCompanyName(),
            findProduct.getPrice(),
            findProduct.getStatus(),
            imageUrls
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

        // Response로 변환
        return productsPage.map(product -> new ProductSearchResponseDto(
            product.getProductId(),
            product.getProductName(),
            product.getCompanyName(),
            product.getCategory(),
            product.getPrice(),
            product.getStatus(),
            product.getThumbnailUrl() // 썸네일 생성 메서드 호출
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

        // Response로 변환
        return productsPage.map(product -> new ProductSearchResponseDto(
            product.getProductId(),
            product.getProductName(),
            product.getCompanyName(),
            product.getCategory(),
            product.getPrice(),
            product.getStatus(),
            product.getThumbnailUrl() // 썸네일 생성 메서드 호출
        ));
    }


    /**
     * 이미지 업로드
     *
     * @param productId 상품 id
     * @param image     사용자가 올리는 이미지파일
     * @return UploadFileInfo 반환되는 이미지 정보들
     */
    @Transactional
    public UploadFileInfo uploadImages(Long productId, MultipartFile image) {

        // Product 조회
        Product findProduct = findById(productId);

        int currentImageCount = imageStorageRepository.countByProduct_ProductId(findProduct.getProductId());
        if (currentImageCount >= 5) {
            throw new CustomResponseStatusException(ErrorCode.CANNOT_OVERLOAD_FILE);
        }

        // 파일 업로드 로직 호출
        UploadFileInfo imageInfo = s3Service.uploadFile(image);

        Integer nextPosition = imageStorageRepository.findMaxPositionByProductId(findProduct.getProductId())
            .orElse(0) + 1;

        // FileStorage 객체 생성
        ProductImage saveImage = new ProductImage(imageInfo.fileUrl(), imageInfo.fileKey(), image.getContentType(), image.getSize(), nextPosition, findProduct);


        // DB에 저장
        imageStorageRepository.save(saveImage);

        // 업로드 결과 반환
        return new UploadFileInfo(saveImage.getFileUrl(), saveImage.getFileKey());
    }


    /**
     * 이미지 삭제
     *
     * @param productId 상품 id
     * @param imageId   이미지파일 id
     * @param email     사용자 이메일
     */
    @Transactional
    public void deleteImage(Long productId, Long imageId, String email) {

        Product findProduct = findById(productId);

        User user = userService.findByEmail(email);

        if (!findProduct.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("권한이 없습니다.");
        } // 사용자 외 관리자도 삭제 할 수 있나??

        ProductImage findImage = imageStorageRepository.findById(imageId)
            .orElseThrow(() -> new CustomResponseStatusException(ErrorCode.NOT_FOUND_File));

        if (!findImage.getProduct().getProductId().equals(productId)) {
            throw new CustomResponseStatusException(ErrorCode.INVALID_IMAGE_PRODUCT_MATCH);
        }

        imageStorageRepository.deleteById(imageId);
        s3Service.deleteFile(findImage.getFileKey()); // TODO s3삭제 시 주문페이지에서 이미지는 어떻게 할 것인지 정하기

        imageStorageRepository.updatePositionsAfterDelete(productId, findImage.getPosition());

    }

    /**
     * 이미지 수정
     *
     * @param productId  상품 id
     * @param requestDto 이미지 수정 시 필요한 요청사항
     * @param email      사용자 이메일
     * @return UpdateImageResponseDto
     */
    @Transactional
    public UpdateImageResponseDto updateImages(Long productId, UpdateImageRequestDto requestDto, String email) {

        Product findProduct = findById(productId);
        User user = userService.findByEmail(email);

        if (!findProduct.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        // 순서를 바꿀 이미지
        ProductImage findProductImage = imageStorageRepository.findById(requestDto.getImageId())
            .orElseThrow(() -> new CustomResponseStatusException(ErrorCode.NOT_FOUND_File));

        // productId에 해당하는 이미지들의 리스트
        List<ProductImage> images = imageStorageRepository.findByProduct_ProductId(productId);

        Integer originalPosition = findProductImage.getPosition();
        Integer updatedPosition = requestDto.getPosition();

        // 기존 포지션 < 바뀔 포지션
        if (originalPosition < updatedPosition) { // 효율이 떨어지는 코드, 이후 래팩토링필요

            for (ProductImage targetPosition : images) {
                if (originalPosition < targetPosition.getPosition() && targetPosition.getPosition() <= updatedPosition) {
                    targetPosition.downPosition();
                }
            }
        } else {
            for (ProductImage targetPosition : images) {
                if (updatedPosition <= targetPosition.getPosition() && targetPosition.getPosition() < originalPosition) {
                    targetPosition.upPosition();
                }
            }
        }
        findProductImage.updatePosition(requestDto.getPosition());

        return UpdateImageResponseDto.toDto(findProductImage);
    }

    /**
     * 대표이미지(position = 1) -> AI이미지로 변환 후 position = 1으로 위치 재배치
     *
     * @param productId 상품 id
     * @param email     사용자 이메일
     */
    @Transactional
    public void setRepresentativeAiImage(Long productId, String email) {

        Product findProduct = findById(productId);
        User user = userService.findByEmail(email);

        if (!findProduct.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        // 기존 포지션 1 이미지가 있으면 AI 이미지로 교체하고 기존 이미지를 뒤로 밀기
        ProductImage firstImage = imageStorageRepository.findByProduct_ProductIdAndPosition(productId, 1);

        if (firstImage != null) {
//            // 기존 포지션 한칸씩 밀기
//            imageStorageRepository.incrementAllPositions(productId);
//        }

            // PersonaService를 통해 AI 이미지 생성
            String aiImageUrl = personaService.generatePersonaFromProduct(firstImage.getFileUrl()).getUrl();

            // S3에 업로드
            UploadFileInfo uploadImageInfo = s3Service.uploadFileFromUrl(aiImageUrl);

            // 새로 업로드된 이미지를 0번 이미지로 추가
            ProductImage aiImage = new ProductImage(
                uploadImageInfo.fileUrl(),
                uploadImageInfo.fileKey(),
                0,
                findProduct);

            imageStorageRepository.save(aiImage);
            log.info("Representative AI image successfully saved for productId: {}", productId);
        }
    }

    public Product findById(Long productId) {
        return productRepository.findById(productId)
            .filter(product -> product.getStatus() != ProductStatus.DELETED)
            .orElseThrow(() -> new CustomResponseStatusException(ErrorCode.NOT_FOUND_PRODUCT));
    }

//    // 썸네일 메서드
//    public String getThumbnail(Product product) {
//        if (product.getImages() != null && !product.getImages().isEmpty()) {
//            return product.getImages().stream()
//                .filter(image -> image.getPosition() == 1) // position 1인 이미지 찾기
//                .map(ProductImage::getFileUrl)
//                .findFirst()
//                .orElse(null);
//        }
//        return null;
//    }


    /**
     * 추천 상품 조회
     *
     * @param recommendations 추천 키워드
     * @return 추천 상품 목록
     */
    public List<Product> findByNameLike(RecommendationKeywordDto[] recommendations) {
        return productRepository.findProductsByNameLike(recommendations);
    }


}

