package com.cvte.scm.wip.domain.core.thirdpart.ebs.exception;


import com.cvte.csb.core.exception.ServerException;

/**
 * @author zy
 * @date 2020-05-11 10:20
 **/
public class EbsInvokeException extends ServerException {

    public EbsInvokeException(String errMsg) {
        super("5000500", errMsg);
    }
}
