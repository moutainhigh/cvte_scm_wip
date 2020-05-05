package com.cvte.scm.demo.sys.service.base;


import com.cvte.scm.demo.client.sys.base.dto.SysRoleDTO;

import java.util.List;


/**
 * @Author: wufeng
 * @Date: 2019/10/21 09:41
 */
public interface SysRoleService {

    /**
     * 根据用户Id获取角色列表，默认当前用户
     * @param userId
     * @return
     */
    List<SysRoleDTO> listSysRoleByUserId(String userId);

    /**
     * 判断指定用户是否管理员
     * @param userId
     * @return
     */
    Boolean isAdmin(String userId);

    /**
     * 判断当前用户是否管理员
     * @return
     */
    Boolean isAdmin();
}
