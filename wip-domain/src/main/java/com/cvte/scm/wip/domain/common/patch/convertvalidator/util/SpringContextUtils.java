package com.cvte.scm.wip.domain.common.patch.convertvalidator.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @version 1.0
 * @descriptions:
 * @author: ykccchen
 * @date: 2020/7/27 10:57 上午
 */
@Slf4j
@Component
public class SpringContextUtils implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtils.context = applicationContext;
    }

    public static <T>T getBean (Class<T> c){
        return context.getBean(c);
    }

}
