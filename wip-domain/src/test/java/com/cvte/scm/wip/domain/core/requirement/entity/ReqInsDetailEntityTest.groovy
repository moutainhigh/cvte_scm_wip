package com.cvte.scm.wip.domain.core.requirement.entity

import com.cvte.scm.wip.domain.BaseJunitTest
import com.cvte.scm.wip.domain.core.requirement.util.ReqInsSplitHelper
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipLotVO

import java.math.RoundingMode

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
        ReqInsSplitHelper.allocateQty(ReqInsDetailEntity.get(), reqLineList1, wipLotMap, new BigDecimal("0.5"));
        ReqInsSplitHelper.allocateQty(ReqInsDetailEntity.get(), reqLineList2, wipLotMap, new BigDecimal("0.5").negate());
        then:
        reqLineList1[0].reqQty == 63 && reqLineList1[0].unitQty == 1.26
        reqLineList1[1].reqQty == 62 && reqLineList1[1].unitQty == 1.24
        reqLineList1[2].reqQty == 125 && reqLineList1[2].unitQty == 1.25
        reqLineList1[3].reqQty == 125 && reqLineList1[3].unitQty == 1.25
        and:
        reqLineList2[0].reqQty == 37 && reqLineList2[0].unitQty == 0.74
        reqLineList2[1].reqQty == 38 && reqLineList2[1].unitQty == 0.76
        reqLineList2[2].reqQty == 75 && reqLineList2[2].unitQty == 0.75
        reqLineList2[3].reqQty == 75 && reqLineList2[3].unitQty == 0.75
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
        ReqInsSplitHelper.allocateQty(ReqInsDetailEntity.get(), reqLineList1, wipLotMap, new BigDecimal("2").negate());
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

    def "两个小批次,增加数量"() {
        given:
        def lotNoList = ["SK20051866_1", "SK20051866_2"]
        def unitQty = "0.36429872"
        def wipLotMap = [
                (lotNoList[0]): new WipLotVO(lotQuantity: 250, lotNumber: lotNoList[0]),
                (lotNoList[1]): new WipLotVO(lotQuantity: 55, lotNumber: lotNoList[1]),
        ]
        when:
        def reqLineList1 = ReqInsDetailEntity.get().setPosNo("A1").setItemQty(new BigDecimal("111")).setItemUnitQty(new BigDecimal(unitQty))
                .parseIncreaseType(null, wipLotMap)
        then:
        reqLineList1[0].reqQty == 91 && reqLineList1[0].unitQty == BigDecimal.valueOf(91).divide(BigDecimal.valueOf(250), 6, RoundingMode.DOWN).doubleValue()
        reqLineList1[1].reqQty == 20 && reqLineList1[1].unitQty == BigDecimal.valueOf(20).divide(BigDecimal.valueOf(55), 6, RoundingMode.DOWN).doubleValue()
    }

    def "两个小批次,减少数量"() {
        given:
        def lotNoList = ["SK20051866_1", "SK20051866_2"]
        def unitQty = "0.36429872"
        def wipLotMap = [
                (lotNoList[0]): new WipLotVO(lotQuantity: 250, lotNumber: lotNoList[0]),
                (lotNoList[1]): new WipLotVO(lotQuantity: 55, lotNumber: lotNoList[1]),
        ]
        def reqLineList = [
                new WipReqLineEntity(lotNumber: lotNoList[0], posNo: "CD158", reqQty: 250),
                new WipReqLineEntity(lotNumber: lotNoList[1], posNo: "CD158", reqQty: 55),
        ]
        when:
        def insDetail = ReqInsDetailEntity.get().setPosNo("A1").setItemQty(new BigDecimal("111")).setItemUnitQty(new BigDecimal(unitQty))
        def reqLineList1 = ReqInsSplitHelper.allocateQty(insDetail, reqLineList, wipLotMap, new BigDecimal(unitQty).negate())
        then:
        reqLineList1[0].reqQty == 35 && reqLineList1[0].unitQty == BigDecimal.valueOf(35).divide(BigDecimal.valueOf(55), 6, RoundingMode.DOWN).doubleValue()
        reqLineList1[2].reqQty == 159 && reqLineList1[2].unitQty == BigDecimal.valueOf(159).divide(BigDecimal.valueOf(250), 6, RoundingMode.DOWN).doubleValue()
    }

    def "4个小批次, 多次减少不均匀数量"() {
        given:
        def lotNoList = ["CKD20090575_1", "CKD20090575_2", "CKD20090575_3", "CKD20090575_4"]
        def unitQty = "0.24646062"
        def lotQty = 10030
        def wipLotMap = [
                (lotNoList[0]): new WipLotVO(lotQuantity: lotQty, lotNumber: lotNoList[0]),
                (lotNoList[1]): new WipLotVO(lotQuantity: lotQty, lotNumber: lotNoList[1]),
                (lotNoList[2]): new WipLotVO(lotQuantity: lotQty, lotNumber: lotNoList[2]),
                (lotNoList[3]): new WipLotVO(lotQuantity: lotQty, lotNumber: lotNoList[3]),
        ]
        def reqLineList = [
                new WipReqLineEntity(lotNumber: lotNoList[0], posNo: "PCB", reqQty: 2475),
                new WipReqLineEntity(lotNumber: lotNoList[1], posNo: "PCB", reqQty: 2471),
                new WipReqLineEntity(lotNumber: lotNoList[2], posNo: "PCB", reqQty: 2471),
                new WipReqLineEntity(lotNumber: lotNoList[3], posNo: "PCB", reqQty: 2471),
        ]
        def reqInsDetail = ReqInsDetailEntity.get().setPosNo("PCB").setItemQty(new BigDecimal("9888")).setItemUnitQty(new BigDecimal(unitQty));
        when:
        def result = ReqInsSplitHelper.allocateQty(reqInsDetail, reqLineList, wipLotMap, new BigDecimal(unitQty).negate())
        then:
        result[0].reqQty == 0
        result[2].reqQty == 0
        result[4].reqQty == 0
        result[6].reqQty == 0
    }

}
