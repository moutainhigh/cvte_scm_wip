package com.cvte.scm.wip.domain.core.ckd.processor;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.scm.wip.domain.core.ckd.annotation.McTaskStatusAnnotation;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskStatusEnum;
import com.cvte.scm.wip.domain.core.ckd.handler.McTaskStatusUpdateHandlerFactory;
import com.cvte.scm.wip.domain.core.ckd.handler.McTaskStatusUpdateIHandler;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author zy
 * @date 2020-05-06 09:22
 **/
public class McTaskStatusProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanNames) {
        McTaskStatusAnnotation mcTaskStatusAnnotation = AnnotationUtils.findAnnotation(bean.getClass(), McTaskStatusAnnotation.class);
        if (ObjectUtils.isNotNull(mcTaskStatusAnnotation)) {
            if (!(bean instanceof McTaskStatusUpdateIHandler)) {
                throw new ParamsIncorrectException("McTaskStatusAnnotation 标注的体征处理器必须实现 McTaskStatusUpdateIHandler");
            }

            McTaskStatusEnum[] mcTaskStatusEnums = mcTaskStatusAnnotation.updateToStatusArr();
            for (int i = 0; i < mcTaskStatusEnums.length; i++) {
                McTaskStatusUpdateHandlerFactory.put(mcTaskStatusAnnotation.curStatus(), mcTaskStatusEnums[i], (McTaskStatusUpdateIHandler) bean);
            }
        }

        return bean;
    }
}
