package com.cvte.scm.wip.domain.core.requirement.entity

import com.cvte.scm.wip.domain.BaseJunitTest
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipLotVO

class ReqInsDetailEntityTest extends BaseJunitTest {

    def "数量增/减"() {
        given:
        def lotNoList = ["lot1", "lot2"]
        def posNoList=  ["A1", "B1", "C1", "D1"]
        def reqLineList1 = [
                new WipReqLineEntity(lotNumber: lotNoList[0], posNo: posNoList[0], reqQty: 100),
                new WipReqLineEntity(lotNumber: lotNoList[0], posNo: posNoList[1], reqQty: 100),
                new WipReqLineEntity(lotNumber: lotNoList[1], posNo: posNoList[2], reqQty: 50),
                new WipReqLineEntity(lotNumber: lotNoList[1], posNo: posNoList[3], reqQty: 50),
        ]
        def reqLineList2 = [
                new WipReqLineEntity(lotNumber: lotNoList[0], posNo: posNoList[0], reqQty: 100),
                new WipReqLineEntity(lotNumber: lotNoList[0], posNo: posNoList[1], reqQty: 100),
                new WipReqLineEntity(lotNumber: lotNoList[1], posNo: posNoList[2], reqQty: 50),
                new WipReqLineEntity(lotNumber: lotNoList[1], posNo: posNoList[3], reqQty: 50),
        ]
        def wipLotMap = [
                (lotNoList[0]): new WipLotVO(lotQuantity: 100),
                (lotNoList[1]): new WipLotVO(lotQuantity: 50),
        ]
        when:
        ReqInsDetailEntity.get().allocateQty(reqLineList1, wipLotMap, new BigDecimal("0.5"));
        ReqInsDetailEntity.get().allocateQty(reqLineList2, wipLotMap, new BigDecimal("0.5").negate());
        then:
        reqLineList1[0].reqQty == 125 && reqLineList1[0].unitQty == 1.25
        reqLineList1[1].reqQty == 125 && reqLineList1[1].unitQty == 1.25
        reqLineList1[2].reqQty == 63 && reqLineList1[2].unitQty == 1.26
        reqLineList1[3].reqQty == 62 && reqLineList1[3].unitQty == 1.24
        and:
        reqLineList2[0].reqQty == 75 && reqLineList2[0].unitQty == 0.75
        reqLineList2[1].reqQty == 75 && reqLineList2[1].unitQty == 0.75
        reqLineList2[2].reqQty == 37 && reqLineList2[2].unitQty == 0.74
        reqLineList2[3].reqQty == 38 && reqLineList2[3].unitQty == 0.76
    }

    def "数量减为0"() {
        given:
        def lotNoList = ["lot1", "lot2"]
        def posNoList=  ["A1", "B1", "C1", "D1"]
        def reqLineList1 = [
                new WipReqLineEntity(lotNumber: lotNoList[0], posNo: posNoList[0], reqQty: 100),
                new WipReqLineEntity(lotNumber: lotNoList[0], posNo: posNoList[1], reqQty: 100),
                new WipReqLineEntity(lotNumber: lotNoList[1], posNo: posNoList[2], reqQty: 50),
                new WipReqLineEntity(lotNumber: lotNoList[1], posNo: posNoList[3], reqQty: 50),
        ]
        def wipLotMap = [
                (lotNoList[0]): new WipLotVO(lotQuantity: 100),
                (lotNoList[1]): new WipLotVO(lotQuantity: 50),
        ]
        when:
        ReqInsDetailEntity.get().allocateQty(reqLineList1, wipLotMap, new BigDecimal("2").negate());
        then:
        reqLineList1[0].reqQty == 0 && reqLineList1[0].unitQty == 0
        reqLineList1[1].reqQty == 0 && reqLineList1[1].unitQty == 0
        reqLineList1[2].reqQty == 0 && reqLineList1[2].unitQty == 0
        reqLineList1[3].reqQty == 0 && reqLineList1[3].unitQty == 0
    }

    def "增加数量的目标投料行不存在则新增"() {
        given:
        def lotNoList = ["lot1", "lot2"]
        def unitQty = "0.33333333"
        def wipLotMap = [
                (lotNoList[0]): new WipLotVO(lotQuantity: 100, lotNumber: lotNoList[0]),
                (lotNoList[1]): new WipLotVO(lotQuantity: 50, lotNumber: lotNoList[1]),
        ]
        when:
        def reqLineList1 = ReqInsDetailEntity.get().setPosNo("A1").setItemUnitQty(new BigDecimal(unitQty))
                .parseIncreaseType(null, wipLotMap)
        then:
        reqLineList1[0].reqQty == 34 && reqLineList1[0].unitQty == 34 / 100
        reqLineList1[1].reqQty == 16 && reqLineList1[1].unitQty == 16 / 50
    }

}