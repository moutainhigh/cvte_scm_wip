package com.cvte.scm.wip.domain.core.requirement.dto;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
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

    @NotBlank(message = "物料编码不能为空")
    @ExcelProperty(value = "物料编码")
    private String itemCode;

    @NotBlank(message = "产品型号不能为空")
    @ExcelProperty(value = "产品型号")
    private String productModel;

    @ExcelProperty(value = "文件版本")
    private String versionNo;

    @ExcelProperty(value = "位置A")
    private String locationA;

    @ExcelProperty(value = "位置B")
    private String locationB;

    @NotNull(message = "数量不能为空")
    @ExcelProperty(value = "数量")
    private Integer unitQty;

    @ExcelProperty(value = "位置A部件数量")
    private Integer locationAQty;

    @ExcelProperty(value = "工艺属性")
    private String techniqueAttr;

}
