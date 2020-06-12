package com.cvte.scm.wip.domain.core.ckd.annotation;


import com.cvte.scm.wip.domain.core.ckd.enums.McTaskStatusEnum;

import java.lang.annotation.*;

/**
 * @author zy
 * @date 2020-04-30 19:01
 **/
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.TYPE)
public @interface McTaskStatusAnnotation {

    McTaskStatusEnum curStatus();

    McTaskStatusEnum[] updateToStatusArr();
}
