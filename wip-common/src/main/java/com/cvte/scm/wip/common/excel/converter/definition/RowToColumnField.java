package com.cvte.scm.wip.common.excel.converter.definition;

import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.scm.wip.common.excel.converter.IMerge;
import com.cvte.scm.wip.common.excel.converter.context.ConvertContext;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.*;

/**
 * @author zy
 * @date 2020-06-08 12:55
 **/
@ApiModel("行转列字段定义")
@NoArgsConstructor
@EqualsAndHashCode
public class RowToColumnField extends BaseField {

    @ApiModelProperty("值字段名")
    private Object valueField;
    @ApiModelProperty(value = "合并算法", hidden = true)
    private IMerge iMerge = new DefaultMerge();

    @ApiModelProperty(value = "值字段下标", hidden = true)
    private Integer valueFieldIndex;
    @ApiModelProperty(value = "该字段行转列后的头列表", hidden = true)
    private List<String> convertedHeads;

    public Object getValueField() {
        return valueField;
    }

    public void setValueField(Object valueField) {
        this.valueField = valueField;
    }

    public IMerge getiMerge() {
        return iMerge;
    }

    public void setiMerge(IMerge iMerge) {
        this.iMerge = iMerge;
    }

    public RowToColumnField(String headField, String valueField) {
        this(headField, valueField, new DefaultMerge());
    }

    public RowToColumnField(String originFile, String valueField, IMerge iMerge) {
        this.valueField = valueField;
        this.iMerge = iMerge;
        super.setOriginFiled(originFile);
    }

    @Override
    public List<String> getHeads(ConvertContext convertContext) {
        if (CollectionUtils.isEmpty(this.convertedHeads)) {
            initConvertedHeads(convertContext);
        }
        return this.convertedHeads;
    }


    @Override
    public Integer fillRows(Object uniqueField, ConvertContext convertContext, Integer index, List<Object> row) {

        Map<Object, Object> map = new HashMap<>();
        List<List<Object>> originTable = convertContext.getOriginRowMap().get(uniqueField);
        Integer headIndex = getHeadColumnIndex(convertContext.getOriginHeads());
        Integer valueIndex = getValueColumnIndex(convertContext.getOriginHeads());
        for (List<Object> originRow : originTable) {
            Object key = originRow.get(headIndex);
            Object value = ObjectUtils.isNotNull(valueIndex) ? originRow.get(valueIndex) : null;
            putRowValue(map, key, value);
        }

        for (String convertedHead : this.convertedHeads) {
            row.add(map.get(convertedHead));
        }

        return index + this.convertedHeads.size();
    }

    @Override
    public Integer setColumnsWidth(Integer index, Sheet sheet) {
        if (ObjectUtils.isNull(this.getWidth())) {
            return index + this.convertedHeads.size();
        }
        for (int i = 0; i < this.convertedHeads.size(); i ++) {
            sheet.setColumnWidth(index + i, this.getWidth());
        }
        return index + this.convertedHeads.size();
    }


    private Integer getValueColumnIndex(String[] originHeads) {
        if (ObjectUtils.isNull(this.valueFieldIndex)) {
            this.valueFieldIndex = getFieldIndex(this.valueField, originHeads, false);
        }
        return this.valueFieldIndex;
    }

    private Integer getHeadColumnIndex(String[] originHeads) {
        if (ObjectUtils.isNull(this.getOriginFieldIndex())) {
            this.setOriginFieldIndex(getFieldIndex(this.getOriginFiled(), originHeads));
        }
        return this.getOriginFieldIndex();
    }

    private void initConvertedHeads(ConvertContext convertContext) {
        Set<String> title = new LinkedHashSet<>();
        Integer titleFieldIndex = getHeadColumnIndex(convertContext.getOriginHeads());
        Object[][] table = convertContext.getOriginTable();
        for (Object[] row : table) {
            if (ObjectUtils.isNotNull(row[titleFieldIndex])) {
                title.add(row[titleFieldIndex].toString());
            }
        }
        this.convertedHeads = new ArrayList<>(title);
    }

    private void putRowValue(Map<Object, Object> map, Object key, Object value) {
        if (ObjectUtils.isNull(key)) {
            return;
        }
        if (!map.containsKey(key)) {
            map.put(key.toString(), value);
            return;
        }
        // 如果该key值已存在，使用设置的合并算法进行合并
        map.put(key.toString(), this.iMerge.merge(value, map.get(key)));
    }

    public static class DefaultMerge implements IMerge {

        @Override
        public Object merge(Object obj1, Object obj2) {
            if (ObjectUtils.isNull(obj1) || ObjectUtils.isNull(obj2)) {
                return ObjectUtils.isNotNull(obj1) ? obj1 : obj2;
            }
            if (obj1 instanceof Integer) {
                return (Integer) obj1 + (Integer) obj2;
            } else if (obj1 instanceof Long) {
                return (Long) obj1 + (Long) obj2;
            } else {
                return obj1;
            }
        }

    }
}
