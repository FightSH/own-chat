package com.shen.chat.data.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineCacheConfig {


    @Bean(name = "codeCache")
    public Cache<String, String> codeCache() {

        return Caffeine.newBuilder().expireAfterWrite(3, TimeUnit.MINUTES).build();

    }

}
