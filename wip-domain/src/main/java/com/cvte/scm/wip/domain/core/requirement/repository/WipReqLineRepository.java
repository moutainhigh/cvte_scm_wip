package com.cvte.scm.wip.domain.core.requirement.repository;

import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqLineKeyQueryVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.BillStatusEnum;
import tk.mybatis.mapper.entity.Example;

import java.util.Collection;
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

    List<WipReqLineEntity> selectValidByKey(WipReqLineKeyQueryVO keyQueryVO, Collection<BillStatusEnum> statusEnumSet);

    List<WipReqLineEntity> selectValidByKey(WipReqLineKeyQueryVO keyQueryVO, List<String> statusList);

    void insertSelective(WipReqLineEntity lineEntity);

    void updateSelectiveById(WipReqLineEntity lineEntity);

    void writeIncrementalData(List<String> wipEntityIdList, List<Integer> organizationIdList);

    /**
     * 更改单生成的非标工单会直接生成已发放的投料单, 投料行引入的EBS视图是每天两次, 会漏掉这些投料行, 所以额外增加一个补漏的方法
     * @since 2020/7/24 3:22 下午
     * @author xueyuting
     */
    void writeLackLines(List<String> wipEntityIdList, List<Integer> organizationIdList);

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
     * @param changeType 变更类型
     * @param organization 组织
     * @param itemNoList 用于校验的物料编号
     * @param dimensionId 组织维度
     * @param outRangeItemNoList {out}筛选出的范围外的物料编号
     * @return 允许手工变更的物料类别列表
     */
    List<String> selectOutRangeItemList(String changeType, String organization, List<String> itemNoList, String dimensionId, List<String> outRangeItemNoList);

}
