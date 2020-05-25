package com.cvte.scm.wip.domain.common.workflow.service;

import com.alibaba.fastjson.JSON;
import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.wfp.api.sdk.constants.DocStatus;
import com.cvte.csb.wfp.api.sdk.constants.OperationType;
import com.cvte.csb.wfp.api.sdk.dto.ApprovalProcExt;
import com.cvte.csb.wfp.api.sdk.dto.CrtFormExt;
import com.cvte.csb.wfp.api.sdk.dto.ProcInfoExt;
import com.cvte.csb.wfp.api.sdk.dto.ProcessParam;
import com.cvte.csb.wfp.api.sdk.service.IWorkflowExtendedService;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.constants.ResponseDefinition;
import com.cvte.scm.wip.common.enums.AccountTypeEnum;
import com.cvte.scm.wip.domain.common.user.entity.UserBaseEntity;
import com.cvte.scm.wip.domain.common.user.service.UserService;
import com.cvte.scm.wip.domain.core.subrule.valueobject.AuditorVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
  *
  * @author  : xueyuting
  * @since    : 2020/2/22 10:37
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Service
public class WorkFlowService {

    private static final String MACHINE_ACCOUNT = "wip_admin";

    /**
     * csb 工作流开放接口地址
     */
    @Value("${csb.wfp.url}")
    private String cspWpfApiUrl;

    private IWorkflowExtendedService workflowExtendedService;
    private UserService userService;

    public WorkFlowService(IWorkflowExtendedService workflowExtendedService, UserService userService) {
        this.workflowExtendedService = workflowExtendedService;
        this.userService = userService;
    }

    public String createWf(String businessId, String formCode, String docSubject, String processNumber) {
        UserBaseEntity enableUserInfo = userService.getUserByAccount(CurrentContext.getCurrentOperatingUser().getAccount());
        String userAccount;
        if (AccountTypeEnum.OUTER.getCode().equals(enableUserInfo.getAccountType())) {
            // 外部账户在OA上没有账号,使用机器节点代替
            userAccount = MACHINE_ACCOUNT;
        } else {
            userAccount = CurrentContext.getCurrentOperatingUser().getAccount();
        }
        //创建工作流
        CrtFormExt crtFormExt = new CrtFormExt();
        crtFormExt.setDocSubject(docSubject + "-" + processNumber) //流程标题：WIP-临时批次替代-XXIX0123456
                .setDocStatus(DocStatus.EXAMINE.getKey()) //10为草稿；20为审批中
                .setHandlerAccount(userAccount) //创建人账户
                .setFormCode(formCode) //表单编码
                .setFormInstanceId(businessId); //业务ID
        return workflowExtendedService.createProcess(crtFormExt);
    }

    public List<AuditorVO> getAuditor(String formInstanceId) {
        validateFormInstanceId(formInstanceId);
        List<AuditorVO> auditorList;
        ProcInfoExt procInfoExt = new ProcInfoExt();
        procInfoExt.setFormInstanceId(formInstanceId);
        try {
            String response = workflowExtendedService.getApproverList(procInfoExt);
            Object data = getRespData(response);
            auditorList = JSON.parseArray(data.toString(), AuditorVO.class);
        } catch (Exception e) {
            log.error("获取下一节点审核人信息失败，异常信息：", e);
            throw new ServerException(ResponseDefinition.SERVER_ERR_CODE, ResponseDefinition.SERVER_ERR_MESSAGE);
        }
        return auditorList;
    }

    public String isExistProcess(String formInstanceId, String formCode) {
        validateFormInstanceId(formInstanceId);
        ProcInfoExt procInfoExt = new ProcInfoExt();
        procInfoExt.setFormInstanceId(formInstanceId);
        procInfoExt.setFormCode(formCode);
        try {
            String response = workflowExtendedService.isExistProcess(procInfoExt);
            Map<String, Object> map = JSON.parseObject(response, Map.class);
            String status = (String)map.get("status");
            if (status.equals("0")) {
                return (String)map.get("data");
            }
            return "false";
        } catch (Exception e) {
            log.error("获取审核流信息失败，异常信息：", e);
            throw new ServerException(ResponseDefinition.SERVER_ERR_CODE, ResponseDefinition.SERVER_ERR_MESSAGE);
        }
    }

    public String getCurrentNodeName(String formInstanceId, String formCode) {
        validateFormInstanceId(formInstanceId);
        ProcInfoExt procInfoExt = new ProcInfoExt();
        procInfoExt.setFormInstanceId(formInstanceId);
        String response = null;
        try {
            response = workflowExtendedService.getCurrentNodesInfo(procInfoExt);
            Object data = getRespData(response);
            List<Map> nodes = JSON.parseArray(data.toString(), Map.class);
            Map<String, String> firstNode = (Map<String, String>)nodes.get(0);
            return firstNode.get("nodeName");
        } catch (Exception e) {
            log.error("获取审核流信息失败，异常信息：", e);
            throw new ServerException(ResponseDefinition.SERVER_ERR_CODE, ResponseDefinition.SERVER_ERR_MESSAGE);
        }
    }

    public String submitDraftNode(String formInstanceId, String formCode, String reason) {
        return approveProcess(formInstanceId, formCode, reason, OperationType.DRAFTER_SUBMIT.getKey());
    }

    public String abandon(String formInstanceId, String formCode, String reason) {
        return approveProcess(formInstanceId, formCode, reason, OperationType.HANDLER_ABANDON.getKey());
    }

    private String approveProcess(String formInstanceId, String formCode, String reason, String operationType) {
        validateFormInstanceId(formInstanceId);
        // 获取但前审核人列表
        List<AuditorVO> auditors = getAuditor(formInstanceId);
        List<String> auditorsAccount = Optional.ofNullable(auditors).orElse(new ArrayList<>()).stream().map(AuditorVO::getLoginName).collect(Collectors.toList());
        if (ListUtil.empty(auditorsAccount)) {
            // 审核流异常:当前审核人为空,直接返回
            log.info("Id为【{}】的审核流当前审核人为空,操作失败", formInstanceId);
            return null;
        }
        String handleAccount = CurrentContext.getCurrentOperatingUser().getAccount();
        if (!auditorsAccount.contains(handleAccount)) {
            // 如果操作人不是当前审核人, 则强行改为当前审核人, 确保操作可以进行
            handleAccount = auditorsAccount.get(0);
        }
        // 在意见中注名实际操作人
        reason = String.format("WIP实际操作人【%s】【%s】", CurrentContext.getCurrentOperatingUser().getName(), reason);
        ApprovalProcExt approvalProcExt = new ApprovalProcExt();
        approvalProcExt.setFormInstanceId(formInstanceId);
        approvalProcExt.setFormCode(formCode);
        approvalProcExt.setHandlerAccount(handleAccount);
        ProcessParam processParam = new ProcessParam();
        processParam.setOperationType(operationType);
        processParam.setAuditNote(reason);
        approvalProcExt.setProcessParam(processParam);
        return workflowExtendedService.approveProcess(approvalProcExt);

    }

    private void validateFormInstanceId(String formInstanceId) {
        if (StringUtils.isBlank(formInstanceId)) {
            throw new ParamsRequiredException("单据id不能为空");
        }
    }

    private Object getRespData(String response) {
        log.info(response);
        Map<String, Object> map = JSON.parseObject(response, Map.class);
        if (!"0".equals(map.get("status"))) {
            log.error("请求接口成功，但返回状态码不为0，返回值：" + response);
            throw new RuntimeException("请求接口成功，但返回状态码不为0，返回值：" + response);
        }
        return map.get("data");
    }
}
