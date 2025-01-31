package com.example.pillyohae.product.service;

import com.example.pillyohae.global.exception.CustomResponseStatusException;
import com.example.pillyohae.global.exception.code.ErrorCode;
import com.example.pillyohae.product.dto.NutrientCreateRequestDto;
import com.example.pillyohae.product.dto.NutrientResponseDto;
import com.example.pillyohae.product.entity.Nutrient;
import com.example.pillyohae.product.repository.NutrientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NutrientService {

    private final NutrientRepository nutrientRepository;

    // 모든 영양소 조회 (상품생성시)
    public List<NutrientResponseDto> findAll() {

        List<Nutrient> nutrients = nutrientRepository.findAllOrderedByName();
        return nutrients
            .stream()
            .map(nutrient -> new NutrientResponseDto(
                nutrient.getNutrientId(),
                nutrient.getName(),
                nutrient.getDescription()
            ))
            .toList();
    }

    /**
     * 주요 성분 추가 (admin)
     *
     * @param requestDto
     * @return
     */
    @Transactional
    public NutrientResponseDto createNutrient(NutrientCreateRequestDto requestDto) {

        // 중복 이름 체크
        if (nutrientRepository.existsByName(requestDto.getName())) {
            throw new CustomResponseStatusException(ErrorCode.DUPLICATE_NUTRIENT_NAME);
        }
        Nutrient nutrient = new Nutrient(requestDto.getName(), requestDto.getDescription());
        Nutrient savedNutrient = nutrientRepository.save(nutrient);

        return new NutrientResponseDto(
            savedNutrient.getNutrientId(),
            savedNutrient.getName(),
            savedNutrient.getDescription()
        );
    }
}
