package com.cvte.scm.wip.common.constants;

/**
 * @Author: wufeng
 * @Date: 2019/12/25 11:38
 */
public class ResponseDefinition {


    public static final String SUCCESS_CODE = "0";
    public static final String SUCCESS_MESSAGE = "success";

    //参数类异常，前缀：400

    public static final String ERR_PARAM_CODE = "4000001";
    public static final String ERR_PARAM_MESSAGE = "必填参数未提供";

    public static final String ERR_PARAM_FORMAT_CODE = "4000002";
    public static final String ERR_PARAM_FORMAT_MESSAGE = "所提供的参数格式有误";

    public static final String ERR_PARAM_BUSINESS_CODE = "4000003";
    public static final String ERR_PARAM_BUSINESS_MESSAGE = "业务参数异常";

    // 授权类异常，前缀：410

    // 操作类异常，前缀：420
    public static final String ERR_PARAM_OPERATION_CODE = "4200001";
    public static final String ERR_PARAM_OPERATION_MESSAGE = "操作不正确";

    // 系统类异常，前缀：500

    public static final String SERVER_ERR_CODE = "5000000";
    public static final String SERVER_ERR_MESSAGE = "服务器内部错误，请联系管理员";

}
