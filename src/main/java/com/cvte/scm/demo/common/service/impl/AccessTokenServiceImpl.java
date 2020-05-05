package com.cvte.scm.demo.common.service.impl;

import com.cvte.csb.core.exception.client.authorizations.InvalidTokenException;
import com.cvte.csb.iac.APP;
import com.cvte.csb.iac.IACManager;
import com.cvte.csb.iac.exception.IACServerErrorException;
import com.cvte.csb.iac.exception.IACVerifyFailException;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.demo.common.service.AccessTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * @Author: wufeng
 * @Date: 2019/12/23 14:26
 */
@Slf4j
@Service
public class AccessTokenServiceImpl implements AccessTokenService {

    @Value("${csb.iac.appId}")
    private String appId;

    @Value("${csb.iac.appSecret}")
    private String appSecret;

    @Value("${csb.iac.systemId}")
    private String systemId;


    @Override
    public boolean iacVerify(String iacToken) {
        try {
            if (StringUtils.isBlank(iacToken)) {
                throw new InvalidTokenException("x-iac-token为空");
            }
            APP app = IACManager.verify(systemId, iacToken);

            if (ObjectUtils.isNull(app) || StringUtils.isBlank(app.getAppId(), app.getAppName())) {
                throw new InvalidTokenException("IAC鉴权失败，app信息为空");
            }
            //鉴权通过
            return true;
        } catch (IACVerifyFailException | UnsupportedEncodingException e) {
            // do something 无效token
            throw new InvalidTokenException("token失效");
        } catch (IACServerErrorException e) {
            //do something 验证服务暂不可用
            throw new InvalidTokenException("IAC鉴权服务暂不可用");
        }
    }

    @Override
    public String getAccessToken() throws Exception {
        return IACManager.getAccessToken(appId, appSecret);
    }

}