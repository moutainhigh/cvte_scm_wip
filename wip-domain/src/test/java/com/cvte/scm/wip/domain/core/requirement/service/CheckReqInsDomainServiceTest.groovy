package com.cvte.scm.wip.domain.core.requirement.service

import com.cvte.csb.core.exception.ServerException
import com.cvte.scm.wip.common.enums.error.ReqInsErrEnum
import com.cvte.scm.wip.domain.BaseJunitTest
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsDetailEntity
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInsRepository
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLineRepository
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.BillStatusEnum
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.InsOperationTypeEnum
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ProcessingStatusEnum
import org.springframework.beans.factory.annotation.Autowired

import java.text.SimpleDateFormat

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
        def reqIns = new ReqInsEntity().setStatus(ProcessingStatusEnum.PENDING.code)
                .setDetailList([ReqInsDetailEntity.get()])
        def reqLineList = []
        when(lineRepository.selectValidByKey(anyObject(), anyObject())).thenReturn(reqLineList)
        when:
        checkReqInsDomainService.validAndGetLine(reqIns)
        then:
        ServerException ie = thrown()
        ie.getMessage() == ReqInsErrEnum.TARGET_LINE_INVALID.desc
    }

    def "one of lines is issued"() {
        given:
        def reqIns = new ReqInsEntity().setStatus(ProcessingStatusEnum.PENDING.code)
                .setDetailList([ReqInsDetailEntity.get().setInsDetailId("detail1")])
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
        ie.getMessage() == ReqInsErrEnum.TARGET_LINE_ISSUED.desc
    }

    def "replace and qty is null"() {
        given:
        def reqIns = new ReqInsEntity().setStatus(ProcessingStatusEnum.PENDING.code)
                .setDetailList([ReqInsDetailEntity.get().setInsDetailId("detail1").setOperationType(InsOperationTypeEnum.REPLACE.getCode())])
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
        def reqIns = new ReqInsEntity().setDetailList([ReqInsDetailEntity.get().setInsDetailId("detail1").setOperationType(InsOperationTypeEnum.REPLACE.getCode()).setItemQty(new BigDecimal(100))])
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
        ie.getMessage() == ReqInsErrEnum.PART_MIX.desc + reqIns.getDetailList().get(0).toString()
    }

    def "pre ins exists"() {
        given:
        def df = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS")
        def reqIns = ReqInsEntity.get().setStatus(ProcessingStatusEnum.PENDING.code).setAimHeaderId("header1").setAimReqLotNo("XX1").setEnableDate(df.parse("2020-06-02 00:00:00"))
        def existsReqIns = [new ReqInsEntity().setInsHeaderId("insHead1").setEnableDate(df.parse("2020-06-01 00:00:00"))]
        when(headerRepository.selectByAimHeaderId(anyObject(), anyObject())).thenReturn(existsReqIns)
        when:
        checkReqInsDomainService.checkPreInsExists(reqIns)
        then:
        ServerException ie = thrown()
        ie.getMessage() == ReqInsErrEnum.EXISTS_PRE_INS.desc + "XX1"
    }
}
