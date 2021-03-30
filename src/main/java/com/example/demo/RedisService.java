package com.example.demo;

import java.util.Set;
import org.redisson.RedissonMap;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class RedisService {

  @Autowired
  private CacheManager cacheManager;
  @Autowired
  private RedissonClient redissonClient;

  public void putToRegion(String region, String key, Object value) {
    cacheManager.getCache(region).put(key, value);
  }

  public Set<String> findKeysInRegion(String region, String pattern) {
    return ((RMap<String, Object>) cacheManager.getCache(region).getNativeCache()).keySet(pattern);
  }

  public void deleteKeysInRegion_UsingSpringCacheManagerWrapper(String region, String key) {
    ((RedissonMap<String, Object>) cacheManager.getCache(region).getNativeCache())
        .fastRemove(key);
  }

  public void deleteKeysInRegion(String region, String key) {
    RMapCache<String, Object> mapCache = redissonClient.getMapCache(region);
    mapCache.remove(key);
  }
}
