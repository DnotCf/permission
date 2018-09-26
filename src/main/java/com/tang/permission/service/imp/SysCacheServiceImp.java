package com.tang.permission.service.imp;

import com.google.common.base.Joiner;
import com.tang.permission.bean.CachKeyConstans;
import com.tang.permission.common.RedisPool;
import com.tang.permission.service.SysCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;

import javax.annotation.Resource;

@Service
@Slf4j
public class SysCacheServiceImp implements SysCacheService {

    @Autowired
    private RedisPool redisPool;

    @Override
    public void saveCache(String toSaveValue, int timeoutSeconds, CachKeyConstans perfix) {
        saveCache(toSaveValue, timeoutSeconds, perfix,null);
    }

    @Override
    public void saveCache(String toSaveValue, int timeoutSeconds, CachKeyConstans perfix, String... keys) {

        if (StringUtils.isBlank(toSaveValue)) {
            return;
        }
        ShardedJedis shardedJedis = null;
        try {
            String cacheKey = generateCacheKey(perfix, keys);
            shardedJedis = redisPool.instance();
            shardedJedis.setex(cacheKey, timeoutSeconds, toSaveValue); //带过期时间的

        } catch (Exception e) {
            log.error("save cache Exception,perfix:{},keys:{}", perfix, keys);
        }finally {
            redisPool.safeClose(shardedJedis);

        }


    }

    @Override
    public String getFromCache(CachKeyConstans prefix, String... keys) {

        if (keys == null || keys.length == 0) {

            return null;
        }
        ShardedJedis shardedJedis = null;
        String cacheValue = null;
        try {
            String cacheKey = generateCacheKey(prefix, keys);
            shardedJedis = redisPool.instance();
            cacheValue= shardedJedis.get(cacheKey); //带过期时间的
            return cacheValue;
        } catch (Exception e) {
            log.error("get cache Exception,perfix:{},keys:{}", prefix, keys);

            return null;
        }finally {
            redisPool.safeClose(shardedJedis);

        }
    }

    public String generateCacheKey(CachKeyConstans prefix, String... keys) {

        String key = prefix.name();

        if (keys != null && keys.length > 0) {
            key = key + "_" + Joiner.on("_").join(keys);
        }
        return key;
    }
}
