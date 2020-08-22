package com.cvte.scm.wip.common.excel.converter.definition;

import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.scm.wip.common.excel.converter.context.ConvertContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author zy
 * @date 2020-06-08 13:02
 **/
@Data
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
        row.add(convertContext.getOriginRowMap().get(uniqueField).get(0).get(this.getOriginFieldIndex()));
        return index + 1;
    }

    @Override
    public Integer setColumnsWidth(Integer index, Sheet sheet) {
        if (ObjectUtils.isNotNull(this.getWidth())) {
            sheet.setColumnWidth(index, this.getWidth());
        }
        return index + 1;
    }


}
