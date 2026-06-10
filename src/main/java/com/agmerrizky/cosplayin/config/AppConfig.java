package com.agmerrizky.cosplayin.config;

import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tools.jackson.databind.ObjectMapper;

@Configuration
public class AppConfig {

        @Bean
        ObjectMapper objectMapper() {
                return new ObjectMapper();
        }

        @Bean
        Tika tika() {
                return new Tika();
        }

}
