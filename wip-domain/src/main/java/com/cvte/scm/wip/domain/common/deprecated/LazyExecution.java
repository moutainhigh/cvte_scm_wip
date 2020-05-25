package com.cvte.scm.wip.domain.common.deprecated;

/**
 * 这是一个函数式接口，主要用于延迟加载操作，比如事务一致性问题的处理
 *
 * @author : jf
 * Date    : 2019.02.18
 * Time    : 20:48
 * Email   ：jiangfeng7128@cvte.com
 */
@FunctionalInterface
public interface LazyExecution {
    void execute();
}
