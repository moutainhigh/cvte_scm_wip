package com.cvte.scm.wip.infrastructure.boot.config;

import com.cvte.csb.toolkit.StringUtils;
import com.github.pagehelper.StringUtil;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Optional;

/**
 * @Author: liuxiaojing
 * @Date: 2020/09/24 17:01
 * @Version 1.0.0
 * @Description: Swagger配置类
 **/
@Configuration
public class SwaggerConfig {

    @Value("${csb.swagger.basePackage}")
    private String basePackage;//扫描包路径
    @Value("${spring.application.name}")
    private String serviceName;
    @Value("${csb.swagger.description}")
    private String description;
    @Value("${csb.swagger.version}")
    private String version;
    @Value("${csb.swagger.enable:true}")
    private boolean enable;
    @Value("${scm.swagger.api.uri.regex:^/api/.*|^/openapi/.*}")
    private String apiUriRegex;
    @Value("${scm.swagger.api.class.path.regex:}")
    private String apiClassPathRegex;//正则表达式过滤类路径
    @Value("${scm.swagger.api.class.path.exclude.regex:}")
    private String apiClassPathExcludeRegex;//正则表达式过滤类名

    @Bean
    @Qualifier("swaggerApiDocket")
    public Docket createSwaggerApiDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("api")
                .enable(enable).apiInfo(apiInfo())
                .select()
                .apis(basePackage(this.basePackage,this.apiClassPathRegex,this.apiClassPathExcludeRegex))
                .paths(PathSelectors.regex(apiUriRegex)).build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(this.serviceName + "对外接口")
                .description(this.description).version(this.version).build();
    }

    public static Predicate<RequestHandler> basePackage(final String basePackage, final String apiClassPathRegex, final String apiClassPathExcludeRegex) {
        return input -> declaringClass(input).map(handlerPackage(basePackage,apiClassPathRegex,apiClassPathExcludeRegex)::apply).orElse(true);
    }

    /**
     * @param input RequestHandler
     * @return Optional
     */
    private static Optional<Class<?>> declaringClass(RequestHandler input) {
        return Optional.ofNullable(input.declaringClass());
    }

    /**
     * 处理包路径配置规则,支持多路径扫描匹配以逗号隔开
     * @param basePackage 扫描包路径
     * @param apiClassPathRegex 正则表达式过滤类路径
     * @param apiClassPathExcludeRegex 正则表达式过滤类名
     * @return
     */
    private static Function<Class<?>, Boolean> handlerPackage(final String basePackage, final String apiClassPathRegex, final String apiClassPathExcludeRegex) {
        return input -> {
            boolean isMatch = false;
            String packageName = input.getPackage().getName();
            for (String strPackage : basePackage.split(",")) {
                isMatch = packageName.startsWith(strPackage);
                if (isMatch){
                    break;
                }
            }
            if (isMatch && StringUtil.isNotEmpty(apiClassPathExcludeRegex)) {
                isMatch = !input.getName().matches(apiClassPathExcludeRegex);
            }
            if (isMatch && StringUtils.isNotBlank(apiClassPathRegex)) {
                isMatch = input.getName().matches(apiClassPathRegex);
            }
            return isMatch;
        };
    }
}

