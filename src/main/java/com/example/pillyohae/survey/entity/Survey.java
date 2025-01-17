package com.example.pillyohae.survey.entity;

import com.example.pillyohae.global.entity.BaseCreatedTimeEntity;
import com.example.pillyohae.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Getter
@Entity
public class Survey extends BaseCreatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String categories;

    public Survey() {
    }

    public Survey(User user, String categories) {
        this.user = user;
        this.categories = categories;
    }


}
