package com.cvte.scm.demo.sys.service;

import com.cvte.csb.core.interfaces.vo.RestResponse;

/**
 * 系统行为服务
 * @Author: wufeng
 * @Date: 2019/10/15 14:41
 */
public interface SysBehaviorService {

    /**
     * 初始化用户会话
     * @param account
     * @param remoteAddress
     */
    void initUserContext(String account, String remoteAddress);

    /**
     * 初始化用户组织会话
     * @param userOrgRelationId
     */
    void initUserUnitContext(String userOrgRelationId);

    /**
     * 系统登出处理
     */
    void doLogout();

    /**
     * 系统登录处理，内部定向到BSM
     * @param account
     * @param password
     * @return
     */
    RestResponse doLogin(String account, String password);

    /**
     * 获取登录用户简单信息，内部定向到BSM
     * @param id
     * @return
     */
    RestResponse getCurrentUserInfo(String id);
}
