package com.cvte.scm.wip.spi.subrule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.scm.wip.domain.common.deprecated.RestCallUtils;
import com.cvte.scm.wip.domain.common.token.service.AccessTokenService;
import com.cvte.scm.wip.domain.core.subrule.service.SubRuleExecuteLogService;
import com.cvte.scm.wip.domain.core.subrule.valueobject.SubRuleExeLogVO;
import com.cvte.scm.wip.infrastructure.boot.config.api.ApsApiInfoConfiguration;
import com.cvte.scm.wip.spi.subrule.dto.ApsResponse;
import com.cvte.scm.wip.spi.subrule.dto.SubRuleExecuteLogDTO;
import jodd.http.HttpBase;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.util.MimeTypes;
import jodd.util.StringPool;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/29 10:34
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class SubRuleExecuteLogServiceImpl implements SubRuleExecuteLogService {

    private AccessTokenService accessTokenService;
    private ApsApiInfoConfiguration apsApiInfoConfiguration;

    public SubRuleExecuteLogServiceImpl(AccessTokenService accessTokenService, ApsApiInfoConfiguration apsApiInfoConfiguration) {
        this.accessTokenService = accessTokenService;
        this.apsApiInfoConfiguration = apsApiInfoConfiguration;
    }

    @Override
    public List<SubRuleExeLogVO> getLogByRuleNo(String ruleNo) {
        String token;
        try {
            token = accessTokenService.getAccessToken();
        } catch (Exception e) {
            throw new ServerException("", "iac服务异常, 请联系管理员");
        }
        String url = apsApiInfoConfiguration.getBaseUrl() + "/apsert/mrp/temp_sub/log/" + ruleNo;
        HttpResponse httpResponse = new HttpRequest()
                .method(RestCallUtils.RequestMethod.GET.name())
                .header(HttpBase.HEADER_CONTENT_TYPE, MimeTypes.MIME_APPLICATION_JSON)
                .set(url)
                .charset(StringPool.UTF_8)
                .header("x-iac-token", token)
                .send();
        if (httpResponse.charset("UTF-8").statusCode() != HttpStatus.SC_OK) {
            throw new ServerException("", "APS服务异常, 请联系管理");
        }
        ApsResponse<SubRuleExecuteLogDTO> apsResponse = JSON.parseObject(httpResponse.bodyText(), new TypeReference<ApsResponse<SubRuleExecuteLogDTO>>(){});
        if (!"0".equals(apsResponse.getStatus())) {
            throw new ParamsIncorrectException(String.format("日志接口返回值异常, message:%s", apsResponse.getMessage()));
        }

        SubRuleExecuteLogDTO logDTO = apsResponse.getData();
        List<SubRuleExeLogVO> exeLogList = new ArrayList<>();
        for (SubRuleExecuteLogDTO.SuccessLogDTO successLog : logDTO.getSuccessRecordList()) {
            SubRuleExeLogVO exeLog = new SubRuleExeLogVO();
            exeLog.setRuleNo(logDTO.getRuleNo())
                    .setOrderNo(successLog.getOrderNo())
                    .setBeforeItemNo(successLog.getBeforeItemNo())
                    .setAfterItemNo(successLog.getAfterItemNo())
                    .setCrtTime(successLog.getCrtTime())
                    .setResult("替换成功");
            exeLogList.add(exeLog);
        }
        for (SubRuleExecuteLogDTO.FailureLogDTO failureLog : logDTO.getFailureRecordList()) {
            SubRuleExeLogVO exeLog = new SubRuleExeLogVO();
            exeLog.setRuleNo(logDTO.getRuleNo())
                    .setOrderNo(failureLog.getOrderNo())
                    .setCrtTime(failureLog.getCrtTime())
                    .setResult(String.format("替换失败:%s", failureLog.getFailureReason()));
            exeLogList.add(exeLog);
        }
        return exeLogList;
    }
}
