package com.example.pillyohae.recommendation.entity;

import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.survey.entity.Survey;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Getter
@Entity
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    public Recommendation(Survey survey, Product product) {
        this.survey = survey;
        this.product = product;
    }

    public Recommendation() {

    }
}
