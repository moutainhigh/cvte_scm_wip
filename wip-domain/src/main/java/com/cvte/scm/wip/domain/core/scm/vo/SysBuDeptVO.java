package com.cvte.scm.wip.domain.core.scm.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zy
 * @date 2020-05-26 17:08
 **/
@Data
@EqualsAndHashCode
public class SysBuDeptVO {

    private String deptName;

    private String deptCode;

    private String buCode;

    private String buName;
}
