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
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

@Configuration
public class RedisConfiguration {

        @Bean
        RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
                RedisTemplate<String, Object> template = new RedisTemplate<>();
                template.setConnectionFactory(factory);

                // Jackson 3 serializer untuk RedisTemplate (typed, pakai
                // JacksonJsonRedisSerializer)
                JacksonJsonRedisSerializer<Object> serializer = new JacksonJsonRedisSerializer<>(
                                JsonMapper.builder()
                                                .activateDefaultTyping(
                                                                BasicPolymorphicTypeValidator.builder()
                                                                                .allowIfBaseType(Object.class)
                                                                                .build(),
                                                                DefaultTyping.NON_FINAL,
                                                                JsonTypeInfo.As.PROPERTY)
                                                .build(),
                                Object.class);

                template.setKeySerializer(new StringRedisSerializer());
                template.setHashKeySerializer(new StringRedisSerializer());
                template.setValueSerializer(serializer);
                template.setHashValueSerializer(serializer);
                template.afterPropertiesSet();

                return template;
        }

        @Bean
        public CacheManager cacheManager(RedisConnectionFactory factory) {

                JsonMapper mapper = JsonMapper.builder()
                                .activateDefaultTyping(
                                                BasicPolymorphicTypeValidator.builder()
                                                                .allowIfBaseType(Object.class)
                                                                .build(),
                                                DefaultTyping.NON_FINAL,
                                                JsonTypeInfo.As.PROPERTY)
                                .build();

                GenericJacksonJsonRedisSerializer genericSerializer = new GenericJacksonJsonRedisSerializer(mapper);
                RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(10))
                                .serializeKeysWith(
                                                RedisSerializationContext.SerializationPair
                                                                .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(
                                                RedisSerializationContext.SerializationPair
                                                                .fromSerializer(genericSerializer));
                // Hapus disableCachingNullValues() karena sudah pakai
                // enableSpringCacheNullValueSupport()

                Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
                cacheConfigs.put("users", defaultConfig.entryTtl(Duration.ofHours(24)));

                return RedisCacheManager.builder(factory)
                                .cacheDefaults(defaultConfig)
                                .withInitialCacheConfigurations(cacheConfigs)
                                .build();
        }
}