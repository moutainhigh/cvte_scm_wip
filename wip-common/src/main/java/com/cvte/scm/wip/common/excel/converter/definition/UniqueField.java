package com.cvte.scm.wip.common.excel.converter.definition;

import com.cvte.scm.wip.common.excel.converter.context.ConvertContext;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zy
 * @date 2020-06-08 12:53
 **/
@NoArgsConstructor
@EqualsAndHashCode
public class UniqueField extends CommonField {

    private boolean hidden;

    public UniqueField(String originFiled, String convertToFiled) {
        this(originFiled, convertToFiled, false);
    }

    public UniqueField(String originFiled, String convertToFiled, boolean hidden) {
        super(originFiled, convertToFiled);
        this.hidden = hidden;
    }


    @Override
    public List<String> getHeads(ConvertContext convertContext) {
        if (hidden) {
            return new ArrayList<>();
        }
        return super.getHeads(convertContext);
    }

    @Override
    public Integer fillRows(Object uniqueField, ConvertContext convertContext, Integer index, List<Object> row) {
        if (hidden) {
            return index;
        }

        return super.fillRows(uniqueField, convertContext, index, row);
    }

    @Override
    public Integer setColumnsWidth(Integer index, Sheet sheet) {
        if (hidden) {
            return index;
        }
        return super.setColumnsWidth(index, sheet);
    }


    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}

