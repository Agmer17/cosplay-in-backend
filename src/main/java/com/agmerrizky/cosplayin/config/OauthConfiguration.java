package com.agmerrizky.cosplayin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;

@Configuration
public class OauthConfiguration {

    @Value("${GOOGLE_OAUTH_CLIENT}")
    private String googleClientId;

    @Value("${GOOGLE_OAUTH_SECRET}")
    private String googleClientSecret;

    private final String googleCallbackUrl = "http://localhost/api/auth/google-callback";

    @Bean
    OAuth20Service googleOauthService() {
        return new ServiceBuilder(googleClientId)
                .apiSecret(googleClientSecret)
                .defaultScope("email profile")
                .callback(googleCallbackUrl)
                .build(GoogleApi20.instance());
    }

}
