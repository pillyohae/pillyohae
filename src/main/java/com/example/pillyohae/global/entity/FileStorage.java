package com.example.pillyohae.global.entity;

import com.example.pillyohae.product.entity.Product;
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
public class FileStorage {
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

    @CreatedDate
    @Column
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product-id")
    private Product product;


    @Builder
    public FileStorage(String fileUrl, String fileKey, String contentType, Long fileSize, Product product) {
        this.fileUrl = fileUrl;
        this.fileKey = fileKey;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.product = product;


    }
}
