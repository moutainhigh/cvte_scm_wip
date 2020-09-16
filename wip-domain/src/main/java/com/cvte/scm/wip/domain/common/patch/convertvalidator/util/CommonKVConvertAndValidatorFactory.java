package com.cvte.scm.wip.domain.common.patch.convertvalidator.util;


import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;
import lombok.NonNull;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Set;

/**
 * @descriptions: CommonKVConvertAndValidator的工厂类
 * @author: ykccchen
 * @date: 2020/7/17 6:00 下午
 * @version 1.0
 */
public class CommonKVConvertAndValidatorFactory {

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
    public static <K,V>CommonKVConvertAndValidator<K,V> buildByMapper(@NonNull Set<Serializable> KeySet,
                                                                      @NonNull String keyFieldName,
                                                                      String targetFieldName,
                                                                      boolean isConvert,
                                                                      Class<? extends WipBaseRepository> beanMapper,
                                                                      Example example){
        return new CommonKVConvertAndValidator<K,V>(KeySet,keyFieldName,targetFieldName,isConvert,beanMapper,example);
    }

    /**
     * 数据源为Mapper
     * 使用者可以通过这个方法调用Mapper的自带搜索方式去数据库去查询所需要的标准值用来核对
     * 带转换值功能
     * @param KeySet           需要查询的字段集合
     * @param keyFieldName     需要查询的字段名字
     * @param targetFieldName  需要转换的属性名字
     * @param isConvert        是否需要转换
     * @param beanMapper       根据Mapper去查询数据，传递对应的Mapper源
     */
    public static <K,V>CommonKVConvertAndValidator<K,V> buildByMapper(@NonNull Set<Serializable> KeySet,
                                                                      @NonNull String keyFieldName,
                                                                      String targetFieldName,
                                                                      boolean isConvert,
                                                                      Class<? extends WipBaseRepository> beanMapper){

        return new CommonKVConvertAndValidator<K,V>(KeySet,keyFieldName,targetFieldName,isConvert,beanMapper,null);
    }

    /**
     * 数据源为Mapper
     * 使用者可以通过这个方法调用Mapper的自带搜索方式去数据库去查询所需要的标准值来进行核对对应的属性字段
     * @param KeySet           需要查询的字段集合
     * @param keyFieldName     需要查询的字段名字
     * @param beanMapper       根据Mapper去查询数据，传递对应的Mapper源
     */
    public static <K,V>CommonKVConvertAndValidator<K,V> buildByMapper(@NonNull Set<Serializable> KeySet,
                                                                      @NonNull String keyFieldName,
                                                                      Class<? extends WipBaseRepository> beanMapper){

        return new CommonKVConvertAndValidator<K,V>(KeySet,keyFieldName,null,false,beanMapper,null);
    }
    /**
     * 数据源为Mapper
     * 使用者可以通过这个方法调用Mapper的自带搜索方式去数据库去查询所需要的标准值来进行核对对应的属性字段
     * @param KeySet           需要查询的字段集合
     * @param keyFieldName     需要查询的字段名字
     * @param beanMapper       根据Mapper去查询数据，传递对应的Mapper源
     * @param example          查询条件，keyFieldName为默认条件
     */
    public static <K,V>CommonKVConvertAndValidator<K,V> buildByMapper(@NonNull Set<Serializable> KeySet,
                                                                      @NonNull String keyFieldName,
                                                                      Class<? extends WipBaseRepository> beanMapper,
                                                                      Example example){
        return new CommonKVConvertAndValidator<K,V>(KeySet,keyFieldName,null,false,beanMapper,example);
    }
}
