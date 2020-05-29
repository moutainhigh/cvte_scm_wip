package com.cvte.scm.wip.domain.core.requirement.repository;

import com.cvte.scm.wip.domain.core.requirement.entity.WipReqInterfaceEntity;

import java.util.Date;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 21:17
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface WipReqInterfaceRepository {

    /* 根据 group_id 查询未处理的投料单变更数据。 */
    List<WipReqInterfaceEntity> selectByGroupIds(String[] groupIds, String proceed);

    /* 根据 interface_id 查询未处理的投料单变更数据。 */
    List<WipReqInterfaceEntity> selectByInterfaceInIds(String[] interfaceIds, String proceed);

    /* 根据 interface_id 查询待确认变更的接口数据。 */
    List<WipReqInterfaceEntity> selectByNeedConfirm(String[] interfaceIds, String needConfirm);

    /* 根据投料单头表的 ID 查询投料单变更接口表中，待确认变更的数量。 */
    int getNotSolvedNumber(String headerId, String needConfirm);

    /* 获取所有未处理的接口数据 */
    List<WipReqInterfaceEntity> selectOmissionData(String proceed);

    /**
     * 查询#{timeFrom}到#{timeTo}之间处理结果在#{processStatus}的数据
     *
     * @param timeFrom 开始时间
     * @param timeTo   结束时间
     * @param proceed  处理结果
     * @author xueyuting
     * @since 2020/3/12 5:05 下午
     */
    List<WipReqInterfaceEntity> selectBetweenTimeInStatus(Date timeFrom, Date timeTo, String... proceed);

    void updateByIdSelective(WipReqInterfaceEntity interfaceEntity, List<String> idList);

    void batchUpdate(List<WipReqInterfaceEntity> entityList);
    
}
