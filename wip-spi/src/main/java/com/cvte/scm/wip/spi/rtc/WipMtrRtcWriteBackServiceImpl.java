package com.cvte.scm.wip.spi.rtc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.constants.CommonUserConstant;
import com.cvte.scm.wip.common.enums.YoNEnum;
import com.cvte.scm.wip.common.utils.BatchProcessUtils;
import com.cvte.scm.wip.common.utils.CodeableEnumUtils;
import com.cvte.scm.wip.common.utils.DateUtils;
import com.cvte.scm.wip.domain.common.deprecated.RestCallUtils;
import com.cvte.scm.wip.domain.common.token.service.AccessTokenService;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqHeaderService;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcAssignEntity;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.domain.core.rtc.service.WipMtrRtcWriteBackService;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcHeaderTypeEnum;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.exception.EbsInvokeException;
import com.cvte.scm.wip.infrastructure.boot.config.api.CsbpApiInfoConfiguration;
import com.cvte.scm.wip.infrastructure.sys.org.mapper.SysOrganizationMapper;
import com.cvte.scm.wip.spi.common.CommonResponse;
import com.cvte.scm.wip.spi.rtc.dto.XxwipTransactionActionDTO;
import com.cvte.scm.wip.spi.rtc.dto.XxwipTransactionHeadersDTO;
import com.cvte.scm.wip.spi.rtc.dto.XxwipTransactionInfoDTO;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/9/30 15:18
 */
@Service
public class WipMtrRtcWriteBackServiceImpl implements WipMtrRtcWriteBackService {

    private Map<String, String> factoryIdCodeMap = new HashMap<>();

    private CsbpApiInfoConfiguration csbpApiInfoConfiguration;
    private AccessTokenService accessTokenService;
    private SysOrganizationMapper sysOrganizationMapper;
    private WipReqHeaderService wipReqHeaderService;

    public WipMtrRtcWriteBackServiceImpl(CsbpApiInfoConfiguration csbpApiInfoConfiguration, AccessTokenService accessTokenService, SysOrganizationMapper sysOrganizationMapper, WipReqHeaderService wipReqHeaderService) {
        this.csbpApiInfoConfiguration = csbpApiInfoConfiguration;
        this.accessTokenService = accessTokenService;
        this.sysOrganizationMapper = sysOrganizationMapper;
        this.wipReqHeaderService = wipReqHeaderService;
    }

    @Override
    public String createAndSubmit(WipMtrRtcHeaderEntity rtcHeader) {
        // 创建EBS单据
        CommonResponse<Map<String, Object>> syncResponse = this.sync(rtcHeader, "INSERT");
        // 来源单据号
        String transactionNumber = (String) syncResponse.getData().get("transactionNumber");
        rtcHeader.setSourceBillNo(transactionNumber);

        // 同步行ID
        this.syncLineInfo(rtcHeader);

        // 提交
        XxwipTransactionActionDTO actionDTO = new XxwipTransactionActionDTO();
        actionDTO.setAction("SUBMIT")
                .setOrganizationId(rtcHeader.getOrganizationId())
                .setTransactionNumber(transactionNumber)
                .setUserName(rtcHeader.getUpdUser());
        try {
            action(actionDTO);
        } catch (Exception e) {
            actionDTO.setAction("CANCEL");
            action(actionDTO);
            throw e;
        }

        return "创建成功";
    }

    @Override
    public String update(WipMtrRtcHeaderEntity rtcHeader) {
        this.sync(rtcHeader, "UPDATE");
        return "更新成功";
    }

