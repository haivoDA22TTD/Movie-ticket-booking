package com.cinema.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

@Configuration
public class RedisConfigCustom {
    
    @Value("${REDIS_PUBLIC_URL:}")
    private String redisPublicUrl;
    
    @Value("${REDIS_HOST:localhost}")
    private String redisHost;
    
    @Value("${REDIS_PORT:6379}")
    private int redisPort;
    
    @Value("${REDIS_PASSWORD:}")
    private String redisPassword;
    
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config;
        
        // Nếu có REDIS_PUBLIC_URL thì parse từ URL
        if (redisPublicUrl != null && !redisPublicUrl.isEmpty()) {
            try {
                URI uri = new URI(redisPublicUrl);
                
                String host = uri.getHost();
                int port = uri.getPort() > 0 ? uri.getPort() : 6379;
                String password = "";
                
                if (uri.getUserInfo() != null) {
                    String[] userInfo = uri.getUserInfo().split(":");
                    password = userInfo.length > 1 ? userInfo[1] : userInfo[0];
                }
                
                config = new RedisStandaloneConfiguration(host, port);
                if (!password.isEmpty()) {
                    config.setPassword(password);
                }
            } catch (URISyntaxException e) {
                throw new RuntimeException("Invalid REDIS_PUBLIC_URL format", e);
            }
        } else {
            // Fallback: Dùng các biến riêng lẻ
            config = new RedisStandaloneConfiguration(redisHost, redisPort);
            if (redisPassword != null && !redisPassword.isEmpty()) {
                config.setPassword(redisPassword);
            }
        }
        
        return new LettuceConnectionFactory(config);
    }
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJackson2JsonRedisSerializer()
                )
            );
        
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .build();
    }
}
