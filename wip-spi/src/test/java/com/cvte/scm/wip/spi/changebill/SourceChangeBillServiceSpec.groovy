package com.cvte.scm.wip.spi.changebill

import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillDetailBuildVO
import spock.lang.Specification

import java.math.RoundingMode

class SourceChangeBillServiceSpec extends Specification {

    private String splitter = ","

    private SourceChangeBillServiceImpl sourceChangeBillService

    void setup() {
        sourceChangeBillService = new SourceChangeBillServiceImpl(null, null, null)
    }

    void "空或一个位号"() {
        given:
        def emptyPosNo = new ChangeBillDetailBuildVO(itemQty: BigDecimal.ONE, itemUnitQty: BigDecimal.ONE)
        def onePosNo = new ChangeBillDetailBuildVO(posNo: "one", itemQty: BigDecimal.TEN, itemUnitQty: BigDecimal.ONE)
        when:
        def emptyList = sourceChangeBillService.splitBillDetailByPos(emptyPosNo, splitter)
        def oneList = sourceChangeBillService.splitBillDetailByPos(onePosNo, splitter)
        then: "位号空返回"
        emptyList.size() == 1
        def empty = emptyList.get(0)
        empty.posNo == null && empty.itemQty == BigDecimal.ONE && empty.itemUnitQty == BigDecimal.ONE
        and: "一个位号返回"
        oneList.size() == 1
        def one = oneList.get(0)
        one.posNo == "one" && one.itemQty == BigDecimal.TEN && one.itemUnitQty == BigDecimal.ONE
    }

    void "单位用量能被位号数量整除"() {
        given: "两个位号, 用量为2, 单位用量为2"
        def multiPosNo = new ChangeBillDetailBuildVO(posNo: " one , two ", itemQty: new BigDecimal(2), itemUnitQty: new BigDecimal("2"))
        when:
        def multiList = sourceChangeBillService.splitBillDetailByPos(multiPosNo, splitter)
        then: "每个位号用量1, 单位用量1"
        multiList.size() == 2
        def one = multiList.get(0)
        def two = multiList.get(1)
        "one" == one.posNo && one.itemQty == BigDecimal.ONE && one.itemUnitQty == BigDecimal.ONE
        "two" == two.posNo && two.itemQty == BigDecimal.ONE && two.itemUnitQty == BigDecimal.ONE
    }

    void "单位用量不能被位号整除"() {
        def unitQtyStr = "1.333333333333"
        def posNoList = ["RB818", "RB819", "RB820"]
        given: "三个位号, 用量为4, 单位用量为" + unitQtyStr
        def multiPosNo = new ChangeBillDetailBuildVO(posNo: " RB818 , RB819 , RB820 ", itemQty: new BigDecimal(4), itemUnitQty: new BigDecimal(unitQtyStr))
        when:
        def multiList = sourceChangeBillService.splitBillDetailByPos(multiPosNo, splitter)
        then: "位号1用量1, 位号2用量1, 位号3用量2"
        def one = multiList.get(0)
        def two = multiList.get(1)
        def three = multiList.get(2)
        posNoList[0] == one.posNo && one.itemQty == new BigDecimal(1) && one.itemUnitQty == new BigDecimal("0.44444444")
        posNoList[1] == two.posNo && two.itemQty == new BigDecimal(1) && two.itemUnitQty == new BigDecimal("0.44444444")
        posNoList[2] == three.posNo && three.itemQty == new BigDecimal(2) && three.itemUnitQty == new BigDecimal("0.44444446")
    }

