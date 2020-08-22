package com.cvte.scm.wip.common.excel.converter.dto;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.scm.wip.common.excel.converter.definition.BaseField;
import com.cvte.scm.wip.common.excel.converter.enums.FieldDefinitionTypeEnum;
import com.cvte.scm.wip.common.utils.EnumUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

/**
 * @author zy
 * @date 2020-06-11 17:17
 **/
@Data
@Accessors(chain = true)
@EqualsAndHashCode
public class FieldDefinitionDTO {

    private Integer width;

    private Integer high;

    private String type;

    private String originFiled;

    private String field;

    private Object valueField;


    public BaseField convertToBaseField() {
        FieldDefinitionTypeEnum definitionTypeEnum = EnumUtils.getByCode(this.getType(), FieldDefinitionTypeEnum.class);
        if (ObjectUtils.isNull(definitionTypeEnum)) {
            throw new ParamsIncorrectException("字段类型不存在: " + this.getType());
        }
        BaseField baseField = definitionTypeEnum.getBaseFieldInstance();
        BeanUtils.copyProperties(this, baseField);
        return baseField;
    }


}
