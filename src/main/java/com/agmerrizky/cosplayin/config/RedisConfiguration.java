package com.agmerrizky.cosplayin.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {

        @Bean
        RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory,
                        GenericJacksonJsonRedisSerializer serializer) {
                RedisTemplate<String, Object> template = new RedisTemplate<>();
                template.setConnectionFactory(factory);
                template.setKeySerializer(new StringRedisSerializer());
                template.setHashKeySerializer(new StringRedisSerializer());
                template.setValueSerializer(serializer);
                template.setHashValueSerializer(serializer);
                template.afterPropertiesSet();
                return template;
        }

        @Bean
        GenericJacksonJsonRedisSerializer genericJacksonJsonRedisSerializer() {
                GenericJacksonJsonRedisSerializer serializer = GenericJacksonJsonRedisSerializer.builder()
                                .enableUnsafeDefaultTyping()
                                .build();

                return serializer;
        }

        @Bean
        CacheManager cacheManager(RedisConnectionFactory factory, GenericJacksonJsonRedisSerializer serializer) {
                RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(10))
                                .serializeKeysWith(
                                                RedisSerializationContext.SerializationPair
                                                                .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(
                                                RedisSerializationContext.SerializationPair
                                                                .fromSerializer(serializer))
                                .disableCachingNullValues();

                Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
                cacheConfigs.put("users", defaultConfig.entryTtl(Duration.ofHours(24)));

                return RedisCacheManager.builder(factory)
                                .cacheDefaults(defaultConfig)
                                .withInitialCacheConfigurations(cacheConfigs)
                                .build();
        }
}