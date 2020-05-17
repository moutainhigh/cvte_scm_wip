package com.cvte.scm.wip.domain.common.serial;

import java.util.List;

/**
 * @Author: wufeng
 * @Date: 2019/7/11 14:45
 */
public interface SerialNoGenerationService {

    /**
     * 获取下一个非树型流水号
     **/
    String getNextSerialNumberByCode(String serialCode);

    /**
     * 获取下一个非树型流水号(包含传参)
     */
    String getNextSerialNumberByCode(String serialCode, List<String> params);
}
