package com.cvte.scm.wip.domain.core.changebill.entity

import com.cvte.scm.wip.domain.TestStarter
import com.cvte.scm.wip.domain.core.changebill.repository.ChangeBillDetailRepository
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = TestStarter.class)
class ChangeBillDetailEntitySpec extends Specification {

    @Autowired
    private ChangeBillDetailRepository changeBillDetailRepository;

    def setup() {
        def mock1 = [ChangeBillDetailEntity.get().setDetailId("test").setPosNo("no1")]
        Mockito.when(changeBillDetailRepository.selectByBillId(Mockito.anyObject())).thenReturn(mock1)
    }

    def "testGetByBillId"() {
        when:
        def list = ChangeBillDetailEntity.get().getByBillId("1");
        then:
        list[0].detailId == "test" && list[0].posNo == 'no1'
    }

}
