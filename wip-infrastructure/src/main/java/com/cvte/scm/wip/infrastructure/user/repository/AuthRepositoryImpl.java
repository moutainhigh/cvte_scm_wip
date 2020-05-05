package com.cvte.scm.wip.infrastructure.user.repository;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.domain.common.user.repository.AuthRepository;
import com.cvte.scm.wip.infrastructure.client.sys.auth.AuthApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/5 11:49
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class AuthRepositoryImpl implements AuthRepository {

    @Autowired
    private AuthApiClient authApiClient;

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
