package com.cvte.scm.wip.spi.changebill;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.enums.error.ReqInsErrEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.common.deprecated.RestCallUtils;
import com.cvte.scm.wip.domain.common.token.service.AccessTokenService;
import com.cvte.scm.wip.domain.common.user.entity.UserBaseEntity;
import com.cvte.scm.wip.domain.common.user.service.UserService;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;
import com.cvte.scm.wip.domain.core.changebill.service.ChangeBillWriteBackService;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
import com.cvte.scm.wip.infrastructure.boot.config.api.EbsApiInfoConfiguration;
import com.cvte.scm.wip.spi.changebill.DTO.ChangeBillWriteBackDTO;
import com.cvte.scm.wip.spi.common.EbsResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
        UserBaseEntity userEntity = userService.getEnableUserInfo(EntityUtils.getWipUserId());

        ChangeBillWriteBackDTO writeBackDTO = new ChangeBillWriteBackDTO();
        writeBackDTO.setBillNo(changeBillEntity.getBillNo())
                .setBillType(changeBillEntity.getBillType())
                .setExecuteCode(reqInsEntity.getStatus())
                .setUserNo(userEntity.getAccount());
        String executeMessage;
        if (StatusEnum.CLOSE.getCode().equals(reqInsEntity.getStatus())) {
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
