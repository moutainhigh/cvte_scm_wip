package com.cvte.scm.wip.common.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.scm.wip.common.excel.converter.ConvertedTable;
import com.cvte.scm.wip.common.excel.converter.RowToLineConverter;
import com.cvte.scm.wip.common.excel.converter.dto.ExportExcelDTO;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zy
 * @date 2020-06-10 10:43
 **/
@Slf4j
public class ExcelExportUtils {

    private ExcelExportUtils() {}

    /**
     * 导出excel
     **/
    public static void exportAfterConvert(ExportExcelDTO exportExcelDTO, HttpServletResponse response) throws IOException {
        if (ObjectUtils.isNull(exportExcelDTO.getOriginHeads())) {
            exportExcelDTO.setOriginHeads(new ArrayList<>());
        }
        ConvertedTable convertedTable = convert(exportExcelDTO);

        String fileName = URLEncoder.encode(exportExcelDTO.getFileName(), "UTF-8");
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        ExcelWriterSheetBuilder writerSheetBuilder = EasyExcel.write(response.getOutputStream())
                .autoCloseStream(false)
                .head(convertedTable.getHeads())
                .sheet("sheet1")
                .registerWriteHandler(convertedTable.getWriteHandler());

        if (CollectionUtils.isNotEmpty(exportExcelDTO.getConverters())) {
            exportExcelDTO.getConverters().forEach(writerSheetBuilder::registerConverter);
        }
        log.debug("[exportAfterConvert] 开始写入excel");
        writerSheetBuilder.doWrite(convertedTable.getData());
    }


    /**
     * 对请求数据进行转换
     **/
    public static ConvertedTable convert(ExportExcelDTO exportExcelDTO) {
        List<String> headList = exportExcelDTO.getOriginHeads();
        RowToLineConverter converter = new RowToLineConverter(
                headList.toArray(new String[0]),
                convertData(headList, exportExcelDTO.getOriginData()),
                exportExcelDTO.generateBaseFields());
        ConvertedTable convertedTable = converter.convertToTable();
        converter.destroy();
        return convertedTable;
    }


    private static Object[][] convertData(List<String> heads, List<Map<String, Object>> datas) {
        Object[][] arr = new Object[datas.size()][heads.size()];
        for (int i = 0; i < datas.size(); i ++) {
            Map<String, Object> data = datas.get(i);
            arr[i] = new Object[heads.size()];
            for (int j = 0; j < heads.size(); j ++) {
                arr[i][j] = data.get(heads.get(j));
            }
        }
        datas.clear();
        return arr;
    }

}
