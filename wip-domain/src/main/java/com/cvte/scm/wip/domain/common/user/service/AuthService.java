package com.cvte.scm.wip.domain.common.user.service;

import com.cvte.csb.core.interfaces.vo.RestResponse;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 09:58
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface AuthService {

    RestResponse login(String account, String password);

    RestResponse me();

    RestResponse getCurrentUserInfo(String id);

}
