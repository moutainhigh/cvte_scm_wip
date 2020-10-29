package com.cvte.scm.wip.controller.requirement.job;

import com.cvte.csb.base.commons.OperatingUser;
import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.app.req.ins.confirm.ReqInsConfirmApplication;
import com.cvte.scm.wip.common.constants.CommonUserConstant;
import com.cvte.scm.wip.common.utils.CurrentContextUtils;
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInsRepository;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/27 09:48
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
@JobHander(value = "reqInsAutoConfirm")
public class ReqInsAutoConfirmJobHandler extends IJobHandler {

    private ReqInsRepository reqInsRepository;
    private ReqInsConfirmApplication reqInsConfirmApplication;

    public ReqInsAutoConfirmJobHandler(ReqInsRepository reqInsRepository, ReqInsConfirmApplication reqInsConfirmApplication) {
        this.reqInsRepository = reqInsRepository;
        this.reqInsConfirmApplication = reqInsConfirmApplication;
    }

    @Override
    public ReturnT<String> execute(Map<String, Object> map) {
        ReturnT<String> returnT = new ReturnT<>(null);

        String organizationIds = (String)map.get("organizationIds");
        String factoryIds = (String)map.get("factoryIds");
        String billTypes = (String)map.get("billTypes");
        List<String> organizationIdList = null;
        List<String> factoryIdList = null;
        List<String> billTypeList = null;
        if (StringUtils.isNotBlank(organizationIds)) {
            organizationIdList = Arrays.asList(organizationIds.split(","));
        }
        if (StringUtils.isNotBlank(factoryIds)) {
            factoryIdList = Arrays.asList(organizationIds.split(","));
        }
        if (StringUtils.isNotBlank(billTypes)) {
            billTypeList = Arrays.asList(billTypes.split(","));
        }
        List<String> insHeaderIdList = reqInsRepository.getAutoConfirm(organizationIdList, factoryIdList, billTypeList);

        String msg;
        if (ListUtil.empty(insHeaderIdList)) {
            msg = "暂无需自动执行的更改单";
        } else {
            // 原本自动填充的更改用户SCM-WIP会被WIP->EBS同步的存储过程过滤掉, 所以需要其他的自动设置用户
            OperatingUser operatingUser = CurrentContextUtils.createOperatingUser(CommonUserConstant.AUTORUN);
            CurrentContext.setCurrentOperatingUser(operatingUser);
            msg = reqInsConfirmApplication.doAction(insHeaderIdList.toArray(new String[0]));
            CurrentContext.destroy();
        }

        returnT.setMsg(msg);
        return returnT;
    }

}
