package com.cvte.scm.demo.sys.service.base;


import com.cvte.scm.demo.client.sys.base.dto.SysGroup;
import com.cvte.scm.demo.client.sys.base.dto.SysPost;
import com.cvte.scm.demo.client.sys.base.dto.UserBaseDTO;

import java.util.List;

/**
 * @Author: wufeng
 * @Date: 2019/10/21 10:06
 */
public interface SysUserService {

    /**
     * 根据域账号获取用户详情
     * @param account
     * @return
     */
    UserBaseDTO getUserByAccount(String account);

    /**
     *
     * @param id
     * @return
     */
    UserBaseDTO getEnableUserInfo(String id);

    /**
     * 列出用户岗位
     * @param userId
     * @return
     */
    List<SysPost> listUserPosts(String userId);

    /**
     * 列出用户所在群组
     * @param userId
     * @return
     */
    List<SysGroup> listUserGroups(String userId);
}