    def  "单位用量是无理数"() {
        def unitQtyStr = "0.9900990099009900990099009900990099009901"
        def posNoList = ["RB818", "RB819", "RB820"]
        given: "三个位号, 用量为3000, 单位用量为" + unitQtyStr
        def multiPosNo = new ChangeBillDetailBuildVO(posNo: "RB818,RB819,RB820", itemQty: new BigDecimal(3000), itemUnitQty: new BigDecimal(unitQtyStr))
        when:
        def multiList = sourceChangeBillService.splitBillDetailByPos(multiPosNo, splitter)
        then: "单位用量按顺序分配"
        multiList.size() == 3
        def one = multiList[0]
        def two = multiList[1]
        def three = multiList[2]
        posNoList[0] == one.posNo && one.itemQty == new BigDecimal(1000) && one.itemUnitQty == new BigDecimal("0.33003300")
        posNoList[1] == two.posNo && two.itemQty == new BigDecimal(1000) && two.itemUnitQty == new BigDecimal("0.33003300")
        posNoList[2] == three.posNo && three.itemQty == new BigDecimal(1000) && three.itemUnitQty == new BigDecimal("0.33003301")
    }

    def  "单位用量是无理数,且只有一个位号"() {
        def unitQtyStr = "0.9900990099009900990099009900990099009901"
        def posNoList = ["RB818"]
        given: "一个位号, 用量为1000.00001, 单位用量为" + unitQtyStr
        def onePosNo = new ChangeBillDetailBuildVO(posNo: "RB818", itemQty: new BigDecimal(1000.00001), itemUnitQty: new BigDecimal(unitQtyStr))
        when:
        def oneResult = sourceChangeBillService.splitBillDetailByPos(onePosNo, splitter)
        then: "单位用量截断"
        def one = oneResult.get(0)
        posNoList[0] == one.posNo && one.itemQty == new BigDecimal(1000) && one.itemUnitQty == new BigDecimal("0.99009901")
    }

    def "一个位号,用量为空"() {
        def unitQtyStr = "1"
        def posNoList = ["RB818"]
        given: "一个位号, 用量为空, 单位用量为" + unitQtyStr
        def onePosNo = new ChangeBillDetailBuildVO(posNo: "RB818", itemUnitQty: new BigDecimal(unitQtyStr))
        when:
        def oneResult = sourceChangeBillService.splitBillDetailByPos(onePosNo, splitter)
        then: "用量为空"
        def one = oneResult[0]
        posNoList[0] == one.posNo && one.itemQty == null && one.itemUnitQty == new BigDecimal(unitQtyStr)
    }

    def "多个位号,用量为空"() {
        def unitQtyStr = "3"
        def posNoList = ["RB818", "RB819", "RB820"]
        given: "三个位号, 用量为空, 单位用量为" + unitQtyStr
        def onePosNo = new ChangeBillDetailBuildVO(posNo: "RB818, RB819, RB820", itemUnitQty: new BigDecimal(unitQtyStr))
        when:
        def multiList = sourceChangeBillService.splitBillDetailByPos(onePosNo, splitter)
        then: "用量为空"
        def one = multiList[0]
        def two = multiList[1]
        def three = multiList[2]
        posNoList[0] == one.posNo && one.itemQty == null && one.itemUnitQty == new BigDecimal("1")
        posNoList[1] == two.posNo && two.itemQty == null && two.itemUnitQty == new BigDecimal("1")
        posNoList[2] == three.posNo && three.itemQty == null && three.itemUnitQty == new BigDecimal("1")
    }

    def "Non-breaking space"() {
        def unitQtyStr = "2"
        def posNoList = ["DIS_1", "DIS_2"]
        given: "二个位号, 用量为空, 单位用量为" + unitQtyStr
        def onePosNo = new ChangeBillDetailBuildVO(posNo: "DIS_1,DIS_2 ", itemUnitQty: new BigDecimal(unitQtyStr))
        when:
        def multiList = sourceChangeBillService.splitBillDetailByPos(onePosNo, splitter)
        then: "用量为空"
        def one = multiList[0]
        def two = multiList[1]
        posNoList[0] == one.posNo && one.itemQty == null && one.itemUnitQty == new BigDecimal("1")
        posNoList[1] == two.posNo && two.itemQty == null && two.itemUnitQty == new BigDecimal("1")
    }

}
