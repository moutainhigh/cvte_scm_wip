package com.cvte.scm.wip.spi.rtc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.domain.common.deprecated.RestCallUtils;
import com.cvte.scm.wip.domain.common.token.service.AccessTokenService;
import com.cvte.scm.wip.domain.core.rtc.service.WipMtrRtcLotControlService;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcLotControlVO;
import com.cvte.scm.wip.infrastructure.boot.config.api.BsmApiInfoConfiguration;
import com.cvte.scm.wip.spi.subrule.dto.ApsResponse;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/17 16:50
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class WipMtrRtcLotControlServiceImpl implements WipMtrRtcLotControlService {

    private AccessTokenService accessTokenService;
    private BsmApiInfoConfiguration bsmApiInfoConfiguration;

    public WipMtrRtcLotControlServiceImpl(AccessTokenService accessTokenService, BsmApiInfoConfiguration bsmApiInfoConfiguration) {
        this.accessTokenService = accessTokenService;
        this.bsmApiInfoConfiguration = bsmApiInfoConfiguration;
    }

    @Override
    public List<WipMtrRtcLotControlVO> getLotControlByOptionNo(String optionNo, String optionValue) {
        String url = String.format("%s%s%s%s%s", bsmApiInfoConfiguration.getBaseUrl(), "/common_query/mtr/lot_control/", optionNo, "/", optionValue);
        String token;
        try {
            token = accessTokenService.getAccessToken();
        } catch (Exception e) {
            throw new ServerException("", "IAC鉴权服务不可用,无法获取批次强管控数据");
        }

        String restResponse = RestCallUtils.callRest(RestCallUtils.RequestMethod.GET, url, token, Collections.emptyMap());
        ApsResponse<List<WipMtrRtcLotControlVO>> response = JSON.parseObject(restResponse, new TypeReference<ApsResponse<List<WipMtrRtcLotControlVO>>>(){});
        if (!"0".equals(response.getStatus())) {
            throw new ParamsIncorrectException(String.format("BSM系统接口调用异常, 异常信息: %s", response.getMessage()));
        }
        return response.getData();
    }
}
