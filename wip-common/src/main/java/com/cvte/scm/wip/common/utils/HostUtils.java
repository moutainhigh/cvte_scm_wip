package com.cvte.scm.wip.common.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 获取本地服务器主机的工具类。
 *
 * @author : jf
 * Date    : 2020.01.01
 * Time    : 12:26
 * Email   ：jiangfeng7128@cvte.com
 */
public class HostUtils {
    private HostUtils() {
        throw new IllegalAccessError();
    }

    private static String localHostAddress;

    private static void init() {
        try {
            localHostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            localHostAddress = "-1";
        }
    }

    public static String getLocalHostAddress() {
        if (localHostAddress == null) {
            init();
        }
        return localHostAddress;
    }
}
