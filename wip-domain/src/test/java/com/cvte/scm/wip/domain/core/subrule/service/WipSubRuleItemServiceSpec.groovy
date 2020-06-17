package com.cvte.scm.wip.domain.core.subrule.service

import com.cvte.scm.wip.domain.core.item.repository.ScmItemRepository
import com.cvte.scm.wip.domain.core.item.service.ScmItemService
import com.cvte.scm.wip.domain.core.subrule.repository.WipSubRuleItemRepository
import com.cvte.scm.wip.domain.core.subrule.valueobject.SubItemValidateVO
import com.cvte.scm.wip.domain.core.subrule.valueobject.enums.SubRuleAdaptTypeEnum
import com.cvte.scm.wip.domain.core.subrule.valueobject.enums.SubRuleScopeTypeEnum
import spock.lang.Specification

class WipSubRuleItemServiceSpec extends Specification {

    private WipSubRuleItemRepository wipSubRuleItemRepository
    private WipSubRuleItemService wipSubRuleItemService;

    def setup() {
        wipSubRuleItemRepository = Mock(WipSubRuleItemRepository.class)
        wipSubRuleItemService = new WipSubRuleItemService(null, wipSubRuleItemRepository)
    }

    def "物料不在批次对应的投料单中"() {
        given:
        def lotNoList = ["GC-SRCBU190805504", "GC-SRCBUBS190806274"]
        def beforeItemNoList = ["003.001.0000454", "003.001.0000562"]
        def validItemList = [
                new SubItemValidateVO(adaptItem: lotNoList[0], beforeItemNo: beforeItemNoList[0]),
                new SubItemValidateVO(adaptItem: lotNoList[1], beforeItemNo: beforeItemNoList[0]),
        ]
        wipSubRuleItemRepository.getItemsInReq(_ as List, _ as List, _ as String) >> validItemList
        when:
        def subItemNosList = [beforeItemNoList[0].split(), beforeItemNoList[1].split()]
        def scopeMap = [
                (SubRuleScopeTypeEnum.PRODUCTION_LOT.code):[
                        (SubRuleAdaptTypeEnum.EQUAL.code):lotNoList.join(";")
                ]
        ]
        String errMsg = wipSubRuleItemService.validateItemReq(subItemNosList, scopeMap, "514")
        subItemNosList = [beforeItemNoList[0].split()]
        String sucMsg = wipSubRuleItemService.validateItemReq(subItemNosList, scopeMap, "514")
        then:
        errMsg == "【GC-SRCBU190805504批次中不存在物料003.001.0000562】【GC-SRCBUBS190806274批次中不存在物料003.001.0000562】"
        sucMsg == ""
    }

}
