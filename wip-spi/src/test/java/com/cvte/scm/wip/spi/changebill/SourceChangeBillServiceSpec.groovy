package com.cvte.scm.wip.spi.changebill

import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillDetailBuildVO
import spock.lang.Specification

import java.math.RoundingMode

class SourceChangeBillServiceSpec extends Specification {

    private String splitter = ",";

    private SourceChangeBillServiceImpl sourceChangeBillService;

    void setup() {
        sourceChangeBillService = new SourceChangeBillServiceImpl(null, null, null);
    }

    void "空或一个位号"() {
        given:
        ChangeBillDetailBuildVO emptyPosNo = new ChangeBillDetailBuildVO(itemQty: BigDecimal.ONE, itemUnitQty: BigDecimal.ONE);
        ChangeBillDetailBuildVO onePosNo = new ChangeBillDetailBuildVO(posNo: "one", itemQty: BigDecimal.TEN, itemUnitQty: BigDecimal.ONE);
        when:
        List<ChangeBillDetailBuildVO> emptyList = sourceChangeBillService.splitBillDetailByPos(emptyPosNo, splitter);
        List<ChangeBillDetailBuildVO> oneList = sourceChangeBillService.splitBillDetailByPos(onePosNo, splitter);
        then: "位号空返回"
        emptyList.size() == 1
        ChangeBillDetailBuildVO empty = emptyList.get(0);
        empty.posNo == null && empty.itemQty == BigDecimal.ONE && empty.itemUnitQty == BigDecimal.ONE
        and: "一个位号返回"
        oneList.size() == 1
        ChangeBillDetailBuildVO one = oneList.get(0);
        one.posNo == "one" && one.itemQty == BigDecimal.TEN && one.itemUnitQty == BigDecimal.ONE
    }

    void "单位用量能被位号数量整除"() {
        given: "两个位号, 用量为2, 单位用量为2"
        ChangeBillDetailBuildVO multiPosNo = new ChangeBillDetailBuildVO(posNo: " one , two ", itemQty: new BigDecimal(2), itemUnitQty: new BigDecimal("2"));
        when:
        List<ChangeBillDetailBuildVO> multiList = sourceChangeBillService.splitBillDetailByPos(multiPosNo, splitter);
        then: "每个位号用量1, 单位用量1"
        multiList.size() == 2
        ChangeBillDetailBuildVO one = multiList.get(0)
        ChangeBillDetailBuildVO two = multiList.get(1)
        "one" == one.posNo && one.itemQty == BigDecimal.ONE && one.itemUnitQty == BigDecimal.ONE
        "two" == two.posNo && two.itemQty == BigDecimal.ONE && two.itemUnitQty == BigDecimal.ONE
    }

    void "单位用量不能被位号整除"() {
        given: "两个位号, 用量为15, 单位用量为1.1"
        ChangeBillDetailBuildVO multiPosNo = new ChangeBillDetailBuildVO(posNo: " one , two ", itemQty: new BigDecimal(15), itemUnitQty: new BigDecimal("1.1"));
        BigDecimal unitQty = new BigDecimal("1.1").divide(new BigDecimal(2), 8, RoundingMode.DOWN)
        when:
        List<ChangeBillDetailBuildVO> multiList = sourceChangeBillService.splitBillDetailByPos(multiPosNo, splitter);
        then: "位号1用量8, 单位用量0.55; 位号2用量7, 单位用量0.55"
        multiList.size() == 2
        ChangeBillDetailBuildVO one = multiList.get(0)
        ChangeBillDetailBuildVO two = multiList.get(1)
        "one" == one.posNo && one.itemQty == new BigDecimal(8) && one.itemUnitQty == unitQty
        "two" == two.posNo && two.itemQty == new BigDecimal(7) && two.itemUnitQty == unitQty
    }

}
