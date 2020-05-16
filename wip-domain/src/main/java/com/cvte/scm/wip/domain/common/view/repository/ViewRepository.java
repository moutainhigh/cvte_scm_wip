package com.cvte.scm.wip.domain.common.view.repository;

import com.cvte.scm.wip.domain.common.view.entity.PageResultEntity;
import com.cvte.scm.wip.domain.common.view.vo.DatabaseQueryVO;
import com.cvte.scm.wip.domain.common.view.vo.SysViewPageParamVO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/10 16:46
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public interface ViewRepository {

    PageResultEntity getViewPageDataByViewPageParam(SysViewPageParamVO sysViewPageParam);

    List<Map<String, Object>> executeQuery(DatabaseQueryVO databaseQueryVO);

}
