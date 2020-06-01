package com.cvte.scm.wip.domain.core.requirement.service

import com.cvte.csb.core.exception.ServerException
import com.cvte.scm.wip.common.enums.error.ChangeBillErrEnum
import com.cvte.scm.wip.domain.BaseJunitTest
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsDetailEntity
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInsRepository
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLineRepository
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.BillStatusEnum
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum
import org.springframework.beans.factory.annotation.Autowired

import static org.mockito.Mockito.*

class CheckReqInsDomainServiceTest extends BaseJunitTest {

    @Autowired
    private WipReqLineRepository lineRepository

    @Autowired
    private ReqInsRepository headerRepository

    @Autowired
    private CheckReqInsDomainService checkReqInsDomainService

    def "all lines are closed"() {
        given:
        def reqIns = new ReqInsEntity().setDetailList([new ReqInsDetailEntity()])
        def reqLineList = []
        when(lineRepository.selectValidByKey(anyObject())).thenReturn(reqLineList)
        when:
        checkReqInsDomainService.validAndGetLine(reqIns)
        then:
        ServerException ie = thrown()
        ie.getMessage() == ChangeBillErrEnum.TARGET_LINE_INVALID.desc
    }

    def "one of lines is issued"() {
        given:
        def reqIns = new ReqInsEntity().setDetailList([new ReqInsDetailEntity().setInsDetailId("detail1")])
        def reqLineList = [
                new WipReqLineEntity(lineStatus: BillStatusEnum.PREPARED.getCode()),
                new WipReqLineEntity(lineStatus: BillStatusEnum.ISSUED.getCode())
        ]
        def reqLineMap = new HashMap()
        reqLineMap.put("detail1", reqLineList)
        when:
        checkReqInsDomainService.checkLineStatus(reqIns, reqLineMap)
        then:
        ServerException ie = thrown()
        ie.getMessage() == ChangeBillErrEnum.TARGET_LINE_ISSUED.desc
    }

    def "replace and qty is null"() {
        given:
        def reqIns = new ReqInsEntity().setDetailList([new ReqInsDetailEntity().setInsDetailId("detail1").setOperationType(ChangedTypeEnum.REPLACE.getCode())])
        def reqLineList = [
                new WipReqLineEntity(reqQty: 100)
        ]
        def reqLineMap = new HashMap()
        reqLineMap.put("detail1", reqLineList)
        when:
        checkReqInsDomainService.checkPartMix(reqIns,reqLineMap)
        then:
        notThrown ServerException
    }

    def "part mix"() {
        given:
        def reqIns = new ReqInsEntity().setDetailList([new ReqInsDetailEntity().setInsDetailId("detail1").setOperationType(ChangedTypeEnum.REPLACE.getCode()).setItemQty(new BigDecimal(100))])
        def reqLineList = [
                new WipReqLineEntity(),
                new WipReqLineEntity(reqQty: 30),
                new WipReqLineEntity(reqQty: 50)
        ]
        def reqLineMap = new HashMap()
        reqLineMap.put("detail1", reqLineList)
        when:
        checkReqInsDomainService.checkPartMix(reqIns, reqLineMap)
        then:
        ServerException ie = thrown()
        ie.getMessage() == ChangeBillErrEnum.PART_MIX.desc
    }

    def "pre ins exists"() {
        given:
        def reqIns = ReqInsEntity.get().setAimHeaderId("header1").setAimReqLotNo("XX1")
        def existsReqIns = [new ReqInsEntity()]
        when(headerRepository.selectByAimHeaderId(anyObject(), anyObject())).thenReturn(existsReqIns)
        when:
        checkReqInsDomainService.checkPreInsExists(reqIns)
        then:
        ServerException ie = thrown()
        ie.getMessage() == ChangeBillErrEnum.EXISTS_PRE_INS.desc + "XX1"
    }
}
