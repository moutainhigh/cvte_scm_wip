package com.cvte.scm.wip.common.excel.converter;

import java.util.List;

/**
 * @author zy
 * @date 2020-06-10 10:38
 **/
public interface Convertible {

    /**
     * 将数据转换成二维数组(不含头)
     **/
    List<List<Object>> convertToTableList();

    ConvertedTable convertToTable();
}
