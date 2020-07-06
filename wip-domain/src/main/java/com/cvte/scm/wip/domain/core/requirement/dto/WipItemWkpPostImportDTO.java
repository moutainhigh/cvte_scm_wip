package com.cvte.scm.wip.domain.core.requirement.dto;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cvte.scm.wip.common.constants.CommonConstant;
import jodd.vtor.constraint.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.sql.rowset.BaseRowSet;

/**
 * @author zy
 * @date 2020-05-22 15:06
 **/
@Getter
@Setter
@ExcelIgnoreUnannotated
public class WipItemWkpPostImportDTO extends BaseRowSet {

    /**
     * 行数，用于导入操作。
     */
    private int rowNum;

    /**
     * 辅助字段, 组织id
     **/
    private String organizationId;

    @NotBlank(message = "物料编码不能为空")
    @ExcelProperty(value = "物料编码")
    private String itemCode;

    @NotBlank(message = "组织名称不能为空")
    @ExcelProperty(value = "组织名称")
    private String orgName;

    @NotBlank(message = "产品型号不能为空")
    @ExcelProperty(value = "产品型号")
    private String productModel;

    @ExcelProperty(value = "位置A")
    private String locationA;

    @ExcelProperty(value = "位置B")
    private String locationB;

    @NotNull(message = "数量不能为空")
    @ExcelProperty(value = "数量")
    private Integer unitQty;

    @ExcelProperty(value = "位置A部件数量")
    private Integer locationAQty;

    @NotBlank(message = "工艺属性不能为空")
    @ExcelProperty(value = "工艺属性")
    private String techniqueAttr;


    /**
     * 获取唯一索引键
     *
     * @return java.lang.String
     **/
    public String generateUniqueKey() {
        return this.getOrganizationId()
                + CommonConstant.COMMON_SEPARATOR
                + this.getProductModel()
                + CommonConstant.COMMON_SEPARATOR
                + this.getItemCode()
                + CommonConstant.COMMON_SEPARATOR
                + this.getTechniqueAttr();
    }

}
