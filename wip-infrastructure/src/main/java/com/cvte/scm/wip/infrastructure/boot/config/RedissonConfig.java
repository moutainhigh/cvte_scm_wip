package com.cvte.scm.wip.infrastructure.boot.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/8/29 09:06
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.port}")
    private String port;

    @Value("${spring.redis.database}")
    private Integer database;

    @Value("${spring.redis.pool.max-active}")
    private String poolMaxActive;

    @Value("${spring.redis.pool.max-wait}")
    private String poolMaxWait;

    @Value("${spring.redis.pool.max-idle}")
    private String poolMaxIdle;

    @Value("${spring.redis.pool.min-idle}")
    private String poolMinIdle;

    @Value("${spring.redis.timeout}")
    private Integer timeout;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        SingleServerConfig singleConfig = config.useSingleServer();
        singleConfig.setAddress(host + ":" + port)
                .setPassword(password)
                .setDatabase(database)
                .setTimeout(timeout);
        return Redisson.create(config);
    }

}
