package com.cvte.scm.wip.domain.core.requirement.service

import com.cvte.csb.core.exception.ServerException
import com.cvte.scm.wip.common.enums.error.ReqInsErrEnum
import com.cvte.scm.wip.domain.BaseJunitTest
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsDetailEntity
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInsRepository
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLineRepository
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqLineKeyQueryVO
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
        when(lineRepository.selectValidByKey(anyObject() as WipReqLineKeyQueryVO, anyList())).thenReturn(reqLineList)
        when:
        checkReqInsDomainService.validAndGetLine(reqIns)
        then:
        ServerException ie = thrown()
        ie.getMessage() == "投料行不存在或用量为空"
    }

    def "change qty equals to unissued qty"() {
        given:
        def reqIns = new ReqInsEntity().setStatus(ProcessingStatusEnum.PENDING.code)
                .setDetailList([ReqInsDetailEntity.get().setInsDetailId("detail1").setItemQty(new BigDecimal("100"))])
        def reqLineList = [
                new WipReqLineEntity(lineStatus: BillStatusEnum.PREPARED.getCode(), reqQty: 100, issuedQty: null),
                new WipReqLineEntity(lineStatus: BillStatusEnum.ISSUED.getCode(), reqQty: 100, issuedQty: 100)
        ]
        def reqLineMap = new HashMap()
        reqLineMap.put("detail1", reqLineList)
        when:
        checkReqInsDomainService.checkLineStatus(reqIns, reqLineMap)
        then:
        notThrown()
    }

    def "change qty equals smaller than unissued qty"() {
        given:
        def reqIns = new ReqInsEntity().setStatus(ProcessingStatusEnum.PENDING.code)
                .setDetailList([ReqInsDetailEntity.get().setInsDetailId("detail1").setItemQty(new BigDecimal("100"))])
        def reqLineList = [
                new WipReqLineEntity(lineStatus: BillStatusEnum.PREPARED.getCode(), reqQty: 100, issuedQty: 75),
                new WipReqLineEntity(lineStatus: BillStatusEnum.ISSUED.getCode(), reqQty: 100, issuedQty: 75)
        ]
        def reqLineMap = new HashMap()
        reqLineMap.put("detail1", reqLineList)
        when:
        checkReqInsDomainService.checkLineStatus(reqIns, reqLineMap)
        then:
        ServerException ie = thrown()
        ie.getMessage() == ReqInsErrEnum.TARGET_LINE_ISSUED.desc
    }

    def "change qty equals bigger than unissued qty"() {
        given:
        // user issued lot_2, but want to change lot_3H
        def reqIns = new ReqInsEntity().setStatus(ProcessingStatusEnum.PENDING.code)
                .setDetailList([ReqInsDetailEntity.get().setInsDetailId("detail1").setItemQty(new BigDecimal("200"))])
        def reqLineList = [
                new WipReqLineEntity(lineStatus: BillStatusEnum.ISSUED.getCode(), lotNumber: "lot_1", reqQty: 100, issuedQty: 40),
                new WipReqLineEntity(lineStatus: BillStatusEnum.ISSUED.getCode(), lotNumber: "lot_2", reqQty: 200, issuedQty: 80),
                new WipReqLineEntity(lineStatus: BillStatusEnum.ISSUED.getCode(), lotNumber: "lot_3H", reqQty: 200, issuedQty: 80)
        ]
        def reqLineMap = new HashMap()
        reqLineMap.put("detail1", reqLineList)
        when:
        checkReqInsDomainService.checkLineStatus(reqIns, reqLineMap)
        then:
        notThrown()
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
        ie.getMessage() == ReqInsErrEnum.PART_MIX.desc
    }

    def "pre ins exists"() {
        given:
        def df = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS")
        def reqIns = ReqInsEntity.get().setStatus(ProcessingStatusEnum.PENDING.code).setAimHeaderId("header1").setAimReqLotNo("XX1").setEnableDate(df.parse("2020-06-02 00:00:00"))
        def existsReqIns = [new ReqInsEntity().setInsHeaderId("insHead1").setEnableDate(df.parse("2020-06-01 00:00:00"))]
        when(headerRepository.selectByAimHeaderId(anyString(), anyList())).thenReturn(existsReqIns)
        when:
        checkReqInsDomainService.checkPreInsExists(reqIns)
        then:
        ServerException ie = thrown()
        ie.getMessage() == ReqInsErrEnum.EXISTS_PRE_INS.desc + ","
    }
}
