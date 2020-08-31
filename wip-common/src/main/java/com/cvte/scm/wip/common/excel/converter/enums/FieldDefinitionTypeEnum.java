package com.cvte.scm.wip.common.excel.converter.enums;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.scm.wip.common.enums.CodeEnum;
import com.cvte.scm.wip.common.excel.converter.definition.BaseField;
import com.cvte.scm.wip.common.excel.converter.definition.CommonField;
import com.cvte.scm.wip.common.excel.converter.definition.RowToColumnField;
import com.cvte.scm.wip.common.excel.converter.definition.UniqueField;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zy
 * @date 2020-06-11 17:20
 **/
@Getter
@AllArgsConstructor
public enum FieldDefinitionTypeEnum implements CodeEnum {

    COMMON("COMMON", CommonField.class),
    UNIQUE("UNIQUE", UniqueField.class),
    ROW_TO_COLUMN("ROW_TO_COLUMN", RowToColumnField.class);

    private String code;

    Class<? extends BaseField> cType;

    public BaseField getBaseFieldInstance() {
        try {
            return cType.newInstance();
        } catch (Exception e) {
            throw new ParamsIncorrectException("字段定义实例初始化失败：code=" + code);
        }
    }

}
