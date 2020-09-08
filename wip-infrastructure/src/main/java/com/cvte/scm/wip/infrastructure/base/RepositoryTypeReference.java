package com.cvte.scm.wip.infrastructure.base;

import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeReference;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author zy
 * email zhongyuan@cvte.com
 * date 2020-08-14 11:43
 **/
public class RepositoryTypeReference<T, E> {

    private final Type fromRawType;
    private final Type toRawType;

    protected RepositoryTypeReference() {
        fromRawType = getSuperclassTypeParameter(getClass(), 0);
        toRawType = getSuperclassTypeParameter(getClass(), 1);
    }

    Type getSuperclassTypeParameter(Class<?> clazz, int index) {
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass instanceof Class) {
            // try to climb up the hierarchy until meet something useful
            if (TypeReference.class != genericSuperclass) {
                return getSuperclassTypeParameter(clazz.getSuperclass(), index);
            }

            throw new TypeException("'" + getClass() + "' extends TypeReference but misses the type parameter. "
                    + "Remove the extension or add a type parameter to it.");
        }

        Type rawType = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[index];
        if (rawType instanceof ParameterizedType) {
            rawType = ((ParameterizedType) rawType).getRawType();
        }

        return rawType;
    }

    public final Type getFromRawType() {
        return fromRawType;
    }
    public final Type getToRawType() {
        return toRawType;
    }
}
