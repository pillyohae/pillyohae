package com.example.pillyohae.product.service;


import com.example.pillyohae.global.S3.S3Service;
import com.example.pillyohae.global.dto.UploadFileInfo;
import com.example.pillyohae.global.exception.CustomResponseStatusException;
import com.example.pillyohae.global.exception.code.ErrorCode;
import com.example.pillyohae.persona.dto.PersonaMessageCreateResponseDto;
import com.example.pillyohae.persona.service.PersonaService;
import com.example.pillyohae.product.dto.category.CategoryResponseDto;
import com.example.pillyohae.product.dto.image.ImageUploadResponseDto;
import com.example.pillyohae.product.dto.image.UpdateImageRequestDto;
import com.example.pillyohae.product.dto.image.UpdateImageResponseDto;
import com.example.pillyohae.product.dto.nutrient.NutrientResponseDto;
import com.example.pillyohae.product.dto.product.*;
import com.example.pillyohae.product.dto.product.ProductGetResponseDto.ImageResponseDto;
import com.example.pillyohae.product.entity.*;
import com.example.pillyohae.product.entity.type.ProductStatus;
import com.example.pillyohae.product.repository.*;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.service.UserService;
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
    private final NutrientRepository nutrientRepository;
    private final CategoryRepository categoryRepository;
    private final PersonaMessageRepository personaMessageRepository;
    private final UserService userService;
    private final S3Service s3Service;
    private final PersonaService personaService;


    /**
     * 상품 생성
     *
     * @param requestDto 상품 생성시 필요한 요청사항
     * @param email      사용자 이메일
     * @return 정상처리 시 ProductCreateResponseDto
     */
    @Transactional
    public ProductCreateResponseDto createProduct(ProductCreateRequestDto requestDto,
        String email) {

        User findUser = userService.findByEmail(email);

        Nutrient nutrient = nutrientRepository.findById(requestDto.getNutrientId())
            .orElseThrow(() -> new CustomResponseStatusException(ErrorCode.NOT_FOUND_NUTRIENT));

        Category category = categoryRepository.findById(requestDto.getCategoryId())
            .orElseThrow(() -> new CustomResponseStatusException(ErrorCode.NOT_FOUND_CATEGORY));

        Product savedProduct = productRepository.save(
            requestDto.toEntity(findUser, nutrient, category));

        // 페르소나 메시지 생성
        List<PersonaMessageCreateResponseDto> personaMessages =
            personaService.createPersonaMessageFromProduct(nutrient.getName());

        // 생성된 메시지를 엔티티로 변환 후 저장
        List<PersonaMessage> messageEntities = personaMessages
            .stream()
            .map(message -> new PersonaMessage(message.getMessage()))
            .toList();

        // 상품과 메시지 연관 관계 설정(상품에 메세지 추가)
        savedProduct.addPersonaMessages(messageEntities);

        // 메시지 저장
        personaMessageRepository.saveAll(messageEntities);

        return new ProductCreateResponseDto(savedProduct);

    }

    /**
     * 상품정보 수정
     *
     * @param productId  상품 id
     * @param requestDto 상품정보 수정시 필요한 요청사항
     * @return 정상 처리 시 ProductUpdateResponseDto
     */
    @Transactional
    public ProductUpdateResponseDto updateProduct(Long productId,
        ProductUpdateRequestDto requestDto) {

        Product findProduct = findById(productId);

        Nutrient nutrient = nutrientRepository.findById(requestDto.getNutrientId())
            .orElseThrow(() -> new CustomResponseStatusException(ErrorCode.NOT_FOUND_NUTRIENT));

        Category category = categoryRepository.findById(requestDto.getCategoryId())
            .orElseThrow(() -> new CustomResponseStatusException(ErrorCode.NOT_FOUND_CATEGORY));

        if (requestDto.getStock() < 0) {
            throw new CustomResponseStatusException(ErrorCode.STOCK_CANNOTBE_NEGATIVE);
        }

        findProduct.updateProduct(
            requestDto.getProductName(),
            category,
            requestDto.getDescription(),
            requestDto.getCompanyName(),
            requestDto.getPrice(),
            requestDto.getStock(),
            nutrient
        );

        return new ProductUpdateResponseDto(
            findProduct.getProductId(),
            findProduct.getProductName(),
            findProduct.getCategory(),
            findProduct.getDescription(),
            findProduct.getCompanyName(),
            findProduct.getPrice(),
            findProduct.getStatus(),
            findProduct.getStock(),
            findProduct.getNutrient()
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

        //카테고리 정보 DTO로 변환
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto(
            findProduct.getCategory().getCategoryId(),
            findProduct.getCategory().getName()
        );

        //영양성분 정보 DTO로 변환
        NutrientResponseDto nutrientResponseDto = new NutrientResponseDto(
            findProduct.getNutrient().getNutrientId(),
            findProduct.getNutrient().getName(),
            findProduct.getNutrient().getDescription()
        );

        List<ImageResponseDto> images = findProduct.getImages()
            .stream()
            .map(image -> new ImageResponseDto(
                image.getId(),
                image.getFileUrl(),
                image.getPosition()
            ))
            .toList();

        return new ProductGetResponseDto(
            findProduct.getProductId(),
            findProduct.getProductName(),
            categoryResponseDto,
            findProduct.getDescription(),
            findProduct.getCompanyName(),
            findProduct.getPrice(),
            findProduct.getStatus(),
            findProduct.getStock(),
            images,
            nutrientResponseDto
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
     * @param companyName 상품 분류(검색조건)
     * @param page        페이지 번호
     * @param size        한페이지 게시글 수
     * @param sortBy      상품 정렬 조건(ex. productId, price)
     * @param isAsc       상품 정렬 순서(true: 오름차순, false: 내림차순)
     * @return 정상 처리 시 Page<ProductSearchResponseDto> (페이지로 반환된 dto)
     */
    @Transactional
    public Page<ProductSearchResponseDto> searchAndConvertProducts(String productName,
        String companyName, String categoryName, int page, int size, String sortBy, Boolean isAsc) {

        //정렬 방향과 속성 지정
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        //페이징 객체 생성
        Pageable pageable = PageRequest.of(page, size, sort);
        //상품 조회
        Page<Product> productsPage = productRepository.getAllProduct(productName, companyName,
            categoryName, pageable);

        // Product 객체를 ProductSearchResponseDto로 변환 후 반환
        return productsPage.map(ProductSearchResponseDto::new);
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
    public Page<ProductSearchResponseDto> findSellersProducts(String email, int page, int size,
        String sortBy, Boolean isAsc) {

        User user = userService.findByEmail(email);

        //정렬 방향과 속성 지정
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        //페이징 객체 생성
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> productsPage = productRepository.findProductsByUserId(user.getId(), pageable);

        // Product 객체를 ProductSearchResponseDto로 변환 후 반환
        return productsPage.map(ProductSearchResponseDto::new);
    }


    /**
     * 이미지 업로드
     *
     * @param productId 상품 id
     * @param image     사용자가 올리는 이미지파일
     * @return UploadFileInfo 반환되는 이미지 정보들
     */
    @Transactional
    public ImageUploadResponseDto uploadImages(Long productId, MultipartFile image) {

        // Product 조회
        Product findProduct = findById(productId);

        int currentImageCount = imageStorageRepository.countByProduct_ProductId(
            findProduct.getProductId());
        if (currentImageCount >= 5) {
            throw new CustomResponseStatusException(ErrorCode.CANNOT_OVERLOAD_FILE);
        }

        // 파일 업로드 로직 호출
        UploadFileInfo imageInfo = s3Service.uploadFile(image);

        Integer nextPosition =
            imageStorageRepository.findMaxPositionByProductId(findProduct.getProductId())
                .orElse(0) + 1;

        // FileStorage 객체 생성
        ProductImage saveImage = new ProductImage(imageInfo.fileUrl(), imageInfo.fileKey(),
            image.getContentType(), image.getSize(), nextPosition, findProduct);

        // DB에 저장
        imageStorageRepository.save(saveImage);

        // 업로드 결과 반환
        return new ImageUploadResponseDto(saveImage.getId(), saveImage.getPosition(),
            saveImage.getFileUrl(), saveImage.getFileKey());
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

        imageStorageRepository.deleteImageByIdAndPosition(productId, findImage.getPosition(),
            imageId);

        // position > 1인 경우 자리 재배치 / position <= 1인 경우 자리 재배치 X
        if (findImage.getPosition() > 1) {
            imageStorageRepository.updatePositionsAfterDelete(productId, findImage.getPosition());
        }

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
    public UpdateImageResponseDto updateImages(Long productId, UpdateImageRequestDto requestDto,
        String email) {

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
        if (originalPosition < updatedPosition) {

            for (ProductImage targetPosition : images) {
                if (originalPosition < targetPosition.getPosition()
                    && targetPosition.getPosition() <= updatedPosition) {
                    targetPosition.downPosition();
                }
            }
        } else {
            for (ProductImage targetPosition : images) {
                if (updatedPosition <= targetPosition.getPosition()
                    && targetPosition.getPosition() < originalPosition) {
                    targetPosition.upPosition();
                }
            }
        }
        findProductImage.updatePosition(requestDto.getPosition());

        return UpdateImageResponseDto.toDto(findProductImage);
    }

    /**
     * 대표이미지(position = 1) -> AI이미지로 변환 후 해당 AI 이미지 position = 0으로 배치
     *
     * @param productId 상품 id
     * @param email     사용자 이메일
     */
    @Transactional
    public ImageUploadResponseDto setRepresentativeAiImage(Long productId, String email) {
        
        log.info("ai이미지 생성");
        
        Product findProduct = findById(productId);
        User user = userService.findByEmail(email);

        if (!findProduct.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        // 기존 포지션 1찾기
        ProductImage firstImage = imageStorageRepository.findByProduct_ProductIdAndPosition(
            productId, 1);

        if (firstImage == null) {
            throw new CustomResponseStatusException(ErrorCode.NOT_FOUND_IMAGE_POSITION1);
        }

        // PersonaService를 통해 AI 이미지 생성
        String aiImageUrl = personaService.generatePersonaFromProduct(firstImage.getFileUrl())
            .getUrl();

        // S3에 업로드
        UploadFileInfo uploadImageInfo = s3Service.uploadFileFromUrl(aiImageUrl);

        // 0번 이미지 찾기
        ProductImage positionZeroAiImage = imageStorageRepository.findByProduct_ProductIdAndPosition(
            productId, 0);

        //0번 이미지가 이미 있으면 삭제
        if (positionZeroAiImage != null) {
            deleteImage(productId, positionZeroAiImage.getId(), email);
        }

        // 새로 업로드된 이미지를 0번 이미지로 추가
        ProductImage aiImage = new ProductImage(
            uploadImageInfo.fileUrl(),
            uploadImageInfo.fileKey(),
            "image/png",
            0,
            findProduct);

        imageStorageRepository.save(aiImage);
        log.info("Representative AI image successfully saved for productId: {}", productId);

        return new ImageUploadResponseDto(aiImage.getId(), aiImage.getPosition(),
            aiImage.getFileUrl(), aiImage.getFileKey());
    }

    /**
     * 메인이미지 재업로드(Position1)
     *
     * @param productId 상품 id
     * @param mainImage 사용자가 올리는 대표이미지파일(1번 위치에 올릴 파일)
     * @return UploadFileInfo 반환되는 이미지 정보들
     */
    @Transactional
    public ImageUploadResponseDto uploadImageToPositionOne(Long productId,
        MultipartFile mainImage) {
        log.info("이미지 생성 시도" + mainImage.getOriginalFilename() + "productId:" + productId);
        // Product 조회
        Product findProduct = findById(productId);

        // 파일 업로드 로직 호출
        UploadFileInfo firstImageInfo = s3Service.uploadFile(mainImage);

        ProductImage originalMainImage = imageStorageRepository.findByProduct_ProductIdAndPosition(
            productId, 1);

        if (originalMainImage != null) {
            throw new CustomResponseStatusException(ErrorCode.IMAGE_ALREADY_EXIST);
        }

        ProductImage newPositionOneImage = new ProductImage(
            firstImageInfo.fileUrl(),
            firstImageInfo.fileKey(),
            mainImage.getContentType(),
            mainImage.getSize(),
            1,
            findProduct
        );
        imageStorageRepository.save(newPositionOneImage);

        return new ImageUploadResponseDto(newPositionOneImage.getId(),
            newPositionOneImage.getPosition(), newPositionOneImage.getFileUrl(),
            newPositionOneImage.getFileKey());
    }

    public Product findById(Long productId) {
        return productRepository.findById(productId)
            .filter(product -> product.getStatus() != ProductStatus.DELETED)
            .orElseThrow(() -> new CustomResponseStatusException(ErrorCode.NOT_FOUND_PRODUCT));
    }


    /**
     * 추천 상품 조회
     *
     * @param recommendations 추천 키워드
     * @return 추천 상품 목록
     */
    public List<Product> findByNameLike(List<String> recommendedProductNames) {
        return productRepository.findProductsByNameLike(recommendedProductNames);
    }

    /**
     * 추천에 사용할 모든 상품의 이름과 영양성분을 List 로 반환하는 메서드
     *
     * @return ProductRecommendationDto
     */
    public List<ProductRecommendationDto> getAllProductsWithNutrient() {
        return productRepository.findAllProductsName();
    }

}

