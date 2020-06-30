package com.cvte.scm.wip.domain.core.requirement.entity

import com.cvte.scm.wip.domain.BaseJunitTest
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInsDetailRepository
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsBuildVO
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsDetailBuildVO
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired

class ReqInsDetailEntitySyncTest extends BaseJunitTest {

    @Autowired
    private ReqInsDetailRepository detailRepository

    def "工厂确认更新单据"() {
        given:
        def insHeaderId = "2f69e6b4d90542ffa3d4c98bd1f918c5"
        def changeDetailIdList = [
                "817b2af34a704bc681beeff2ac70e8b7", " 4093f64fd8e2411c850789ac38673b25", "c9ea6d8851c842d38c3af637070c27fb"
        ]
        def insDetailIdList = [
                "3a0265d8102c44cba436292359eb719d", "bc43d5279f9340c6814e3af26cf380a9", "d07321f16fbb4bf899d8d56899cf44ea"
        ]
        def insBuildVo = new ReqInsBuildVO(
                insHeaderId: insHeaderId,
                detailList: [
                        new ReqInsDetailBuildVO(sourceChangeDetailId: changeDetailIdList[0]),
                        new ReqInsDetailBuildVO(sourceChangeDetailId: changeDetailIdList[1]),
                        new ReqInsDetailBuildVO(sourceChangeDetailId: changeDetailIdList[2]),
                ]
        )
        def billDetailEntityList = [
                ReqInsDetailEntity.get().setSourceDetailId(changeDetailIdList[0]).setInsDetailId(insDetailIdList[0]),
                ReqInsDetailEntity.get().setSourceDetailId(changeDetailIdList[1]).setInsDetailId(insDetailIdList[1]),
                ReqInsDetailEntity.get().setSourceDetailId(changeDetailIdList[2]).setInsDetailId(insDetailIdList[2]),
        ]
        Mockito.when(detailRepository.getByInsId(insHeaderId)).thenReturn(billDetailEntityList)
        when:
        def resultList = ReqInsDetailEntity.get().batchSaveInsDetail(insBuildVo, true)
        then:
        resultList.size() == 3
        resultList[0].insDetailId == insDetailIdList[0] && resultList[1].insDetailId == insDetailIdList[1] && resultList[2].insDetailId == insDetailIdList[2]
    }

}