    @Override
    public void syncLineInfo(WipMtrRtcHeaderEntity rtcHeader) {
        String organizationCode = sysOrganizationMapper.getOrgCodeById(rtcHeader.getOrganizationId());
        // 查询
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String url = csbpApiInfoConfiguration.getBaseUrl() + "/wip/wipcompissue/wipCompIssueQuery";
        String iacToken = getIacToken();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("organizationCode", organizationCode);
        paramMap.put("lastUpdateDate", dateFormat.format(rtcHeader.getCrtTime()));
        paramMap.put("transactionNumber", rtcHeader.getSourceBillNo());
        String responseStr = RestCallUtils.callRest(RestCallUtils.RequestMethod.GET, url, iacToken, paramMap);

        // 设置来源行ID
        CommonResponse<List<XxwipTransactionInfoDTO>> response = JSON.parseObject(responseStr, new TypeReference<CommonResponse<List<XxwipTransactionInfoDTO>>>() {
        });
        List<XxwipTransactionInfoDTO> transactionInfoDTOList = response.getData();
        updateLineSourceId(rtcHeader, transactionInfoDTOList);
    }

    @Override
    public String cancel(WipMtrRtcHeaderEntity rtcHeader) {
        XxwipTransactionActionDTO actionDTO = new XxwipTransactionActionDTO();
        actionDTO.setAction("CANCEL")
                .setOrganizationId(rtcHeader.getOrganizationId())
                .setTransactionNumber(rtcHeader.getSourceBillNo())
                .setUserName(rtcHeader.getUpdUser());
        action(actionDTO);
        return "取消成功";
    }

    @Override
    public String close(WipMtrRtcHeaderEntity rtcHeader) {
        XxwipTransactionActionDTO actionDTO = new XxwipTransactionActionDTO();
        actionDTO.setAction("CLOSE")
                .setOrganizationId(rtcHeader.getOrganizationId())
                .setTransactionNumber(rtcHeader.getSourceBillNo())
                .setUserName(rtcHeader.getUpdUser());
        action(actionDTO);
        return "关闭成功";
    }

    @Override
    public String post(WipMtrRtcHeaderEntity rtcHeader) {
        XxwipTransactionActionDTO actionDTO = new XxwipTransactionActionDTO();
        actionDTO.setAction("POST")
                .setOrganizationId(rtcHeader.getOrganizationId())
                .setTransactionNumber(rtcHeader.getSourceBillNo())
                .setUserName(rtcHeader.getUpdUser())
                .setTransactionLineId(rtcHeader.getLineList().stream().map(WipMtrRtcLineEntity::getSourceLineId).collect(Collectors.joining(",")));
        action(actionDTO);
        return "过账成功";
    }

    @Override
    public String cancelLine(WipMtrRtcHeaderEntity rtcHeader) {
        XxwipTransactionActionDTO actionDTO = new XxwipTransactionActionDTO();
        actionDTO.setAction("CANCEL_LINE")
                .setOrganizationId(rtcHeader.getOrganizationId())
                .setTransactionNumber(rtcHeader.getSourceBillNo())
                .setUserName(rtcHeader.getUpdUser())
                .setTransactionLineId(rtcHeader.getLineList().stream().map(WipMtrRtcLineEntity::getSourceLineId).collect(Collectors.joining(",")));
        action(actionDTO);
        return "取消行成功";
    }

    private void action(XxwipTransactionActionDTO actionDTO) {
        String iacToken = getIacToken();
        String actionUrl = csbpApiInfoConfiguration.getBaseUrl() + "/wip/wipcompissue/wipCompIssueAction";
        String actionJsonParam = JSON.toJSONString(actionDTO);
        String actionResponseStr = RestCallUtils.callRest(RestCallUtils.RequestMethod.POST, actionUrl, iacToken, actionJsonParam);
        CommonResponse<Map<String, Object>> actionResponse = parseResponse(actionResponseStr);
        checkSuccess(actionResponse);
    }

    private CommonResponse<Map<String, Object>> parseResponse(String response) {
        return JSON.parseObject(response, new TypeReference<CommonResponse<Map<String, Object>>>() {
        });
    }

