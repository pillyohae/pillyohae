package com.example.pillyohae.recommendation.repository;

import com.example.pillyohae.recommendation.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long>, RecommendationQueryRepository {

}
