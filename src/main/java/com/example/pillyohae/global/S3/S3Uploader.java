package com.example.pillyohae.global.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.pillyohae.global.dto.UploadFileInfo;
import com.example.pillyohae.global.exception.BaseException;
import com.example.pillyohae.global.exception.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Uploader {
    private final AmazonS3 s3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public static final String DATE_FORMAT_YYYYMMDD = "yyyy/MM/dd";

    public UploadFileInfo uploadFile(MultipartFile file) {
        ObjectMetadata metadata = createMetaDataFromFile(file);
        String filePath = createFilePath(file.getContentType(), file.getName());
        try (InputStream inputStream = file.getInputStream()) {
            // S3에 파일 업로드
            s3.putObject(
                new PutObjectRequest(bucket, filePath, inputStream, metadata)
            );
        } catch (Exception e) {
            throw new BaseException(ErrorCode.S3_UPLOADER_ERROR);
        }
        return new UploadFileInfo(getUrlFromBucket(filePath), filePath);
    }

    // s3에 들어갈 파일의 path 설정
    private String createFilePath(String contentType, String fileName) {
//        LocalDate now = LocalDate.now();
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT_YYYYMMDD);
        String randomUUID = UUID.randomUUID().toString();
        String cleanedUUID = randomUUID.replace("-", "");
        cleanedUUID = cleanedUUID.substring(0, 16);
        StringBuilder filePath = new StringBuilder(contentType);
        filePath.append(fileName).append(cleanedUUID);

        return filePath.toString();
    }


    private ObjectMetadata createMetaDataFromFile(MultipartFile file) {
        String contentType = getContentType(file);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(file.getSize());
        return metadata;
    }

    // 유효한 타입인지 검증 후 contentType 반환
    private String getContentType(MultipartFile file) {
        String contentType = file.getContentType();
        Boolean isImage = ("image/jpeg").equals(contentType) || ("image/png").equals(contentType);
        if (contentType == null || !(isImage)) {
            throw new BaseException(ErrorCode.BAD_FORMAT);
        }
        return contentType;
    }

    // 저장된 파일의 URL을 가져옴
    private String getUrlFromBucket(String fileKey) {
        try {
            return s3.getUrl(bucket, fileKey).toString();
        } catch (Exception e) {
            throw new BaseException(ErrorCode.S3_UPLOADER_ERROR);
        }
    }

    public UploadFileInfo uploadFileFromUrl(String imageUrl) {
        try {
            // URL에서 이미지 다운로드
            URL url = new URL(imageUrl);
            InputStream inputStream = url.openStream();

            // 파일 키 생성
            String fileKey = "ai-images/" + UUID.randomUUID().toString();

            // S3에 업로드
            ObjectMetadata metadata = new ObjectMetadata();
            s3.putObject(bucket, fileKey, inputStream, metadata);

            // 업로드된 파일의 URL 반환
            String fileUrl = s3.getUrl(bucket, fileKey).toString();

            // UploadFileInfo 반환
            return new UploadFileInfo(fileUrl, fileKey);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file from URL to S3", e);
        }
    }
}



