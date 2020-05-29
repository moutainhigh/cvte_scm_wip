package com.cvte.scm.wip.domain.core.ckd.hook;

import com.cvte.scm.wip.domain.core.ckd.dto.view.McTaskDeliveringStockView;

import java.util.List;

/**
 * @author zy
 * @date 2020-05-28 19:30
 **/
public interface WriteBackHook {

    /**
     * 获取正在进行中的调拨数据
     *
     * @return java.util.List<com.cvte.scm.wip.ckd.dto.view.McTaskDeliveringStockView>
     **/
    List<McTaskDeliveringStockView> listMcTaskDeliveringStockView();
}
