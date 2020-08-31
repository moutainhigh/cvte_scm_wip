package com.cvte.scm.wip.common.excel.converter;

import com.alibaba.excel.write.handler.WriteHandler;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author zy
 * @date 2020-06-09 18:16
 **/
@Data
@Accessors(chain = true)
public class ConvertedTable {

    private List<List<String>> heads;

    private List<List<Object>> data;

    private WriteHandler writeHandler;

}
