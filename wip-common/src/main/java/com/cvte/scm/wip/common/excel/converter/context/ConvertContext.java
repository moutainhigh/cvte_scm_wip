package com.cvte.scm.wip.common.excel.converter.context;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zy
 * @date 2020-06-08 14:15
 **/
@Data
@Accessors(chain = true)
public class ConvertContext {

    private String[] originHeads;
    private Object[][] originTable;

    private String originUniqueField;
    private Integer originUniqueFieldIndex;
    private Set<Object> originUniqueFiledSet;

    private List<String> heads;

    private Map<Object, List<List<Object>>> originRowMap;

    public void destroy() {
        this.originHeads = null;
        this.originTable = null;
        this.originUniqueField = null;
        this.originUniqueFieldIndex = null;
        this.originUniqueFiledSet = null;
        this.heads = null;
        this.originRowMap = null;
    }
}
