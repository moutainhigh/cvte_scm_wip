package com.cvte.scm.wip.infrastructure.boot.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import tk.mybatis.spring.mapper.MapperScannerConfigurer;

import java.util.Properties;

/**
 * 业务数据源mapper扫包
 *
 * @Author: wufeng
 * @Date: 2019/12/25 11:04
 */
@Configuration
@AutoConfigureAfter(WipDataSourceConfig.class)
public class WipMapperConfigurer implements EnvironmentAware {

    @Bean(name = "pgMapperScannerConfigurer")
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("pgSqlSessionFactory");
        mapperScannerConfigurer.setBasePackage("com.cvte.scm.wip.infrastructure.**.mapper");
        Properties properties = new Properties();
        properties.setProperty("ORDER", "BEFORE");
        properties.setProperty("mappers", "tk.mybatis.mapper.common.Mapper,com.cvte.csb.jdbc.mybatis.mapper.IdsMapper,com.cvte.csb.jdbc.mybatis.mapper.BatchMapper");
        mapperScannerConfigurer.setProperties(properties);
        return mapperScannerConfigurer;
    }

    @Override
    public void setEnvironment(Environment environment) {

    }
}
