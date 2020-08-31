package com.cvte.scm.wip.common.excel.converter.easyexcel;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.math.BigDecimal;
import java.util.Objects;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/8/21 17:25
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public class NumberConverter implements Converter<BigDecimal> {

    @Override
    public Class supportJavaTypeKey() {
        return BigDecimal.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.NUMBER;
    }

    @Override
    public BigDecimal convertToJavaData(CellData cellData, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) {
        return Objects.nonNull(cellData.getNumberValue()) ? new BigDecimal(cellData.getNumberValue().toString()) : null;
    }

    @Override
    public CellData convertToExcelData(BigDecimal bigDecimal, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) {
        return Objects.nonNull(bigDecimal) ? new CellData<>(bigDecimal) : null;
    }

}
