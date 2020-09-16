package com.cvte.scm.wip.domain.core.patch.service;

import com.cvte.scm.wip.domain.core.patch.entity.WipPatchEntity;
import com.cvte.scm.wip.domain.core.patch.entity.WipPatchLinesEntity;

import java.util.List;

/**
 * @version 1.0
 * @descriptions: 投料单行信息服务处理类
 * @author: ykccchen
 * @date: 2020/7/24 4:30 下午
 */
public interface WipPatchLinesService {

    void saveList(List<WipPatchLinesEntity> entityList, WipPatchEntity wipPatchEntity);

    void insertList(List<WipPatchLinesEntity> entityList, WipPatchEntity wipPatchEntity, String status);

    List<WipPatchLinesEntity> selectList(String billId);

    void delete(String billId,Integer id);

    boolean verifyWipPatchLines(String billId, String itemId);
}