    private void checkSuccess(CommonResponse<Map<String, Object>> commonResponse) {
        if (!"0".equals(commonResponse.getStatus()) || !"S".equals(commonResponse.getData().get("rtStatus"))) {
            String msg;
            String rtMessage = (String)commonResponse.getData().get("rtMessage");
            if (!"success".equals(commonResponse.getMessage())) {
                msg = commonResponse.getMessage() + "_" + rtMessage;
            } else {
                msg = rtMessage;
            }
            throw new EbsInvokeException(msg);
        }
    }

    private CommonResponse<Map<String, Object>> sync(WipMtrRtcHeaderEntity rtcHeader, String action) {
        XxwipTransactionHeadersDTO headersDTO = generateSyncDTO(rtcHeader, action);
        // 同步至EBS
        String syncUrl = csbpApiInfoConfiguration.getBaseUrl() + "/wip/wipcompissue/wipCompIssueImport";
        String iacToken = getIacToken();
        // 请求参数
        Map<String, XxwipTransactionHeadersDTO> paramMap = new HashMap<>();
        paramMap.put("importHdrJson", headersDTO);
        String syncJsonParam = JSON.toJSONString(paramMap);
        // 同步接口
        String syncResponseStr = RestCallUtils.callRest(RestCallUtils.RequestMethod.POST, syncUrl, iacToken, syncJsonParam);
        CommonResponse<Map<String, Object>> syncResponse = parseResponse(syncResponseStr);
        checkSuccess(syncResponse);
        return syncResponse;
    }

    private XxwipTransactionHeadersDTO generateSyncDTO(WipMtrRtcHeaderEntity rtcHeader, String action) {
        WipReqHeaderEntity reqHeader = wipReqHeaderService.getBySourceId(rtcHeader.getMoId());

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        XxwipTransactionHeadersDTO headersDTO = new XxwipTransactionHeadersDTO();
        headersDTO.setTransactionNumber(rtcHeader.getSourceBillNo())
                .setInterfaceOrigSource(CommonUserConstant.SCM_WIP)
                .setInterfaceOrigSourceId(rtcHeader.getHeaderId())
                .setInterfaceAction(action)
                .setOrganizationId(rtcHeader.getOrganizationId())
                .setApplyDate(dateFormat.format(DateUtils.getMinutesBeforeTime(rtcHeader.getUpdTime(), 1)))
                .setTransactionDate(dateFormat.format(DateUtils.getMinutesBeforeTime(rtcHeader.getUpdTime(), 1)))
                .setTransactionTypeDesc(CodeableEnumUtils.getCodeableEnumByCode(rtcHeader.getBillType(), WipMtrRtcHeaderTypeEnum.class).getDesc() + "单")
                .setWipEntityName(reqHeader.getSourceNo())
                .setWipLotNumber(reqHeader.getSourceLotNo())
                .setStartQuantity(rtcHeader.getBillQty().toString())
                .setRemark(rtcHeader.getRemark())
                .setUserName(rtcHeader.getCrtUser())
                .setAutoDist(YoNEnum.N.getCode())
                .setSubmitFlag(YoNEnum.N.getCode())
                .setPostFlag(YoNEnum.N.getCode());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        List<XxwipTransactionHeadersDTO.XxwipTransactionLinesDTO> linesDTOList = new ArrayList<>();
        for (WipMtrRtcLineEntity rtcLine : rtcHeader.getLineList()) {
            XxwipTransactionHeadersDTO.XxwipTransactionLinesDTO linesDTO = new XxwipTransactionHeadersDTO.XxwipTransactionLinesDTO();
            linesDTO.setTransactionNumber(rtcHeader.getSourceBillNo())
                    .setInterfaceOrigSource(CommonUserConstant.SCM_WIP)
                    .setInterfaceOrigSourceId(rtcLine.getLineId())
                    .setOrganizationId(rtcHeader.getOrganizationId())
                    .setComponentItem(rtcLine.getItemNo())
                    .setOperationSeqNum(rtcLine.getWkpNo())
                    .setSupplySubinventory(rtcLine.getInvpNo())
                    .setSupplyLocator(this.getLocator(rtcLine.getInvpNo(), rtcHeader.getFactoryId()))
                    .setQuantityIssue(rtcLine.getIssuedQty().toString())
                    .setNotes(rtcLine.getRemark())
                    .setVendorName(rtcLine.getSupplierName())
                    .setBadReason(rtcLine.getBadMtrReason())
                    .setUserName(rtcLine.getCrtUser())
                    .setSeqNum(rtcLine.getSerialNo())
                    .setBadDesc(rtcLine.getBadMtrDesc());
            linesDTOList.add(linesDTO);

            List<WipMtrRtcAssignEntity> rtcAssignList = rtcLine.getAssignList();
            if (ListUtil.notEmpty(rtcAssignList)) {
                List<XxwipTransactionHeadersDTO.XxwipTransactionAssignsDTO> assignDTOList = new ArrayList<>();
                for (WipMtrRtcAssignEntity rtcAssign : rtcAssignList) {
                    XxwipTransactionHeadersDTO.XxwipTransactionAssignsDTO assignDTO = new XxwipTransactionHeadersDTO.XxwipTransactionAssignsDTO();
                    assignDTO.setInterfaceOrigSource(CommonUserConstant.SCM_WIP)
                            .setInterfaceOrigSourceId(rtcAssign.getAssignId())
                            .setOrganizationId(rtcAssign.getOrganizationId())
                            .setComponentItem(rtcLine.getItemNo())
                            .setOperationSeqNum(rtcLine.getWkpNo())
                            .setLotNumber(rtcAssign.getMtrLotNo())
                            .setTransactionDate(Objects.nonNull(rtcAssign.getFirstStockinDate()) ? df.format(rtcAssign.getFirstStockinDate()) : "")
                            .setTransactionQuantity(rtcAssign.getIssuedQty().toString());
                    assignDTOList.add(assignDTO);
                }
                linesDTO.setImportBatchJson(assignDTOList);
            }
        }
        headersDTO.setImportLnJson(linesDTOList);
        return headersDTO;
    }

