package com.fraudedetection.detecaofraudeanaliseriscos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import com.redislabs.redisgraph.RedisGraphContext;

@Configuration
public class FalkorDBConfig {

    @Value("${spring.falkordb.host:localhost}")
    private String host;

    @Value("${spring.falkordb.port:6379}")
    private int port;

    @Value("${spring.falkordb.password:}")
    private String password;

    @Value("${spring.falkordb.graph.name:fraude}")
    private String graphName;

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);

        if (password != null && !password.isEmpty()) {
            return new JedisPool(poolConfig, host, port, 2000, password);
        } else {
            return new JedisPool(poolConfig, host, port, 2000);
        }
    }

    @Bean
    public RedisGraphContext redisGraphContext(JedisPool jedisPool) {
        return new RedisGraphContext(jedisPool, graphName);
    }
}