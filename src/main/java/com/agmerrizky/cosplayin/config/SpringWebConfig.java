package com.agmerrizky.cosplayin.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.agmerrizky.cosplayin.common.resolver.CurrentUserResolver;
import com.agmerrizky.cosplayin.middleware.AuthMiddleware;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SpringWebConfig implements WebMvcConfigurer {
    private final AuthMiddleware authMiddleware;
    private final CurrentUserResolver currResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authMiddleware).addPathPatterns("/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currResolver);
    }
}
