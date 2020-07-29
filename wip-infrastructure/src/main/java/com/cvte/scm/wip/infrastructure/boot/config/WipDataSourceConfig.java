package com.cvte.scm.wip.infrastructure.boot.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.infrastructure.deprecated.BaseBatchMapper;
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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 业务数据源配置
 *
 * @author : wufeng
 * Date: 2019/12/25 11:01
 */
@Configuration
public class WipDataSourceConfig {

    @Value("${spring.busi.datasource.driver-class-name}")
    private String driveClassName;
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

    @Value("${spring.datasource.aps.driver-class-name}")
    private String apsDriveClassName;
    @Value("${spring.datasource.aps.url}")
    private String apsUrl;
    @Value("${spring.datasource.aps.username}")
    private String apsUserName;
    @Value("${spring.datasource.aps.password}")
    private String apsPassword;

    /*@Value("${demo.mybatis.basePackage}")
    private String demoMybatisBasePackage;

    @Value("${demo.mybatis.xmlLocation}")
    private String demoMybatisXmlLocation;*/

    @Bean(name = "pgDataSource")
    @Qualifier("pgDataSource")
    public DataSource druidDataSource() {
        return createDataSource(url, driveClassName, userName, password);
    }

    @Bean(name = "pgSqlSessionFactory")
    public SqlSessionFactory testSqlSessionFactory(@Qualifier("pgDataSource") DataSource dataSource) throws Exception {
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

        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mybatis/mapper/**/*.xml"));

        // 必须在setMapperLocations 方法后设置配置
        SqlSessionFactory sqlSessionFactory = bean.getObject();
        sqlSessionFactory.getConfiguration().setJdbcTypeForNull(JdbcType.NULL);
        sqlSessionFactory.getConfiguration().setMapUnderscoreToCamelCase(true);
        return sqlSessionFactory;
    }

    @Bean(name = "pgTransactionManager")
    public DataSourceTransactionManager testTransactionManager(@Qualifier("pgDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "pgBatchMapper")
    public BaseBatchMapper namedParameterJdbcTemplate(@Qualifier("pgDataSource") DataSource pgScmDataSource) {
        return new BaseBatchMapper(new NamedParameterJdbcTemplate(pgScmDataSource));
    }

    @Bean(name = "pgSqlSessionTemplate")
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("pgSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(name = "pgParameterJdbcTemplate")
    public NamedParameterJdbcTemplate pgParameterJdbcTemplate(@Qualifier("pgDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    private DataSource createDataSource(String url, String driverClassName, String userName, String password) {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(userName);
        druidDataSource.setPassword(password);
        druidDataSource.setDriverClassName(StringUtils.isNotBlank(driverClassName) ? driverClassName : "com.mysql.jdbc.Driver");
        druidDataSource.setMaxActive(StringUtils.isNotBlank(maxActive) ? Integer.parseInt(maxActive) : 10);
        druidDataSource.setInitialSize(StringUtils.isNotBlank(initialSize) ? Integer.parseInt(initialSize) : 1);
        druidDataSource.setMaxWait(StringUtils.isNotBlank(maxWait) ? Integer.parseInt(maxWait) : 60000);
        druidDataSource.setMinIdle(StringUtils.isNotBlank(minIdle) ? Integer.parseInt(minIdle) : 3);
        druidDataSource.setTimeBetweenEvictionRunsMillis(StringUtils.isNotBlank(timeBetweenEvictionRunsMillis) ?
                Integer.parseInt(timeBetweenEvictionRunsMillis) : 60000);
        druidDataSource.setMinEvictableIdleTimeMillis(StringUtils.isNotBlank(minEvictableIdleTimeMillis) ?
                Integer.parseInt(minEvictableIdleTimeMillis) : 300000);
        druidDataSource.setValidationQuery(StringUtils.isNotBlank(validationQuery) ? validationQuery : "select 'x'");
        druidDataSource.setTestWhileIdle(!StringUtils.isNotBlank(testWhileIdle) || Boolean.parseBoolean(testWhileIdle));
        druidDataSource.setTestOnBorrow(StringUtils.isNotBlank(testOnBorrow) && Boolean.parseBoolean(testOnBorrow));
        druidDataSource.setTestOnReturn(StringUtils.isNotBlank(testOnReturn) && Boolean.parseBoolean(testOnReturn));
        druidDataSource.setPoolPreparedStatements(!StringUtils.isNotBlank(poolPreparedStatements) || Boolean.parseBoolean(poolPreparedStatements));
        try {
            druidDataSource.setMaxOpenPreparedStatements(StringUtils.isNotBlank(maxOpenPreparedStatements) ? Integer.parseInt(maxOpenPreparedStatements) : 20);
            druidDataSource.setFilters(StringUtils.isNotBlank(filters) ? filters : "stat, wall");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return druidDataSource;
    }

    @Bean(name = "ORACLE_ERP_TEST")
    @Qualifier("ORACLE_ERP_TEST")
    public DataSource erpTestDataSource() {
        return createDataSource(apsUrl, apsDriveClassName, apsUserName, apsPassword);
    }

    @Bean(name = "ORACLE_ERP_TEST_TRANSACTION_MANAGER")
    public DataSourceTransactionManager erpTestTransactionManager(@Qualifier("ORACLE_ERP_TEST") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "ORACLE_ERP_TEST_BATCH_MAPPER")
    public BaseBatchMapper erpTestNamedParameterJdbcTemplate(@Qualifier("ORACLE_ERP_TEST") DataSource dataSource) {
        return new BaseBatchMapper(new NamedParameterJdbcTemplate(dataSource));
    }
}
