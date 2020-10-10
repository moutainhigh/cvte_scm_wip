package com.cvte.scm.wip.infrastructure.base;

import com.cvte.scm.wip.common.utils.ClassUtils;

import java.lang.reflect.Type;

/**
 * @author zy
 * email zhongyuan@cvte.com
 * date 2020-08-14 11:43
 **/
public class RepositoryTypeReference<T, E> {

    private final Type dataObjectRawType;
    private final Type domainEntityRawType;

    protected RepositoryTypeReference() {
        dataObjectRawType = ClassUtils.getSuperclassTypeParameter(getClass(), 1);
        domainEntityRawType = ClassUtils.getSuperclassTypeParameter(getClass(), 2);
    }

    public final Type getDataObjectRawType() {
        return dataObjectRawType;
    }
    public final Type getDomainEntityRawType() {
        return domainEntityRawType;
    }
}
