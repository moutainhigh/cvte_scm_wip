package com.cvte.scm.demo.sys.context;

import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.scm.demo.client.sys.base.dto.SysPost;
import com.cvte.scm.demo.client.sys.base.dto.SysRoleDTO;

import java.util.Collections;
import java.util.List;

/**
 * 全局系统上下文对象
 *
 * @Author: wufeng
 * @Date: 2019/12/23 16:05
 */
public class GlobalContext extends CurrentContext {

    public GlobalContext() {

    }

    public static void setRoleList(List<SysRoleDTO> roleList) {
        CurrentContext.set("roleList", roleList);
    }

    public static List<SysRoleDTO> getRoleList() {
        return ObjectUtils.isNull(get("roleList")) ? Collections.emptyList() : (List<SysRoleDTO>) get("roleList");
    }

    public static void setPostList(List<SysPost> postList) {
        CurrentContext.set("postList", postList);
    }

    public static List<SysPost> getPostList() {
        return ObjectUtils.isNull(get("postList")) ? Collections.emptyList() : (List<SysPost>) get("postList");
    }


    public enum UserType {
        INNER_USER, VENDOR, UNKNOWN
    }


}
