package com.example.pillyohae.product.service;

import com.example.pillyohae.product.dto.CategoryResponseDto;
import com.example.pillyohae.product.entity.Category;
import com.example.pillyohae.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

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
