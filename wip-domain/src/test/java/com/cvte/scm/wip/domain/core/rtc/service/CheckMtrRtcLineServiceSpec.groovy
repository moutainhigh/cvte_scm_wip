package com.cvte.scm.wip.domain.core.rtc.service

import com.cvte.scm.wip.domain.BaseJunitTest
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcAssignEntity
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcLineBuildVO
import org.springframework.beans.factory.annotation.Autowired

class CheckMtrRtcLineServiceSpec extends BaseJunitTest {

    @Autowired
    private CheckMtrRtcLineService checkMtrRtcLineService

    void "checkChangeable"() {
        given:
        def numList = [new BigDecimal("0"), new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("3")]
        def rtcLineBuildVO = new WipMtrRtcLineBuildVO()
        def rtcLine = WipMtrRtcLineEntity.get()
        rtcLine.setReqQty(numList[2]).setIssuedQty(numList[2])
        rtcLine.setAssignList([WipMtrRtcAssignEntity.get()])
        when: "数量改多"
        rtcLineBuildVO.setReqQty(numList[3]).setIssuedQty(numList[3])
        checkMtrRtcLineService.checkChangeable(rtcLineBuildVO, rtcLine)
        then: "OK"
        notThrown()
        when: "数量没改"
        rtcLineBuildVO.setReqQty(numList[2]).setIssuedQty(numList[2])
        checkMtrRtcLineService.checkChangeable(rtcLineBuildVO, rtcLine)
        then: "OK"
        notThrown()
        when: "需求数量改少"
        rtcLineBuildVO.setReqQty(numList[1]).setIssuedQty(numList[2])
        checkMtrRtcLineService.checkChangeable(rtcLineBuildVO, rtcLine)
        then: "报错"
        Exception e = thrown()
        e.getMessage() == "分配了批次后只能增加数量"
        when: "实发数量改少"
        rtcLineBuildVO.setReqQty(numList[2]).setIssuedQty(numList[1])
        checkMtrRtcLineService.checkChangeable(rtcLineBuildVO, rtcLine)
        then: "报错"
        e = thrown()
        e.getMessage() == "分配了批次后只能增加数量"
    }

}
