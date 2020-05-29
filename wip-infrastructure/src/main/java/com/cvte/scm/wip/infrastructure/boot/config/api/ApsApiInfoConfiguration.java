package com.cvte.scm.wip.infrastructure.boot.config.api;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/29 10:35
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@ConfigurationProperties(prefix = "api.aps")
@Data
@Configuration
public class ApsApiInfoConfiguration {

    private String baseUrl;

}
