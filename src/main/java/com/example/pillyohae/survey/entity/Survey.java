package com.example.pillyohae.survey.entity;

import com.example.pillyohae.global.entity.BaseCreatedTimeEntity;
import com.example.pillyohae.recommendation.entity.Recommendation;
import com.example.pillyohae.survey.dto.SurveySubmitRequestDto;
import com.example.pillyohae.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
@Entity
public class Survey extends BaseCreatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // 기본 정보
    private Integer age;
    private String gender;
    private String height;
    private String weight;

    // 건강 목표 및 상태
    // healthGoals는 DB에 하나의 문자열로 저장 (콤마로 구분)
    private String healthGoals;
    private String healthCondition;

    // 생활습관 정보는 DB에 문자열로 저장 (예: JSON 또는 key=value 형식)
    private String lifestyle;

    // 추가: 추천 이유 (한 설문당 하나)
    private String recommendationReason;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Recommendation> recommendation = new ArrayList<>();

    public Survey() {
    }

    public Survey(User user, SurveySubmitRequestDto requestDto) {
        this.user = user;
        this.age = requestDto.getAge();
        this.gender = requestDto.getGender();
        this.height = requestDto.getHeight();
        this.weight = requestDto.getWeight();
        // healthGoals는 리스트를 콤마로 구분한 문자열로 변환
        this.healthGoals = requestDto.getHealthGoals() != null
            ? String.join(",", requestDto.getHealthGoals()) : "";
        this.healthCondition = requestDto.getHealthCondition() != null
            ? requestDto.getHealthCondition() : "";
        // lifestyle 정보를 JSON 문자열로 저장하거나 원하는 포맷으로 변환 (여기서는 toString() 사용)
        this.lifestyle = requestDto.getLifestyle() != null
            ? requestDto.getLifestyle().toString() : "";
    }

    public void updateRecommendationReason(String recommendationReason) {
        this.recommendationReason = recommendationReason;
    }
}
