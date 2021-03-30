package com.example.demo;

import java.io.IOException;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

@SpringBootApplication
public class DemoApplication {
  @Bean(destroyMethod = "shutdown")
  public RedissonClient redissonClient(@Value("classpath:/redisson.yaml") Resource configFile) throws IOException {
    Config config = Config.fromYAML(configFile.getInputStream());
    RedissonClient redissonClient = Redisson.create(config);
    redissonClient.getKeys().flushall();
    return redissonClient;
  }

  @Bean
  public CacheManager cacheManager(RedissonClient redissonClient) {

    return new RedissonSpringCacheManager(redissonClient, "classpath:/redis_region_settings.yaml") {

      @Override
      protected CacheConfig createDefaultConfig() {
        int ttl = 30 * 60 * 1000; // 30 min
        int maxIdleTime = 10 * 60 * 1000; // 10 min
        int maxSize = 1000;
        CacheConfig cacheConfig = new CacheConfig(ttl, maxIdleTime);
        cacheConfig.setMaxSize(maxSize);
        return cacheConfig;
      }
    };
  }

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }

}
