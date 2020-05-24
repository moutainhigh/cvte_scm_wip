package com.cvte.scm.wip.infrastructure.boot.config.api;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/4/7 09:53
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@ConfigurationProperties(prefix = "api.ocs")
@Data
@Configuration
public class OcsApiInfoConfiguration {

    private String baseUrl;

}
