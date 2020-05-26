package com.cvte.scm.wip.domain.core.ckd.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zy
 * @date 2020-05-15 11:36
 **/
@Component
public class WipMcTaskConstant {

    /** 默认物料仓库 **/
    public static String DEFAULT_STOREHOUSE_METRIC_CODE;

    /** 默认在制仓库 **/
    public static String DEFAULT_STOREHOUSE_MAKE_CODE;

    /** 默认CKD仓库 **/
    public static String DEFAULT_STOREHOUSE_CKD_CODE;


    /** 库存组织前缀 **/
    public static String PREFIX_INV = "INV_";

    /** 工厂编码分割符 **/
    public static String FACTORY_CODE_SEPARATOR = "-";

    /** 组织编码分割符 **/
    public static String ORG_CODE_SEPARATOR = "-";

    @Value("${ebd.storehouse.metric.code:01}")
    public void setDefaultStorehouseMetricCode(String defaultStorehouseMetricCode) {
        this.DEFAULT_STOREHOUSE_METRIC_CODE = defaultStorehouseMetricCode;
    }

    @Value("${ebs.storehouse.make.code:257}")
    public void setDefaultStorehouseMakeCode(String defaultStorehouseMakeCode) {
        this.DEFAULT_STOREHOUSE_MAKE_CODE = defaultStorehouseMakeCode;
    }


    @Value("${ebs.storehouse.ckd.code:54}")
    public void setDefaultStorehouseCkdCode(String defaultStorehouseCkdCode) {
        this.DEFAULT_STOREHOUSE_CKD_CODE = defaultStorehouseCkdCode;
    }
}
