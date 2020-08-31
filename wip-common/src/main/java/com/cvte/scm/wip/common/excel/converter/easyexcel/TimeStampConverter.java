package com.cvte.scm.wip.common.excel.converter.easyexcel;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.csb.toolkit.date.DateStyle;
import com.cvte.csb.toolkit.date.DateUtils;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author zy
 * @date 2020-06-12 08:58
 **/
public class TimeStampConverter implements Converter<Timestamp> {

    @Override
    public Class supportJavaTypeKey() {
        return Timestamp.class;
    }

    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Timestamp convertToJavaData(CellData cellData, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) {
        return ObjectUtils.isNotNull(cellData.getNumberValue()) ? new Timestamp(cellData.getNumberValue().longValue()) : null;
    }

    @Override
    public CellData convertToExcelData(Timestamp timestamp, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) {
        return ObjectUtils.isNotNull(timestamp) ? new CellData<>(DateUtils.DateToString(new Date(timestamp.getTime()), DateStyle.YYYY_MM_DD_HH_MM_SS)) : null;
    }

}
