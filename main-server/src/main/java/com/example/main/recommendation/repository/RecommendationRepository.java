package com.example.main.recommendation.repository;

import com.example.common.recommendation.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long>, RecommendationQueryRepository {

}
