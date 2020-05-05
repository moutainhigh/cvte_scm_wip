package com.cvte.scm.wip.domain.common.user.repository;

import com.cvte.csb.core.interfaces.vo.RestResponse;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/5 11:48
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface AuthRepository {

    RestResponse login(String account, String password);

    RestResponse me();

    RestResponse getCurrentUserInfo(String id);

}
