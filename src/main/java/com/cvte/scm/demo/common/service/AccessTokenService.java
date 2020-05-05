package com.cvte.scm.demo.common.service;

/**
 * @Author: wufeng
 * @Date: 2019/12/23 14:26
 */
public interface AccessTokenService {
    /**
     * 对指定token进行系统访问权限校验
     * @param iacToken
     * @return
     */
    boolean iacVerify(String iacToken);

    /**
     * 获取本系统的访问凭证
     * @return
     * @throws Exception
     */
    String getAccessToken() throws Exception;
}
