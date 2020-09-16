package com.cvte.scm.wip.domain.common.patch.convertvalidator;

/**
 * 只做效验功能，请勿做其他的操作
 * @version 1.0
 * @descriptions: 通用比较转换器的比较器适配器接口类
 * @author: ykccchen
 * @date: 2020/7/17 4:06 下午
 */
public interface ValidatorAdapter {

    /**
     * 建议在知道该对象的时候强制转换
     * @param standardValue  标准数据对象
     * @param target   待检查对象
     * @return
     */
    boolean invoke(Object standardValue,Object target);
}
