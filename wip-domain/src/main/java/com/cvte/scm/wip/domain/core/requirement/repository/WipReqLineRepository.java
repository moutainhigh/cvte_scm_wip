package com.cvte.scm.wip.domain.core.requirement.repository;

import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqLineKeyQueryVO;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 15:54
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface WipReqLineRepository {

    WipReqLineEntity selectById(String id);

    List<WipReqLineEntity> selectList(WipReqLineEntity queryEntity);

    List<WipReqLineEntity> selectByExample(Example example);

    List<WipReqLineEntity> selectValidByKey(WipReqLineKeyQueryVO keyQueryVO, List<String> statusList);

    void insertSelective(WipReqLineEntity lineEntity);

    void updateSelectiveById(WipReqLineEntity lineEntity);

    void writeIncrementalData(List<String> wipEntityIdList, List<Integer> organizationIdList);

    /**
     * 创建一个定制化的 {@link Example} 对象。包含了大批次号、组织、小批次号、工序号、位号、物料ID、物料编号以及行版本字段的查询条件。
     * <p>
     * 这些字段组成了投料单表{@code wip_req_lines}的索引
     */
    Example createCustomExample(WipReqLineEntity wipReqLine);

    Example createExample();

    List<WipReqLineEntity> selectByColumnAndStatus(WipReqLineEntity lineEntity, int status);

    /**
     *
     * @since 2020/7/17 6:54 下午
     * @author xueyuting
     * @param organization 组织
     * @param itemNoList 用于校验的物料编号
     * @param outRangeItemNoList {out}筛选出的范围外的物料编号
     * @return 允许手工变更的物料类别列表
     */
    List<String> selectOutRangeItemList(String organization, List<String> itemNoList, List<String> outRangeItemNoList);

}
