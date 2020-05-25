package com.cvte.scm.wip.domain.core.subrule.valueobject.enums;

import com.cvte.scm.wip.common.enums.Codeable;
import lombok.Getter;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/2/26 14:27
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Getter
public enum SubRuleWfNodeEnum implements Codeable {
    SELLER("seller", "销售或销管"),
    ELECTRONIC_ENGINEER("electronicEngineer", "电子工程师"),
    WIRE_ENGINEER("wireEngineer", "线材工程师"),
    OPTICAL_ENGINEER("opticalEngineer", "光学工程师"),
    STRUCTURAL_ENGINEER("structuralEngineer", "结构工程师"),
    PACKAGING_ENGINEER("packagingEngineer", "包装工程师"),
    SOFTWARE_ENGINEER("softwareEngineer", "软件工程师"),
    BOM_ENGINEER("bomEngineer", "BOM工程师"),
    QUALITY("quality", "品质"),
    MATERIAL_CONTROL("materialControl", "物控"),
    PE("PE", "PE"),
    NPI("NPI", "NPI"),
    CIRCULANT("circulant", "传阅人"),
    ;
    private String code;
    private String desc;

    SubRuleWfNodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
