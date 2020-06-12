package com.cvte.scm.wip.common.utils;

import com.cvte.csb.base.commons.OperatingUser;

import java.util.Optional;

import static com.cvte.csb.base.context.CurrentContext.getCurrentOperatingUser;

/**
 * 处理当前上下文的工具类，主要是为空信息赋默认值。
 *
 * @author : jf
 * Date    : 2020.01.10
 * Time    : 09:24
 * Email   ：jiangfeng7128@cvte.com
 */
public class CurrentContextUtils {

    private static final OperatingUser EMPTY_USER = new OperatingUser();

    /**
     * 获取当前操作用户信息，如果不存在，则直接返回空的用户信息。
     */
    public static OperatingUser getOrEmptyOperatingUser() {
        return Optional.ofNullable(getCurrentOperatingUser()).orElse(EMPTY_USER);
    }

    public static String getOrDefaultUserId(String defaultUserId) {
        return Optional.ofNullable(getOrEmptyOperatingUser().getId()).orElse(defaultUserId);
    }

    public static OperatingUser createOperatingUser(String userId) {
        OperatingUser user = new OperatingUser();
        user.setId(userId);
        return user;
    }

    public static OperatingUser mockOperatingUser(String mockUser) {
        OperatingUser user = new OperatingUser();
        user.setName(mockUser);
        user.setId(mockUser);
        user.setAccount(mockUser);
        return user;
    }
}