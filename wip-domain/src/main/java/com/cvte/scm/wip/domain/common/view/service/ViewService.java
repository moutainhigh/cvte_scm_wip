package com.cvte.scm.wip.domain.common.view.service;

import com.cvte.scm.wip.domain.common.view.entity.PageResultEntity;
import com.cvte.scm.wip.domain.common.view.vo.DatabaseQueryVO;
import com.cvte.scm.wip.domain.common.view.vo.SysViewPageParamVO;

import java.util.List;
import java.util.Map;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 10:01
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface ViewService {

    PageResultEntity getViewPageDataByViewPageParam(SysViewPageParamVO sysViewPageParam);

    List<Map<String, Object>> executeQuery(DatabaseQueryVO databaseQueryVO);

}
