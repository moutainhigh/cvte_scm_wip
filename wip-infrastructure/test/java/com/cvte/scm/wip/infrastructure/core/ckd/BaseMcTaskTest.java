package com.cvte.scm.wip.infrastructure.core.ckd;

import com.cvte.csb.base.commons.OperatingUser;
import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.utils.CurrentContextUtils;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcTaskEntity;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcTaskLineEntity;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcWfEntity;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskLineStatusEnum;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskStatusEnum;
import com.cvte.scm.wip.domain.core.ckd.service.WipMcTaskLineService;
import com.cvte.scm.wip.domain.core.ckd.service.WipMcTaskService;
import com.cvte.scm.wip.domain.core.ckd.service.WipMcTaskVersionService;
import com.cvte.scm.wip.domain.core.ckd.service.WipMcWfService;
import com.cvte.scm.wip.domain.core.scm.service.ScmViewCommonService;
import com.cvte.scm.wip.infrastructure.BaseJunitTest;
import org.junit.Before;
import org.junit.Ignore;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author zy
 * @date 2020-05-08 12:11
 **/
@Ignore
@Transactional("pgTransactionManager")
public class BaseMcTaskTest extends BaseJunitTest {

    @Autowired
    protected WipMcWfService wipMcWfService;

    @Autowired
    protected ScmViewCommonService scmViewCommonService;

    @Autowired
    @InjectMocks
    protected WipMcTaskService wipMcTaskService;

    @Autowired
    protected WipMcTaskLineService wipMcTaskLineService;

    @Autowired
    protected WipMcTaskVersionService wipMcTaskVersionService;

    protected String mcTaskId = UUIDUtils.getUUID();

    protected String mcTaskNo = UUIDUtils.getUUID();


    protected String factoryId = "9f8e58be54bc574ce0536426150a4549";

    protected static List<String> lineIds = Arrays.asList(UUIDUtils.get32UUID(), UUIDUtils.get32UUID());

    @Before
    public void initTask() {
        initCurrentUser();

        WipMcWfEntity wipMcWf = new WipMcWfEntity();
        wipMcWf.setWfId(UUIDUtils.getUUID())
                .setMcTaskId(mcTaskId)
                .setStatus(getTaskStatus());
        EntityUtils.writeStdCrtInfoToEntity(wipMcWf, CurrentContextUtils.getOrEmptyOperatingUser().getId());
        wipMcWfService.insertSelective(wipMcWf);


        WipMcTaskEntity wipMcTask = new WipMcTaskEntity();
        wipMcTask.setMcTaskId(mcTaskId)
                .setMcTaskNo(mcTaskNo)
                .setMcWfId(wipMcWf.getWfId())
                .setMtrReadyTime(new Date())
                .setFactoryId(factoryId)
                .setClient("客户")
                .setOrgId("e34d85e4f9d04b229529176044d96d41");  // SR
        EntityUtils.writeStdCrtInfoToEntity(wipMcTask, CurrentContextUtils.getOrDefaultUserId("unit-test"));
        wipMcTaskService.insertSelective(wipMcTask);

        wipMcTaskLineService.insertList(buildInitLineList(mcTaskId, lineIds));

        wipMcTaskVersionService.add(wipMcTask.getMcTaskId());
    }

    protected List<WipMcTaskLineEntity> buildInitLineList(String mcTaskId, List<String> lineIds) {
        List<WipMcTaskLineEntity> lineEntities = new ArrayList<>();
        for (String lineId : lineIds) {
            WipMcTaskLineEntity entity = new WipMcTaskLineEntity();
            entity.setMcTaskId(mcTaskId)
                    .setLineId(lineId)
                    .setLineStatus(McTaskLineStatusEnum.NORMAL.getCode())
                    .setItemId("595202")
                    .setSourceLineId(UUIDUtils.get32UUID())
                    .setSourceLineNo(UUIDUtils.get32UUID())
                    .setMcQty(10);
            EntityUtils.writeCurUserStdCrtInfoToEntity(entity);
            lineEntities.add(entity);
        }
        return lineEntities;
    }

    protected String getTaskStatus() {

        return McTaskStatusEnum.CREATE.getCode();
    }


    public void initCurrentUser() {
        OperatingUser operatingUser = new OperatingUser();
        operatingUser.setId("561e8797c90f44e787e95654ff5bd614");
        operatingUser.setAccount("zhongyuan");
        operatingUser.setName("钟源");
        CurrentContext.setCurrentOperatingUser(operatingUser);
    }

}

