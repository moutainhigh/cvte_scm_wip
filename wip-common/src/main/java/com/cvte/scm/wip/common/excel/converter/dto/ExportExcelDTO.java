package com.cvte.scm.wip.common.excel.converter.dto;

import com.alibaba.excel.converters.Converter;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.scm.wip.common.excel.converter.definition.BaseField;
import com.cvte.scm.wip.common.excel.converter.definition.CommonField;
import com.cvte.scm.wip.common.excel.converter.definition.UniqueField;
import com.cvte.scm.wip.common.excel.converter.enums.FieldDefinitionTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zy
 * @date 2020-06-10 10:57
 **/
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class ExportExcelDTO {

    @ApiModelProperty("导出文件名")
    private String fileName;

    @ApiModelProperty("导出excel头")
    private List<String> exportHeads;

    @ApiModelProperty("导出excel头标题")
    private Map<String, String> headTextMap;

    @ApiModelProperty("原始头")
    private List<String> originHeads;
    @ApiModelProperty("原始数据")
    private List<Map<String, Object>> originData;


    private List<FieldDefinitionDTO> fieldDefinitions;

    @ApiModelProperty("easyExcel导出转换器")
    private List<Converter<?>> converters;

    public List<BaseField> generateBaseFields() {
        Map<String, FieldDefinitionDTO> fieldDefinitionMap = CollectionUtils.isEmpty(this.fieldDefinitions) ? new HashMap<>()
                : this.fieldDefinitions.stream().collect(Collectors.toMap(FieldDefinitionDTO::getOriginFiled, Function.identity()));

        List<BaseField> baseFields = new ArrayList<>();
        for (String exportHead : this.exportHeads) {
            if (fieldDefinitionMap.containsKey(exportHead)) {
                baseFields.add(fieldDefinitionMap.get(exportHead).convertToBaseField());
                fieldDefinitionMap.remove(exportHead);
            } else {
                baseFields.add(new CommonField(exportHead, exportHead));
            }
        }

        // 剩余的项不展示，目前只支持唯一键隐藏
        for (FieldDefinitionDTO fieldDefinitionDTO : fieldDefinitionMap.values()) {
            if (FieldDefinitionTypeEnum.UNIQUE.getCode().equals(fieldDefinitionDTO.getType())) {
                UniqueField uniqueField = (UniqueField) fieldDefinitionDTO.convertToBaseField();
                uniqueField.setHidden(true);
                baseFields.add(uniqueField);
                break;
            }
        }
        return baseFields;
    }


}
