package com.cvte.scm.wip.spi.sys.user;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.domain.common.user.service.AuthService;
import com.cvte.scm.wip.infrastructure.client.sys.auth.AuthApiClient;
import org.springframework.stereotype.Service;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 09:59
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service(value = "wipAuthService")
public class AuthServiceImpl implements AuthService {

    private AuthApiClient authApiClient;

    public AuthServiceImpl(AuthApiClient authApiClient) {
        this.authApiClient = authApiClient;
    }

    @Override
    public RestResponse login(String account, String password) {
        return authApiClient.login(account, password);
    }

    @Override
    public RestResponse me() {
        return authApiClient.me();
    }

    @Override
    public RestResponse getCurrentUserInfo(String id) {
        return authApiClient.getCurrentUserInfo(id);
    }
}
