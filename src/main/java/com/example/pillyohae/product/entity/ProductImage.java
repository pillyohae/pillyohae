package com.example.pillyohae.product.entity;

import com.example.pillyohae.global.exception.CustomResponseStatusException;
import com.example.pillyohae.global.exception.code.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String fileUrl;

    @Column
    @Lob
    private String fileKey;

    @Column
    private String contentType;

    @Column
    private Long fileSize;

    @Column
    private Integer position;

    @CreatedDate
    @Column
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;


    @Builder
    public ProductImage(String fileUrl, String fileKey, String contentType, Long fileSize, Integer position, Product product) {
        this.fileUrl = fileUrl;
        this.fileKey = fileKey;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.position = position;
        this.product = product;
    }

    public void updatePosition(Integer position) {
        if (position < 0) {
            throw new CustomResponseStatusException(ErrorCode.BAD_POSITION);
        }
        this.position = position;
    }

    public void downPosition() {
        if (this.position > 0) {
            this.position--;
        }
    }

    public void upPosition() {

        this.position++;
    }
}
