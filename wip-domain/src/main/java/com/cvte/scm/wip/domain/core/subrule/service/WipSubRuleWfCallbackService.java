package com.cvte.scm.wip.domain.core.subrule.service;

import com.alibaba.fastjson.JSON;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.dto.WfDoMethodEKP;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.enums.AuditorNodeEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.common.workflow.service.WorkFlowService;
import com.cvte.scm.wip.domain.core.subrule.entity.WipSubRuleEntity;
import com.cvte.scm.wip.domain.core.subrule.entity.WipSubRuleWfEntity;
import com.cvte.scm.wip.domain.core.subrule.repository.WipSubRuleRepository;
import com.cvte.scm.wip.domain.core.subrule.repository.WipSubRuleWfRepository;
import com.cvte.scm.wip.domain.core.subrule.valueobject.enums.SubRuleStatusEnum;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 21:19
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Service
public class WipSubRuleWfCallbackService {

    private static final String FORM_CODE = "wipSubRule";

    private static final String DOC_SUBJECT = "WIP-临时批次替代";


    private WorkFlowService workFlowService;
    private WipSubRuleWfJob wipSubRuleWfJob;
    private WipSubRuleWfRepository wipSubRuleWfRepository;
    private WipSubRuleRepository wipSubRuleRepository;

    public WipSubRuleWfCallbackService(WorkFlowService workFlowService, WipSubRuleWfJob wipSubRuleWfJob, WipSubRuleWfRepository wipSubRuleWfRepository, WipSubRuleRepository wipSubRuleRepository) {
        this.workFlowService = workFlowService;
        this.wipSubRuleWfJob = wipSubRuleWfJob;
        this.wipSubRuleWfRepository = wipSubRuleWfRepository;
        this.wipSubRuleRepository = wipSubRuleRepository;
    }

    public String createSubRuleWorkFlow(String ruleId) {
        WipSubRuleEntity querySubRule = new WipSubRuleEntity().setRuleId(ruleId).setRuleStatus(null);
        List<WipSubRuleEntity> subRuleList = wipSubRuleRepository.selectList(querySubRule);
        if (ListUtil.empty(subRuleList)) {
            log.info("该临时替代规则不存在或已失效, ruleId = {}", ruleId);
            throw new ParamsIncorrectException("该临时替代规则不存在或已失效!");
        }
        if (subRuleList.size() > 1) {
            log.info("替代规则唯一标识重复, ruleId = {}", ruleId);
            throw new ParamsIncorrectException("替代规则唯一标识重复!");
        }
        String flowId = null;
        String isExistProcess = workFlowService.isExistProcess(ruleId, FORM_CODE);
        if (!"false".equals(isExistProcess)) {
            // 若驳回后重新提交，则工作流已存在，使用已有工作流
            flowId = isExistProcess;
            String nodeName = workFlowService.getCurrentNodeName(ruleId, FORM_CODE);
            if (AuditorNodeEnum.DRAFT_NODE.getDesc().equals(nodeName)) {
                workFlowService.submitDraftNode(ruleId, FORM_CODE, "提交");
            }
        } else {
            // 工作流不存在，则新建
            WipSubRuleEntity subRule = subRuleList.get(0);
            // 创建工作流
            String response = workFlowService.createWf(ruleId, FORM_CODE, DOC_SUBJECT, subRule.getRuleNo());
            Map restResponse = JSON.parseObject(response, Map.class);
            if (!"0".equals(restResponse.get("status"))) {
                log.warn("发起流程失败,失败原因:{},返回参数:{}", restResponse.get("message"), restResponse);
                throw new ParamsIncorrectException("发起流程失败,失败原因:" + restResponse.get("message"));
            }
            flowId = (String) restResponse.get("data");
        }
        // 更新临时代用单的状态为审核中, 保存processId
        WipSubRuleEntity updateSubRule = new WipSubRuleEntity().setRuleId(ruleId)
                .setProcessId(flowId)
                .setRuleStatus(SubRuleStatusEnum.REVIEW.getCode());
        wipSubRuleRepository.update(updateSubRule);
        return flowId;
    }

    public Map<String, String> getAuditors(String formInstanceId, List<String> fields) {
        Map<String, String> fieldsMap = null;
        if (StringUtils.isNotBlank(formInstanceId)) {
            List<WipSubRuleWfEntity> subRuleWfList = wipSubRuleWfRepository.selectByRuleId(formInstanceId);
            if (ListUtil.notEmpty(subRuleWfList)) {
                fieldsMap = Maps.newHashMap();
                Map<String, List<WipSubRuleWfEntity>> groupFields = subRuleWfList.stream().collect(groupingBy(WipSubRuleWfEntity::getNode));
                for (String field : fields) {
                    List<WipSubRuleWfEntity> wipSubRuleWfList = groupFields.get(field);
                    if (ListUtil.notEmpty(wipSubRuleWfList)) {
                        String accounts = wipSubRuleWfList.stream().map(WipSubRuleWfEntity::getUserId).collect(joining(","));
                        fieldsMap.put(field, accounts);
                    }
                }
            }
        }
        return fieldsMap;
    }

    public RestResponse updateAuditor(WfDoMethodEKP ekp) {
        wipSubRuleWfJob.asyncUpdate(ekp.getFormInstanceId());
        return new RestResponse();
    }

    public RestResponse pass(WfDoMethodEKP ekp) {
        WipSubRuleEntity subRule = wipSubRuleRepository.selectByRuleIdAndStatus(ekp.getFormInstanceId(), SubRuleStatusEnum.REVIEW.getCode());
        Integer result = updateStatusByWorkFlow(subRule, ekp.getFormInstanceId(), SubRuleStatusEnum.EFFECTIVE);
        return new RestResponse().setData(result);
    }

    public RestResponse reject(WfDoMethodEKP ekp) {
        WipSubRuleEntity subRule = wipSubRuleRepository.selectByRuleId(ekp.getFormInstanceId());
        Integer result = updateStatusByWorkFlow(subRule, ekp.getFormInstanceId(), SubRuleStatusEnum.DRAFT);
        return new RestResponse().setData(result);
    }

    public RestResponse abandon(WfDoMethodEKP ekp) {
        WipSubRuleEntity subRule = wipSubRuleRepository.selectByRuleId(ekp.getFormInstanceId());
        Integer result = updateStatusByWorkFlow(subRule, ekp.getFormInstanceId(), SubRuleStatusEnum.INVALID);
        return new RestResponse().setData(result);
    }

    private Integer updateStatusByWorkFlow(WipSubRuleEntity subRule, String ruleId, SubRuleStatusEnum status) {
        if (null == subRule) {
            log.info("ID【{}】对应的替换规则不存在", ruleId);
            return null;
        }
        if (null == status) {
            log.info("改后规则状态不可为空");
            return null;
        }
        subRule.setRuleStatus(status.getCode());
        EntityUtils.writeStdUpdInfoToEntity(subRule, "WorkFlow");
        return wipSubRuleRepository.update(subRule);
    }
}
