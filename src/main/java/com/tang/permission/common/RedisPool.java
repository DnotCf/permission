package com.tang.permission.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import javax.annotation.Resource;


@Slf4j
public class RedisPool {


    @Resource(name = "shardedJedisPool")
    private ShardedJedisPool shardedJedisPool; //获得spring管理的连接池

    public ShardedJedis instance() {
        return shardedJedisPool.getResource(); //获得连接
    }

    public void safeClose(ShardedJedis shardedJedis) {

        try {

            if (shardedJedis != null) {
                shardedJedis.close();

            }

        } catch (Exception e) {
            log.error("redis close exception,exception:{}", e);
        }

    }
}
