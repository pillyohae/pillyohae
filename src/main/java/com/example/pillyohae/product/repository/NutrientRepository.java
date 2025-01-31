package com.example.pillyohae.product.repository;

import com.example.pillyohae.product.entity.Nutrient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NutrientRepository extends JpaRepository<Nutrient, Long> {

    @Query("SELECT n FROM Nutrient n ORDER BY n.name ASC")
    List<Nutrient> findAllOrderedByName();
}
