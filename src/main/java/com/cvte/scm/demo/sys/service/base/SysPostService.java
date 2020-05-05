package com.cvte.scm.demo.sys.service.base;


import com.cvte.scm.demo.client.sys.base.dto.SysPost;
import com.cvte.scm.demo.client.sys.base.dto.SysUser;

import java.util.List;

/**
 * @Author: wufeng
 * @Date: 2019/11/9 13:44
 */
public interface SysPostService {

    List<SysUser> getUserListByPostId(String postId);

    SysPost getPost(String id);

    SysUser getUserByPostIdAndUserId(String postId, String userId);
}
