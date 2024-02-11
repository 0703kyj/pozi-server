package com.pozi.naver.config;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NaverConfig {
    @Bean
    public Gson gson() {
        return new Gson();
    }
}
