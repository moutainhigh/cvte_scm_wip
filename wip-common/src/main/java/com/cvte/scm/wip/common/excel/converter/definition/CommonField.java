package com.cvte.scm.wip.common.excel.converter.definition;

import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.scm.wip.common.excel.converter.context.ConvertContext;
import com.cvte.scm.wip.common.excel.converter.enums.ValueMergeTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author zy
 * @date 2020-06-08 13:02
 **/
@Data
@Slf4j
@EqualsAndHashCode
@NoArgsConstructor
public class CommonField extends BaseField {

    private String field;

    public CommonField(String originFiled, String convertToFiled) {
        this.field = convertToFiled;
        super.setOriginFiled(originFiled);
    }

    @Override
    public List<String> getHeads(ConvertContext convertContext) {
        return Collections.singletonList(this.field);
    }

    @Override
    public Integer fillRows(Object uniqueField, ConvertContext convertContext, Integer index, List<Object> row) {
        if (ObjectUtils.isNull(this.getOriginFieldIndex())) {
            this.setOriginFieldIndex(getFieldIndex(this.getOriginFiled(), convertContext.getOriginHeads()));
        }
        row.add(this.mergeValueByType(this.getValueMergeType(), convertContext.getOriginRowMap().get(uniqueField)));
        return index + 1;
    }

    @Override
    public Integer setColumnsWidth(Integer index, Sheet sheet) {
        if (ObjectUtils.isNotNull(this.getWidth())) {
            sheet.setColumnWidth(index, this.getWidth());
        }
        return index + 1;
    }

    /**
     * 根据值聚合类型处理字段值
     * @since 2020/8/25 7:35 下午
     * @author xueyuting
     * @param valueMergeType {in} 值聚合类型
     * @param originTable {in} 主键对应的所有行
     */
    private Object mergeValueByType(ValueMergeTypeEnum valueMergeType, List<List<Object>> originTable) {
        if (ValueMergeTypeEnum.SUM.equals(valueMergeType)) {
            try {
                return originTable.stream().map(keyToRows -> new BigDecimal(keyToRows.get(this.getOriginFieldIndex()).toString())).reduce(BigDecimal.ZERO, BigDecimal::add);
            } catch (RuntimeException re) {
                log.error(re.getMessage());
            }
        }
        return originTable.get(0).get(this.getOriginFieldIndex());
    }

}
