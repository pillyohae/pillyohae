package com.example.main.product.service;

import com.example.common.product.entity.Category;
import com.example.main.product.dto.category.CategoryResponseDto;
import com.example.main.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 모든 카테고리 조회 (상품생성시)
     *
     * @return List<CategoryResponseDto> 카테고리 리스트
     */
    public List<CategoryResponseDto> findAll() {

        List<Category> categories = categoryRepository.findAllOrderByName();
        return categories
            .stream()
            .map(category -> new CategoryResponseDto(
                category.getCategoryId(),
                category.getName()
            ))
            .toList();
    }
}
