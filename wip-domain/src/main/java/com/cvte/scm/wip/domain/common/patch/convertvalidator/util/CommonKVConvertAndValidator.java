package com.cvte.scm.wip.domain.common.patch.convertvalidator.util;


import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.scm.wip.domain.common.patch.convertvalidator.ValidatorAdapter;
import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 注意：需要com.cvte.aps.apsert.common.util.SpringContextUtils静态类完成加载和扫描
 * @version 1.0
 * @descriptions: 效验并转换工具
 * @author: ykccchen
 * @date: 2020/7/10 4:06 下午
 */
@Slf4j
public class CommonKVConvertAndValidator<K,V> {

    private Map<K, V> commonKVMap;

    private String keyFieldName;

    private String targetFieldName;

    private boolean isConvert;

    private List<ValidatorAdapter> validatorList = new ArrayList<>();




    /**
     * 数据源为Mapper
     * 使用者可以通过这个方法调用Mapper的自带搜索方式去数据库去查询所需要的标准值用来核对
     * 该方法为用户自定义设置而外查询条件的方法
     * 带转换值功能
     * @param KeySet           需要查询的字段集合
     * @param keyFieldName     需要查询的字段名字
     * @param targetFieldName  需要转换的属性名字
     * @param isConvert        是否需要转换
     * @param beanMapper       根据Mapper去查询数据，传递对应的Mapper源
     * @param example          查询条件，keyFieldName为默认条件
     */
    public CommonKVConvertAndValidator(@NonNull Set<Serializable> KeySet,
                                       @NonNull String keyFieldName,
                                        String targetFieldName,
                                        boolean isConvert,
                                        Class<? extends WipBaseRepository> beanMapper,
                                        Example example) {
        this.keyFieldName = keyFieldName;
        this.targetFieldName = targetFieldName;
        this.isConvert = isConvert;
        initMapperSource(KeySet, beanMapper,example);
    }


    private void initMapperSource(Set<Serializable> keySet, Class<? extends WipBaseRepository> clazz, Example example){
        WipBaseRepository bean = SpringContextUtils.getBean(clazz);
        commonKVMap = new HashMap<>();
        String[] strs = new String[keySet.size()];
        int i = 0;
        for (Serializable serializable : keySet) {
            strs[i++] = String.valueOf(serializable);
        }
        List<V> list = bean.selectListByIds(strs);
        try {
            for (V v : list) {
                Field declaredField = v.getClass().getDeclaredField(keyFieldName);
                declaredField.setAccessible(true);
                commonKVMap.put((K)declaredField.get(v), v);
            }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error(String.format("字段%s不存在，请检查代码的编写",keyFieldName));
                log.error(e.toString());
        }
    }

    /**
     * 开始效验并转换
     * @param target
     * @return
     */
    public boolean validatorAndConvert(V target){
        Serializable o = null;
        try {
            Field declaredField = target.getClass().getDeclaredField(keyFieldName);
            declaredField.setAccessible(true);
            o = (Serializable) declaredField.get(target);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error(e.toString());
        }
        if (ObjectUtils.isNull(o)){
            return false;
        }
        V value = getValue(o);
        if (value != null){
            // 循环使用效验器，出错回滚
            for (ValidatorAdapter validatorAdapter : validatorList) {
                boolean invoke = validatorAdapter.invoke(value,target);
                if (invoke == false){
                    return false;
                }
            }
            if (isConvert){
                try {
                    Field keyField = null;
                    // 获取目标待设置的属性类
                    keyField = target.getClass().getDeclaredField(targetFieldName);
                    keyField.setAccessible(true);
                    if (value instanceof Serializable){
                        keyField.set(target,value);
                    }else {
                        // 获取匹配类待取值的属性类
                        Field declaredField = value.getClass().getDeclaredField(targetFieldName);
                        declaredField.setAccessible(true);
                        // 值转换
                        keyField.set(target,declaredField.get(value));
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    log.error(String.format("字段%s不存在，请检查代码的编写",keyFieldName));
                    log.error(e.toString());
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public V getValue(Serializable key){
        V value = commonKVMap.get(key);
        return value;
    }

    /**
     * 添加效验器，需要实现ValidatorAdapter接口
     * 在执行效验前操作
     * @param validator
     */
    public void addValidatorList(ValidatorAdapter validator){
        validatorList.add(validator);
    }
    /**
     * 添加效验器，需要实现ValidatorAdapter接口
     * 在执行效验前操作
     * @param validatorList
     */
    public void addValidatorListAll(List<ValidatorAdapter> validatorList){
        validatorList.addAll(validatorList);
    }



}
