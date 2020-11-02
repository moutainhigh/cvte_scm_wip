package com.cvte.scm.wip.spi.changebill;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cvte.csb.base.commons.OperatingUser;
import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.scm.wip.common.constants.CommonUserConstant;
import com.cvte.scm.wip.common.enums.error.ReqInsErrEnum;
import com.cvte.scm.wip.domain.common.deprecated.RestCallUtils;
import com.cvte.scm.wip.domain.common.token.service.AccessTokenService;
import com.cvte.scm.wip.domain.common.user.entity.UserBaseEntity;
import com.cvte.scm.wip.domain.common.user.service.UserService;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;
import com.cvte.scm.wip.domain.core.changebill.service.ChangeBillWriteBackService;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ProcessingStatusEnum;
import com.cvte.scm.wip.infrastructure.boot.config.api.EbsApiInfoConfiguration;
import com.cvte.scm.wip.spi.changebill.DTO.ChangeBillWriteBackDTO;
import com.cvte.scm.wip.spi.common.EbsResponse;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/11 12:25
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Component
public class ChangeBillWriteBackServiceImpl implements ChangeBillWriteBackService {

    private UserService userService;
    private AccessTokenService accessTokenService;
    private EbsApiInfoConfiguration ebsApiInfoConfiguration;

    public ChangeBillWriteBackServiceImpl(UserService userService, AccessTokenService accessTokenService, EbsApiInfoConfiguration ebsApiInfoConfiguration) {
        this.userService = userService;
        this.accessTokenService = accessTokenService;
        this.ebsApiInfoConfiguration = ebsApiInfoConfiguration;
    }

    @Override
    public String writeBackToEbs(ReqInsEntity reqInsEntity, ChangeBillEntity changeBillEntity) {
        String account = CommonUserConstant.SCM_WIP;
        OperatingUser user = CurrentContext.getCurrentOperatingUser();
        if (Objects.nonNull(user) && CommonUserConstant.AUTORUN.equals(user.getId())) {
            account = CommonUserConstant.AUTORUN;
        } else if (Objects.nonNull(user)) {
            UserBaseEntity userEntity = userService.getEnableUserInfo(user.getId());
            if (Objects.isNull(userEntity)) {
                throw new ParamsIncorrectException(String.format("不存在ID为%s的用户", user.getId()));
            }
            account = userEntity.getAccount();
        }

        ChangeBillWriteBackDTO writeBackDTO = new ChangeBillWriteBackDTO();
        writeBackDTO.setBillNo(changeBillEntity.getBillNo())
                .setBillType(changeBillEntity.getBillType())
                .setExecuteCode(reqInsEntity.getStatus())
                .setUserNo(account);
        String executeMessage;
        if (ProcessingStatusEnum.CLOSE.getCode().equals(reqInsEntity.getStatus())) {
            executeMessage = reqInsEntity.getInvalidReason();
        } else {
            executeMessage = reqInsEntity.getExecuteResult();
        }
        writeBackDTO.setExecuteMessage(executeMessage);

        String jsonParamStr = JSON.toJSONString(writeBackDTO);
        String iacToken;
        try {
            iacToken = accessTokenService.getAccessToken();
        } catch (Exception e) {
            throw new ServerException("5000000", "IAC鉴权服务不可用,无法回写执行结果到EBS返工单");
        }
        String url = ebsApiInfoConfiguration.getBaseUrl() + "/xxfnd/pubprocess/recordCnResult";

        String restResponse = RestCallUtils.callRest(RestCallUtils.RequestMethod.POST, url, iacToken, jsonParamStr);
        EbsResponse<String> ebsResponse = JSON.parseObject(restResponse, new TypeReference<EbsResponse<String>>(){});
        if (!"S".equals(ebsResponse.getRtStatus())) {
            throw new ServerException(ReqInsErrEnum.WRITE_BACK_ERR.getCode(), ReqInsErrEnum.WRITE_BACK_ERR.getDesc() + ebsResponse.getRtMessage());
        } else {
            return ebsResponse.getRtMessage();
        }
    }

}