    private void updateLineSourceId(WipMtrRtcHeaderEntity rtcHeader, List<XxwipTransactionInfoDTO> transactionInfoDTOList) {
        Map<String, XxwipTransactionInfoDTO> transactionInfoDTOMap = transactionInfoDTOList.stream().collect(Collectors.toMap(info -> BatchProcessUtils.getKey(info.getComponentItemId(), info.getOperationSeqNum()), Function.identity(), (v1, v2) -> v1));
        for (WipMtrRtcLineEntity rtcLine : rtcHeader.getLineList()) {
            String itemKye = BatchProcessUtils.getKey(rtcLine.getItemId(), rtcLine.getWkpNo());
            XxwipTransactionInfoDTO transactionInfoDTO = transactionInfoDTOMap.get(itemKye);
            if (Objects.isNull(transactionInfoDTO)) {
                throw new ParamsIncorrectException(String.format("物料%s工序%s同步失败", rtcLine.getItemNo(), rtcLine.getWkpNo()));
            }
            rtcLine.setSourceLineId(transactionInfoDTO.getTransactionLineId());
        }
    }

    private String getLocator(String invpNo, String factoryId) {
        if (StringUtils.isBlank(invpNo)) {
            return null;
        }
        String factoryCode = factoryIdCodeMap.get(factoryId);
        if (StringUtils.isBlank(factoryCode)) {
            factoryCode = sysOrganizationMapper.getFactoryCodeById(factoryId);
            factoryIdCodeMap.put(factoryId, factoryCode);
        }
        return invpNo + "." + factoryCode + ".";
    }

    private String getIacToken() {
        String iacToken;
        try {
            iacToken = accessTokenService.getAccessToken();
        } catch (Exception e) {
            throw new ServerException("", "IAC鉴权服务不可用,无法从EBS同步返工单数据");
        }
        return iacToken;
    }

}
