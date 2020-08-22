package com.cvte.scm.wip.common.excel.converter.definition;

import com.cvte.csb.core.exception.client.params.SourceNotFoundException;
import com.cvte.scm.wip.common.excel.converter.context.ConvertContext;
import lombok.Data;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

/**
 * @author zy
 * @date 2020-06-08 13:02
 **/
@Data
public abstract class BaseField {

    private Integer width;

    private String originFiled;

    private Integer originFieldIndex;

    public abstract List<String> getHeads(ConvertContext convertContext);

    public abstract Integer fillRows(Object uniqueField, ConvertContext convertContext, Integer index, List<Object> row);

    public abstract Integer setColumnsWidth(Integer index, Sheet sheet);

    protected Integer getFieldIndex(Object field, String[] heads) {
        return getFieldIndex(field, heads, true);
    }

    protected Integer getFieldIndex(Object field, String[] originHeads, boolean isStrict) {
        for (int i = 0; i < originHeads.length; i ++) {
            if (field.equals(originHeads[i])) {
                return i;
            }
        }
        if (isStrict) {
            throw new SourceNotFoundException(String.format("字段%s不存在", field));
        }
        return null;
    }



}
