package com.cvte.scm.wip.domain.core.requirement.service

import com.cvte.csb.toolkit.StringUtils
import com.cvte.scm.wip.domain.BaseJunitTest
import org.springframework.beans.factory.annotation.Autowired

import java.util.regex.Matcher
import java.util.regex.Pattern

class WipReqLineServiceSpec extends BaseJunitTest {

    @Autowired
    private WipReqLineService wipReqLineService

    void "test truncate procedure error msg1"() {
        expect:
        wipReqLineService.truncProcedureErrMsg(errorMsg as String) == result
        where:
        errorMsg | result
        null | null
        "not matched" | "not matched"
        "失败...; ORA-20001: 存在未过账的领料单，请过账或取消后重试！ ORA-06512: 在...: ORA-20001: 存在未过账的领料单，请过账或取消后重试！ ORA-06512: 在...191" | "存在未过账的领料单，请过账或取消后重试！"
    }

}
