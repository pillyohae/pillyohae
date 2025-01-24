package com.example.pillyohae.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /**
     * Server
     */
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "유효성 검사 실패"),
    CONSTRAINT_VIOLATION(HttpStatus.CONFLICT, "제약 조건 위반"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생하였습니다."),

    /**
     * Common
     */
    NOT_FOUND_ENUM_CONSTANT(HttpStatus.NOT_FOUND, "열거형 상수값을 찾을 수 없습니다."),
    IS_NULL(HttpStatus.BAD_REQUEST, "NULL 값이 들어왔습니다."),
    COMMON_INVALID_PARAM(HttpStatus.BAD_REQUEST, "요청한 값이 올바르지 않습니다."),
    NO_SUCH_METHOD(HttpStatus.BAD_REQUEST, "메소드를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 올바르지 않습니다."),
    FORBIDDEN_DELETED_USER_LOGIN(HttpStatus.BAD_REQUEST, "탈퇴한 사용자는 로그인할 수 없습니다."),

    /**
     * NotFound
     */
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "User를 찾을 수 없습니다"),
    NOT_FOUND_File(HttpStatus.NOT_FOUND, "File을 찾을 수 없습니다"),
    NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND, "Product를 찾을 수 없습니다"),
    NOT_FOUND_PROPERTY(HttpStatus.NOT_FOUND, "정렬기준을 확인해주세요"),
    NOT_FOUND_IMAGE_POSITION1(HttpStatus.NOT_FOUND, "AI이미지로 변환할 대표이미지가 없습니다."),
    /**
     * Image
     */
    S3_UPLOADER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3 업로드 중 오류가 발생하였습니다."),
    BAD_FORMAT(HttpStatus.BAD_REQUEST, "허용된 파일 형식이 아닙니다."),
    INVALID_IMAGE_PRODUCT_MATCH(HttpStatus.BAD_REQUEST, "상품에 해당하는 이미지가 아닙니다."),
    CANNOT_OVERLOAD_FILE(HttpStatus.BAD_REQUEST, "이미지는 최대 5개까지 업로드할 수 있습니다"),
    BAD_POSITION(HttpStatus.BAD_REQUEST, "위치 값은 0보다 작을 수 없습니다."),

    /**
     * BadRequest
     */
    STOCK_CANNOTBE_NEGATIVE(HttpStatus.BAD_REQUEST, "재고는 0보다 작을 수 없습니다."),
    QUANTITY_CANNOTBE_NEGATIVE(HttpStatus.BAD_REQUEST, "주문량을 올바르게 입력해주세요."),
    LACK_OF_STOCK(HttpStatus.BAD_REQUEST, "제품의 재고가 부족합니다"),

    /**
     * unAuthorized
     */
    NOT_ALLOW_USER(HttpStatus.UNAUTHORIZED, "USER 권한은 사용할 수 없는 기능입니다"),
    FORBIDDEN_ADMIN_ROLE_REQUIRED(HttpStatus.UNAUTHORIZED, "ADMIN 권한만 사용할 수 있는 기능입니다"),

    /**
     * Jwt error
     */
    UNAUTHORIZED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 비어있거나 만료되었거나 지원하지않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
