package com.cvte.scm.wip.domain.common.context;

import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.scm.wip.domain.common.user.entity.PostEntity;
import com.cvte.scm.wip.domain.common.user.entity.RoleEntity;

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

    public static void setRoleList(List<RoleEntity> roleList) {
        CurrentContext.set("roleList", roleList);
    }

    public static List<RoleEntity> getRoleList() {
        return ObjectUtils.isNull(get("roleList")) ? Collections.emptyList() : (List<RoleEntity>) get("roleList");
    }

    public static void setPostList(List<PostEntity> postList) {
        CurrentContext.set("postList", postList);
    }

    public static List<PostEntity> getPostList() {
        return ObjectUtils.isNull(get("postList")) ? Collections.emptyList() : (List<PostEntity>) get("postList");
    }


    public enum UserType {
        INNER_USER, VENDOR, UNKNOWN
    }


}
