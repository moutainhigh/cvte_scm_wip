package com.cvte.scm.wip.infrastructure.client.common.dto;

import lombok.Data;

/**
 * @Author: wufeng
 * @Date: 2019/12/23 15:35
 */
@Data
public class FeignResult<T> {

    /**
     * 状态码
     */
    private String status;

    /**
     * 说明
     */
    private String message;

    /**
     * 数据
     */
    private T data;
}