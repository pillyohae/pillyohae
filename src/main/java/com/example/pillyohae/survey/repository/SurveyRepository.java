package com.example.pillyohae.survey.repository;

import com.example.pillyohae.survey.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long>, SurveyQueryRepository {

}
