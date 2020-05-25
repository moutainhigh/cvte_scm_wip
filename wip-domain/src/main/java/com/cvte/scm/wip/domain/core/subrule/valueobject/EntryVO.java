package com.cvte.scm.wip.domain.core.subrule.valueobject;

import lombok.Data;

/**
 * 一个键值对，主要为了处理 MyBatis 的结果集问题。
 *
 * @author : jf
 * Date    : 2019.03.14
 * Time    : 15:26
 * Email   ：jiangfeng7128@cvte.com
 */
@Data
public class EntryVO {
    private Object key;
    private Object value;
}