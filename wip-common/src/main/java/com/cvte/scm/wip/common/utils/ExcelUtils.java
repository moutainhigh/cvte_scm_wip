package com.cvte.scm.wip.common.utils;

import com.alibaba.excel.EasyExcel;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.scm.wip.common.listener.ExcelListener;
import lombok.extern.slf4j.Slf4j;
import org.bson.io.Bits;

import javax.sql.rowset.BaseRowSet;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author : jf
 * Date    : 2019.11.06
 * Time    : 09:30
 * Email   ：jiangfeng7128@cvte.com
 */
@Slf4j
public class ExcelUtils {
    /**
     * xls文件魔数：0xD0CF11E0
     */
    private static final int XLS_MAGIC_NUMBER = -791735840;
    /**
     * xlsx文件魔数：504B0304
     */
    private static final int XLSX_MAGI_NUMBER = 1347093252;

    /**
     * 读取Excel的魔数，长度为4个字节。
     */
    private static long readExcelMagicNumber(InputStream inputStream) {
        inputStream.mark(0);
        byte[] buffer = new byte[4];
        try {
            inputStream.read(buffer);
            inputStream.reset();
        } catch (IOException e) {
            throw new ParamsIncorrectException("Excel文件解析错误！");
        }
        return Bits.readIntBE(buffer, 0);
    }

    /**
     * 读取Excel文件，并转换为对象列表。
     * <p>
     * sheet 的序号默认从1开始，sheet 名称默认为"Sheet1"。
     *
     * @param inputStream Excel文件输入流
     * @param clazz       Class对象
     * @param <T>         对象类型
     * @return 对象列表
     */
    public static <T extends BaseRowSet> List<T> readExcel(final InputStream inputStream, final Class<T> clazz) {
        return readExcel(inputStream, clazz, new ExcelListener<>(), 1, "Sheet1");
    }

    /**
     * 读取Excel文件，并转换为对象列表。
     *
     * @param inputStream Excel文件输入流
     * @param clazz       Class对象
     * @param <T>         对象类型
     * @return 对象列表
     */
    public static <T extends BaseRowSet> List<T> readExcel(final InputStream inputStream, final Class<T> clazz,
                                                           ExcelListener<T> listener) {
        return readExcel(inputStream, clazz, listener, 1, "Sheet1");
    }

    /**
     * 读取Excel文件，并转换为对象列表。
     *
     * @param inputStream   Excel文件输入流
     * @param clazz         Class对象
     * @param headRowNumber 首行数据的序号
     * @param sheetName     sheet 的名称
     * @param <T>           对象类型
     * @return 对象列表
     */
    public static <T extends BaseRowSet> List<T> readExcel(final InputStream inputStream, final Class<T> clazz,
                                                           int headRowNumber, String sheetName) {
        return readExcel(inputStream, clazz, new ExcelListener<>(), headRowNumber, sheetName);
    }

    /**
     * 读取Excel文件，并转换为对象列表。
     *
     * @param inputStream   Excel文件输入流
     * @param clazz         Class对象
     * @param listener      自定义的监视器
     * @param headRowNumber 首行数据的序号
     * @param sheetName     sheet 的名称
     * @param <T>           对象类型
     * @return 对象列表
     */
    public static <T extends BaseRowSet> List<T> readExcel(final InputStream inputStream, final Class<T> clazz,
                                                           ExcelListener<T> listener, int headRowNumber, String sheetName) {
        if (inputStream == null) {
            throw new ParamsIncorrectException("Excel文件为空！");
        }
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        long excelMagicNumber = readExcelMagicNumber(bis);
        if (excelMagicNumber != XLS_MAGIC_NUMBER && excelMagicNumber != XLSX_MAGI_NUMBER) {
            throw new ParamsIncorrectException("文件格式错误：非Excel文件！");
        }
        EasyExcel.read(bis, clazz, listener).headRowNumber(headRowNumber).sheet(sheetName).doRead();
        return listener.getData();
    }


}
