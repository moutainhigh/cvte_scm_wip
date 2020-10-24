package com.cvte.scm.wip.domain.core.rtc.service

import com.cvte.csb.base.commons.OperatingUser
import com.cvte.csb.base.context.CurrentContext
import com.cvte.scm.wip.common.constants.CommonDimensionConstant
import com.cvte.scm.wip.domain.BaseJunitTest
import com.cvte.scm.wip.domain.common.user.service.UserService
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcPostLimitEntity
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcPostLimitRepository
import org.springframework.beans.factory.annotation.Autowired

import static org.mockito.Mockito.*

class CheckMtrRtcHeaderServiceSpec extends BaseJunitTest {

    @Autowired
    private CheckMtrRtcHeaderService checkMtrRtcHeaderService

    @Autowired
    private UserService userService

    @Autowired
    private WipMtrRtcPostLimitRepository wipMtrRtcPostLimitRepository

    def setup() {
        def user = new OperatingUser()
        user.setId("xueyuting")
        CurrentContext.setCurrentOperatingUser(user)
    }

    def "inner user"() {
        given:
        when(userService.getDefaultDimensionByUserId(anyObject())).thenReturn(CommonDimensionConstant.SCM)
        when:
        checkMtrRtcHeaderService.checkPostLimit(WipMtrRtcHeaderEntity.get())
        then:
        notThrown()
    }

    def "factory not configured"() {
        given:
        when(userService.getDefaultDimensionByUserId(anyObject())).thenReturn(CommonDimensionConstant.GC)
        when(wipMtrRtcPostLimitRepository.selectOne(anyObject())).thenReturn(null)
        def rtcHeader = WipMtrRtcHeaderEntity.get()
        rtcHeader.setFactoryId("321")
        when:
        checkMtrRtcHeaderService.checkPostLimit(rtcHeader)
        then:
        notThrown()
    }

    def "factory configured but not in limit time"() {
        given:
        when(userService.getDefaultDimensionByUserId(anyObject())).thenReturn(CommonDimensionConstant.GC)
        when(wipMtrRtcPostLimitRepository.selectOne(anyObject())).thenReturn(new WipMtrRtcPostLimitEntity(factoryId: "123", status: "110", limitDay: 1, limitHour: 18))
        def rtcHeader = WipMtrRtcHeaderEntity.get()
        rtcHeader.setFactoryId("123")
        when:
        checkMtrRtcHeaderService.checkPostLimit(rtcHeader)
        then:
        notThrown()
    }

    def "factory configured and in limit time"() {
        given:
        when(userService.getDefaultDimensionByUserId(anyObject())).thenReturn(CommonDimensionConstant.GC)
        when(wipMtrRtcPostLimitRepository.selectOne(anyObject())).thenReturn(new WipMtrRtcPostLimitEntity(factoryId: "123", status: "110", limitDay: 30, limitHour: 18))
        def rtcHeader = WipMtrRtcHeaderEntity.get()
        rtcHeader.setFactoryId("123")
        when:
        checkMtrRtcHeaderService.checkPostLimit(rtcHeader)
        then:
        Exception e = thrown()
        e.getMessage() == String.format("在月末最后%d天的%d点之后，不允许工厂做过账操作", 30, 18)
    }

}
