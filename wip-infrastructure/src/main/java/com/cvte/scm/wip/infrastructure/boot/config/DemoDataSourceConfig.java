package com.cvte.scm.wip.infrastructure.boot.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.cvte.csb.toolkit.StringUtils;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 业务数据源配置
 *
 * @Author: wufeng
 * @Date: 2019/12/25 11:01
 */
@Configuration
public class DemoDataSourceConfig {

    @Value("${spring.busi.datasource.driver-class-name}")
    private String driveClassName ;
    @Value("${spring.busi.datasource.url}")
    private String url;
    @Value("${spring.busi.datasource.username}")
    private String userName;
    @Value("${spring.busi.datasource.password}")
    private String password;
/*
    @Value("${csb.mybatis.typeAliasesPackage:}")
    private String typeAliasesPackage;
    @Value("${csb.mybatis.dialect:}")
    private String dialect;*/

    @Value("${spring.busi.datasource.filters}")
    private String filters;
    @Value("${spring.busi.datasource.maxActive}")
    private String maxActive;
    @Value("${spring.busi.datasource.initialSize}")
    private String initialSize;
    @Value("${spring.busi.datasource.maxWait}")
    private String maxWait;
    @Value("${spring.busi.datasource.minIdle}")
    private String minIdle;
    @Value("${spring.busi.datasource.timeBetweenEvictionRunsMillis}")
    private String timeBetweenEvictionRunsMillis;
    @Value("${spring.busi.datasource.minEvictableIdleTimeMillis}")
    private String minEvictableIdleTimeMillis;
    @Value("${spring.busi.datasource.validationQuery}")
    private String validationQuery;
    @Value("${spring.busi.datasource.testWhileIdle}")
    private String testWhileIdle;
    @Value("${spring.busi.datasource.testOnBorrow}")
    private String testOnBorrow;
    @Value("${spring.busi.datasource.testOnReturn}")
    private String testOnReturn;
    @Value("${spring.busi.datasource.poolPreparedStatements}")
    private String poolPreparedStatements;
    @Value("${spring.busi.datasource.maxOpenPreparedStatements}")
    private String maxOpenPreparedStatements;

    /*@Value("${demo.mybatis.basePackage}")
    private String demoMybatisBasePackage;

    @Value("${demo.mybatis.xmlLocation}")
    private String demoMybatisXmlLocation;*/

    @Bean(name = "demoDataSource")
    public DataSource druidDataSource() {

        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(userName);
        druidDataSource.setPassword(password);
        druidDataSource.setDriverClassName(StringUtils.isNotBlank(driveClassName) ? driveClassName : "com.mysql.jdbc.Driver");
        druidDataSource.setMaxActive(StringUtils.isNotBlank(maxActive) ? Integer.parseInt(maxActive) : 10);
        druidDataSource.setInitialSize(StringUtils.isNotBlank(initialSize) ? Integer.parseInt(initialSize) : 1);
        druidDataSource.setMaxWait(StringUtils.isNotBlank(maxWait) ? Integer.parseInt(maxWait) : 60000);
        druidDataSource.setMinIdle(StringUtils.isNotBlank(minIdle) ? Integer.parseInt(minIdle) : 3);
        druidDataSource.setTimeBetweenEvictionRunsMillis(StringUtils.isNotBlank(timeBetweenEvictionRunsMillis) ?
                Integer.parseInt(timeBetweenEvictionRunsMillis) : 60000);
        druidDataSource.setMinEvictableIdleTimeMillis(StringUtils.isNotBlank(minEvictableIdleTimeMillis) ?
                Integer.parseInt(minEvictableIdleTimeMillis) : 300000);
        druidDataSource.setValidationQuery(StringUtils.isNotBlank(validationQuery) ? validationQuery : "select 'x'");
        druidDataSource.setTestWhileIdle(StringUtils.isNotBlank(testWhileIdle) ? Boolean.parseBoolean(testWhileIdle) : true);
        druidDataSource.setTestOnBorrow(StringUtils.isNotBlank(testOnBorrow) ? Boolean.parseBoolean(testOnBorrow) : false);
        druidDataSource.setTestOnReturn(StringUtils.isNotBlank(testOnReturn) ? Boolean.parseBoolean(testOnReturn) : false);
        druidDataSource.setPoolPreparedStatements(StringUtils.isNotBlank(poolPreparedStatements) ? Boolean.parseBoolean(poolPreparedStatements) : true);
        try {
            druidDataSource.setMaxOpenPreparedStatements(StringUtils.isNotBlank(maxOpenPreparedStatements) ? Integer.parseInt(maxOpenPreparedStatements) : 20);
            druidDataSource.setFilters(StringUtils.isNotBlank(filters) ? filters : "stat, wall");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return druidDataSource;
    }


    @Bean(name = "demoSqlSessionFactory")
    public SqlSessionFactory testSqlSessionFactory(@Qualifier("demoDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);


        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        //properties.setProperty("dialect", dialect);
        properties.setProperty("reasonable", "false");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("returnPageInfo", "check");
        properties.setProperty("params", "count=countSql");
        pageHelper.setProperties(properties);

        bean.setPlugins(new Interceptor[]{pageHelper});

        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/oracle/**/*.xml"));

        /***
         * 必须在setMapperLocations 方法后设置配置
         */
        SqlSessionFactory sqlSessionFactory = bean.getObject();
        sqlSessionFactory.getConfiguration().setJdbcTypeForNull(JdbcType.NULL);
        sqlSessionFactory.getConfiguration().setMapUnderscoreToCamelCase(true);
        return sqlSessionFactory;
    }

    @Bean(name = "demoTransactionManager")
    public DataSourceTransactionManager testTransactionManager(@Qualifier("demoDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "demoSqlSessionTemplate")
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("demoSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
