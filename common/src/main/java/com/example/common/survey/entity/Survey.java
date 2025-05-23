package com.example.common.survey.entity;


import com.example.common.global.entity.BaseCreatedTimeEntity;
import com.example.common.recommendation.entity.Recommendation;
import com.example.common.user.entity.User;
import jakarta.persistence.*;
import lombok.Generated;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
public class Survey extends BaseCreatedTimeEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    private User user;
    private Integer age;
    private String gender;
    private String height;
    private String weight;
    private String healthGoals;
    private String healthCondition;
    private String lifestyle;
    private String recommendationReason;
    @OneToMany(
            mappedBy = "survey",
            cascade = {CascadeType.REMOVE},
            orphanRemoval = true
    )
    private List<Recommendation> recommendation = new ArrayList();

    public Survey() {
    }

    public Survey(User user, Integer age, String gender, String height, String weight, List<String> healthGoals, String healthCondition, Map<String, String> lifestyle) {
        this.user = user;
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.healthGoals = healthGoals != null ? String.join(",", healthGoals) : "";
        this.healthCondition = healthCondition != null ? healthCondition : "";
        this.lifestyle = lifestyle != null ? lifestyle.toString() : "";
    }

    public void updateRecommendationReason(String recommendationReason) {
        this.recommendationReason = recommendationReason;
    }

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public User getUser() {
        return this.user;
    }

    @Generated
    public Integer getAge() {
        return this.age;
    }

    @Generated
    public String getGender() {
        return this.gender;
    }

    @Generated
    public String getHeight() {
        return this.height;
    }

    @Generated
    public String getWeight() {
        return this.weight;
    }

    @Generated
    public String getHealthGoals() {
        return this.healthGoals;
    }

    @Generated
    public String getHealthCondition() {
        return this.healthCondition;
    }

    @Generated
    public String getLifestyle() {
        return this.lifestyle;
    }

    @Generated
    public String getRecommendationReason() {
        return this.recommendationReason;
    }

    @Generated
    public List<Recommendation> getRecommendation() {
        return this.recommendation;
    }
}
