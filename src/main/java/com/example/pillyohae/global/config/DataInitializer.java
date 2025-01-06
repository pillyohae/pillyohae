package com.example.pillyohae.global.config;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataInitializer {

    //아무거나
    /**
     * 어플리케이션이 실행할때 동작하는 메소드
     */
    @PostConstruct
    public void init(){

    }


}