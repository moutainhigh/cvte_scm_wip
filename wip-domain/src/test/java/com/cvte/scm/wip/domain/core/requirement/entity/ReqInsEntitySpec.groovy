package com.cvte.scm.wip.domain.core.requirement.entity

import com.cvte.csb.core.exception.ServerException
import com.cvte.scm.wip.common.enums.error.ReqInsErrEnum
import com.cvte.scm.wip.domain.BaseJunitTest
import com.cvte.scm.wip.domain.core.item.service.ScmItemService
import com.cvte.scm.wip.domain.core.requirement.repository.WipLotRepository
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.InsOperationTypeEnum
import org.springframework.beans.factory.annotation.Autowired

import static org.mockito.Mockito.*

class ReqInsEntitySpec extends BaseJunitTest {

    @Autowired
    private WipLotRepository wipLotRepository
    @Autowired
    private ScmItemService scmItemService

    def "新增未指定批次"() {
        given:
        ReqInsEntity insEntity = ReqInsEntity.get().setDetailList([
                ReqInsDetailEntity.get().setOperationType(InsOperationTypeEnum.ADD.code)
        ])
        when(wipLotRepository.selectByHeaderId(anyObject())).thenReturn(null)
        when:
        insEntity.parse(null)
        then:
        ServerException se = thrown()
        se.status == ReqInsErrEnum.ADD_LOT_NULL.getCode()
    }

    def "删除投料"() {
        given:
        ReqInsEntity insEntity = ReqInsEntity.get().setDetailList([
                ReqInsDetailEntity.get().setInsDetailId("d1").setOperationType(InsOperationTypeEnum.DELETE.code)
        ])
        def reqLineMap = ["d1": [new WipReqLineEntity()]]
        when:
        def reqLineList = insEntity.parse(reqLineMap)
        then:
        reqLineList[0].changeType == ChangedTypeEnum.DELETE.getCode()
    }

    def "替换投料"() {
        given:
        ReqInsEntity insEntity = ReqInsEntity.get().setDetailList([
                ReqInsDetailEntity.get().setInsDetailId("d1").setOperationType(InsOperationTypeEnum.REPLACE.code).setItemIdNew("111")
        ])
        def reqLineMap = ["d1": [new WipReqLineEntity(itemNo: "004.001")]]
        when(scmItemService.getItemNo(anyObject(), anyObject())).thenReturn("004.002")
        when:
        def reqLineList = insEntity.parse(reqLineMap)
        then:
        def reqLine = reqLineList[0]
        reqLine.changeType == ChangedTypeEnum.REPLACE.getCode() && reqLine.beforeItemNo == "004.001" && reqLine.itemNo == "004.002"
    }

}
