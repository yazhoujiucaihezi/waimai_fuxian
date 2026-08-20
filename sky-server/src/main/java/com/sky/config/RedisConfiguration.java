//package com.sky.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//import com.sky.vo.DishVO;
//
//@Configuration
//public class RedisConfiguration {
//
//    @Bean
//    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
//
//        RedisTemplate redisTemplate = new RedisTemplate();
//
//        redisTemplate.setConnectionFactory(redisConnectionFactory);
//
//        // key
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//
//        // value
//        Jackson2JsonRedisSerializer serializer =
//                new Jackson2JsonRedisSerializer(Object.class);
//
//        redisTemplate.setValueSerializer(serializer);
//
//        // hash key
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//
//        // hash value
//        redisTemplate.setHashValueSerializer(serializer);
//
//        return redisTemplate;
//    }
//}