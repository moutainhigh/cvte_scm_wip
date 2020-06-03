package com.cvte.scm.wip.controller.change.job;

import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.app.changebill.parse.ChangeBillParseApplication;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillQueryVO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/3 08:52
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Service
@JobHander(value = "syncChangeBillJob")
public class SyncChangeBillJobHandler extends IJobHandler {

    private static final String ORGANIZATION_ID = "organizationId";
    private static final String MINUTES_BEFORE = "minutesBefore";

    private ChangeBillParseApplication changeBillParseApplication;

    public SyncChangeBillJobHandler(ChangeBillParseApplication changeBillParseApplication) {
        this.changeBillParseApplication = changeBillParseApplication;
    }

    @Override
    public ReturnT<String> execute(Map<String, Object> map) throws Exception {
        ChangeBillQueryVO changeBillQueryVO = new ChangeBillQueryVO();
        ReturnT<String> returnT = ReturnT.SUCCESS;
        String organizationId = (String)map.get(ORGANIZATION_ID);
        if (StringUtils.isBlank(organizationId)) {
            returnT = ReturnT.FAIL;
            returnT.setMsg("组织ID不可为空");
            return returnT;
        }
        changeBillQueryVO.setOrganizationId(organizationId);

        Integer minutesBefore = (Integer)map.get(MINUTES_BEFORE);
        if (Objects.nonNull(minutesBefore)) {
            LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(minutesBefore);
            Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            changeBillQueryVO.setLastUpdDate(date);
        }

        String billNos = changeBillParseApplication.doAction(changeBillQueryVO);
        if (StringUtils.isNotBlank(billNos)) {
            int size = billNos.split(",").length;
            returnT.setMsg(String.format("%s个更改单同步成功,billNo=%s", size, billNos));
        } else {
            returnT.setMsg("更改单无变更");
        }
        return returnT;
    }

}
