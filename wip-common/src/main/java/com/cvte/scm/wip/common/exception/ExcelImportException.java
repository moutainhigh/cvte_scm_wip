package com.cvte.scm.wip.common.exception;


import com.cvte.csb.core.exception.ServerException;

/**
 * @author zy
 * @date 2020-05-22 15:18
 **/
public class ExcelImportException extends ServerException {

    public ExcelImportException(String errMsg) {
        super("50050003", errMsg);
    }
}
