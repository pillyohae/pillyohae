package com.example.pillyohae.product.repository;

import com.example.pillyohae.global.entity.FileStorage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageStorageRepository extends JpaRepository<FileStorage, Long> {
}
