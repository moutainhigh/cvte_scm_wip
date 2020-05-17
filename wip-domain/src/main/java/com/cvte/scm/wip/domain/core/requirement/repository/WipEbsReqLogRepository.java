package com.cvte.scm.wip.domain.core.requirement.repository;

import com.cvte.scm.wip.domain.core.requirement.entity.WipEbsReqLogEntity;

import java.util.Date;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 21:59
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface WipEbsReqLogRepository {

    List<WipEbsReqLogEntity> selectBetweenTimeInStatus(Date timeFrom, Date timeTo, String... processStatus);

}
