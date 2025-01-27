package com.example.pillyohae.product.controller;

import com.example.pillyohae.product.dto.*;
import com.example.pillyohae.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    /**
     * 상품 생성
     *
     * @param userDetails 사용자 정보
     * @param requestDto  상품 생성시 필요한 요청사항
     * @return 정상처리 시 ProductCreateResponseDto
     */
    @PostMapping("/products")
    public ResponseEntity<ProductCreateResponseDto> createProduct(
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @RequestBody ProductCreateRequestDto requestDto
    ) {
        ProductCreateResponseDto responseDto = productService.createProduct(requestDto, userDetails.getUsername());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * 상품정보 수정
     *
     * @param productId  상품 id
     * @param requestDto 상품정보 수정시 필요한 요청사항
     * @return 정상 처리 시 ProductUpdateResponseDto
     */
    @PutMapping("/products/{productId}")
    public ResponseEntity<ProductUpdateResponseDto> updateProduct(
        @PathVariable Long productId,
        @RequestBody ProductUpdateRequestDto requestDto
    ) {
        ProductUpdateResponseDto responseDto = productService.updateProduct(productId, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 상품정보 상세조회(단건 조회)
     *
     * @param productId 상품 id
     * @return 정상 처리 시 ProductGetResponseDto
     */
    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductGetResponseDto> getProduct(
        @PathVariable Long productId
    ) {
        ProductGetResponseDto responseDto = productService.getProduct(productId);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 상품 삭제
     *
     * @param productId   상품 id
     * @param userDetails 사용자 정보
     * @return X
     */
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProduct(
        @PathVariable Long productId,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        productService.deleteProduct(productId, userDetails.getUsername());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 상품 전체 조회(로그인 없이도 사용가능, 조건별 검색 가능)
     *
     * @param productName 상품 이름
     * @param companyName 판매사 이름
     * @param category    상품 분류
     * @param page        페이지 번호
     * @param size        한페이지 게시글 수
     * @param sortBy      상품 정렬 조건(ex. productId, price)
     * @param isAsc       상품 정렬 순서(true: 오름차순, false: 내림차순)
     * @return 정상 처리 시 Page<ProductSearchResponseDto> (페이지로 반환된 dto)
     */
    @GetMapping("/products/search")
    public ResponseEntity<Page<ProductSearchResponseDto>> getAllProduct(
        @RequestParam(required = false) String productName,
        @RequestParam(required = false) String companyName,
        @RequestParam(required = false) String category,
        @RequestParam(value = "page", required = false, defaultValue = "1") int page,
        @RequestParam(value = "size", required = false, defaultValue = "20") int size,
        @RequestParam(value = "sortBy", required = false, defaultValue = "productId") String sortBy,
        @RequestParam(value = "isAsc", required = false, defaultValue = "false") boolean isAsc
    ) {
        page = page - 1;
        Page<ProductSearchResponseDto> searchProducts = productService.searchAndConvertProducts(productName, companyName, category, page, size, sortBy, isAsc);
        return new ResponseEntity<>(searchProducts, HttpStatus.OK);
    }

    /**
     * 판매자 상품 조회(자체 조회기능, 판매자에게 권한 한정)
     *
     * @param userDetails 사용자 정보
     * @param page        페이지 번호
     * @param size        한페이지 게시글 수
     * @param sortBy      상품 정렬 조건(ex. productId)
     * @param isAsc       상품 정렬 순서(true: 오름차순, false: 내림차순)
     * @return 정상 처리 시 Page<ProductSearchResponseDto> (페이지로 반환된 dto)
     */
    @GetMapping("/users/sellers/products")
    public ResponseEntity<Page<ProductSearchResponseDto>> getSellersProducts(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam(value = "page", required = false, defaultValue = "1") int page,
        @RequestParam(value = "size", required = false, defaultValue = "20") int size,
        @RequestParam(value = "sortBy", required = false, defaultValue = "productId") String sortBy,
        @RequestParam(value = "isAsc", required = false) boolean isAsc
    ) {
        page = page - 1;
        Page<ProductSearchResponseDto> findSellersProducts = productService.findSellersProducts(userDetails.getUsername(), page, size, sortBy, isAsc);
        return new ResponseEntity<>(findSellersProducts, HttpStatus.OK);
    }

    /**
     * 이미지 업로드
     *
     * @param productId 상품 id
     * @param image     사용자가 올리는 이미지파일
     * @return UploadFileInfo 반환되는 이미지 정보들
     */
    @PostMapping("/products/{productId}/images")
    public ResponseEntity<ImageUploadResponseDto> uploadImages(
        @PathVariable Long productId,
        @RequestPart(name = "image") MultipartFile image
    ) {
        ImageUploadResponseDto uploadFileInfo = productService.uploadImages(productId, image);
        return new ResponseEntity<>(uploadFileInfo, HttpStatus.OK);
    }

    /**
     * 이미지 삭제
     *
     * @param userDetails 사용자 정보
     * @param productId   상품 id
     * @param imageId     이미지 id
     * @return x
     */
    @DeleteMapping("/products/{productId}/images/{imageId}")
    public ResponseEntity<Void> deleteImage(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long productId,
        @PathVariable Long imageId
    ) {
        productService.deleteImage(productId, imageId, userDetails.getUsername());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 이미지 위치 수정
     *
     * @param userDetails 사용자 정보
     * @param productId   상품 id
     * @param requestDto  이미지 위치 수정 시 필요한 요청정보
     * @return UpdateImageResponseDto
     */
    @PutMapping("/products/{productId}/images")
    public ResponseEntity<UpdateImageResponseDto> updateImage(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long productId,
        @RequestBody UpdateImageRequestDto requestDto
    ) {
        UpdateImageResponseDto updateImage = productService.updateImages(productId, requestDto, userDetails.getUsername());
        return new ResponseEntity<>(updateImage, HttpStatus.OK);
    }

    /**
     * AI이미지 생성 및 S3 업로드, 위치 조정
     *
     * @param userDetails 사용자 정보
     * @param productId   상품 id
     * @return ImageUploadResponseDto 이미지 정보
     */
    @PostMapping("/products/{productId}/ai-image")
    public ResponseEntity<ImageUploadResponseDto> RepresentativeAiImage(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long productId
    ) {
        ImageUploadResponseDto imageUploadResponseDto = productService.setRepresentativeAiImage(productId, userDetails.getUsername());
        return new ResponseEntity<>(imageUploadResponseDto, HttpStatus.OK);
    }

    /**
     * 메인이미지 재업로드(Position1)
     *
     * @param productId 상품 id
     * @param MainImage 사용자가 올리는 대표이미지파일(1번 위치에 올릴 파일)
     * @return ImageUploadResponseDto 이미지 정보
     */
    @PostMapping("/products/{productId}/images/upload-main-image")
    public ResponseEntity<ImageUploadResponseDto> uploadMainImage(
        @PathVariable Long productId,
        @RequestParam(name = "image") MultipartFile MainImage
    ) {
        ImageUploadResponseDto uploadImageToPositionOne = productService.uploadImageToPositionOne(productId, MainImage);
        return new ResponseEntity<>(uploadImageToPositionOne, HttpStatus.OK);
    }

}
