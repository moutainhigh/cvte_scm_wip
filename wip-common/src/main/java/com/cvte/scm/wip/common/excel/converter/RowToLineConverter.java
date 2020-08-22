package com.cvte.scm.wip.common.excel.converter;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.core.exception.client.params.SourceNotFoundException;
import com.cvte.scm.wip.common.excel.converter.context.ConvertContext;
import com.cvte.scm.wip.common.excel.converter.definition.BaseField;
import com.cvte.scm.wip.common.excel.converter.definition.UniqueField;

import java.util.*;

/**
 * @author zy
 * @date 2020-06-10 10:32
 **/
public class RowToLineConverter implements Convertible {

    private String[] originHeads;
    private Object[][] originTable;
    private List<BaseField> columnDefines;

    private Set<Object> originUniqueFiledSet;

    private ConvertContext convertContext;

    public RowToLineConverter(String[] originHeads, Object[][] originTable, List<BaseField> columnDefines) {
        this.originHeads = originHeads;
        this.originTable = originTable;
        this.columnDefines = columnDefines;

        this.init();
    }

    @Override
    public List<List<Object>> convertToTableList() {
        List<List<Object>> objects = new ArrayList<>(convertContext.getOriginUniqueFiledSet().size());

        for (Object uniqueField : originUniqueFiledSet) {
            Integer index = 0;
            List<Object> row = new ArrayList<>(convertContext.getHeads().size());
            for (BaseField baseField : columnDefines) {
                index = baseField.fillRows(uniqueField, convertContext, index, row);
            }
            objects.add(row);
        }
        return objects;
    }

    @Override
    public ConvertedTable convertToTable() {
        ConvertedTable convertedTable = new ConvertedTable();
        convertedTable.setData(this.convertToTableList());

        List<List<String>> heads = new ArrayList<>(convertContext.getHeads().size());
        for (String head : convertContext.getHeads()) {
            List<String> list = new ArrayList<>();
            list.add(head);
            heads.add(list);
        }
        convertedTable.setHeads(heads);
        convertedTable.setWriteHandler(new DefaultColumnWidthWriteHandler(this.columnDefines));

        return convertedTable;
    }

    public void destroy() {
        originHeads = null;
        originTable = null;
        columnDefines = null;
        originUniqueFiledSet = null;
        convertContext.destroy();
        convertContext = null;
    }

    private void init() {
        String originUniqueField = this.findUniqueField(this.columnDefines);
        List<String> heads = new ArrayList<>();
        convertContext = new ConvertContext();
        convertContext.setOriginHeads(originHeads)
                .setOriginTable(originTable)
                .setOriginUniqueField(originUniqueField)
                .setOriginUniqueFieldIndex(findFieldIndex(originUniqueField))
                .setOriginUniqueFiledSet(generateOriginUniqueFiledSet(convertContext.getOriginUniqueFieldIndex(), this.originTable))
                .setHeads(heads)
                .setOriginRowMap(buildOriginRowMap(convertContext.getOriginUniqueFieldIndex(), this.originTable));
        columnDefines.forEach(el -> heads.addAll(el.getHeads(convertContext)));
        originUniqueFiledSet = new LinkedHashSet<>(convertContext.getOriginUniqueFiledSet());
    }

    private String findUniqueField(List<BaseField> columnDefines) {
        for (BaseField baseField : columnDefines) {
            if (baseField instanceof UniqueField) {
                return baseField.getOriginFiled();
            }
        }

        throw new ParamsIncorrectException("行主键不存在");
    }

    private Integer findFieldIndex(String field) {
        for (int i = 0; i < originHeads.length; i++) {
            if (field.equals(originHeads[i])) {
                return i;
            }
        }
        throw new SourceNotFoundException(String.format("字段%s不存在", field));
    }

    private Set<Object> generateOriginUniqueFiledSet(Integer originUniqueFieldIndex, Object[][] originTable) {
        Set<Object> set = new LinkedHashSet<>();
        for (Object[] row : originTable) {
            set.add(row[originUniqueFieldIndex]);
        }
        return set;
    }

    private Map<Object, List<List<Object>>> buildOriginRowMap(Integer uniqueFieldIndex, Object[][] originTable) {
        Map<Object, List<List<Object>>> map = new HashMap<>();
        for (Object[] row : originTable) {
            List<List<Object>> uniqueFieldValueMappedRows = map.computeIfAbsent(row[uniqueFieldIndex], k -> new ArrayList<>());
            uniqueFieldValueMappedRows.add(Arrays.asList(row));
        }
        return map;
    }

    /**
     * 默认处理器，用于设置列宽
     **/
    private static class DefaultColumnWidthWriteHandler implements SheetWriteHandler {
        List<BaseField> columnDefines;

        DefaultColumnWidthWriteHandler(List<BaseField> columnDefines) {
            this.columnDefines = columnDefines;
        }

        @Override
        public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
            // nothing to do
        }

        @Override
        public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
            Integer index = 0;
            for (BaseField baseField : columnDefines) {
                index = baseField.setColumnsWidth(index, writeSheetHolder.getSheet());
            }
        }
    }
}
