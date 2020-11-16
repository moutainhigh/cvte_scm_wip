package com.cvte.scm.wip.infrastructure.boot.config.api;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/30 16:12
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@ConfigurationProperties(prefix = "api.csbp")
@Data
@Configuration
public class CsbpApiInfoConfiguration {

    private String baseUrl;

}
