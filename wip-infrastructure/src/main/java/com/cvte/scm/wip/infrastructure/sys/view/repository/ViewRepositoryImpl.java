package com.cvte.scm.wip.infrastructure.sys.view.repository;

import com.cvte.csb.sys.database.dto.request.DatabaseQueryDTO;
import com.cvte.csb.sys.view.dto.bean.PageResult;
import com.cvte.csb.sys.view.dto.request.SysViewPageParamDTO;
import com.cvte.scm.wip.domain.common.view.entity.PageResultEntity;
import com.cvte.scm.wip.domain.common.view.repository.ViewRepository;
import com.cvte.scm.wip.domain.common.view.vo.DatabaseQueryVO;
import com.cvte.scm.wip.domain.common.view.vo.SysViewPageParamVO;
import com.cvte.scm.wip.infrastructure.client.sys.view.SysViewApiClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/10 16:57
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class ViewRepositoryImpl implements ViewRepository {

    private SysViewApiClient sysViewApiClient;

    public ViewRepositoryImpl(SysViewApiClient sysViewApiClient) {
        this.sysViewApiClient = sysViewApiClient;
    }

    @Override
    public PageResultEntity getViewPageDataByViewPageParam(SysViewPageParamVO sysViewPageParam) {
        SysViewPageParamDTO sysViewPageParamDTO = new SysViewPageParamDTO();
        BeanUtils.copyProperties(sysViewPageParam, sysViewPageParamDTO);
        PageResult pageResult = sysViewApiClient.getViewPageDataByViewPageParam(sysViewPageParamDTO).getData();
        PageResultEntity pageResultEntity = new PageResultEntity();
        BeanUtils.copyProperties(pageResult, pageResultEntity);
        return pageResultEntity;
    }

    @Override
    public List<Map<String, Object>> executeQuery(DatabaseQueryVO databaseQueryVO) {
        DatabaseQueryDTO databaseQuery = new DatabaseQueryDTO();
        BeanUtils.copyProperties(databaseQueryVO, databaseQuery);
        return sysViewApiClient.executeQuery(databaseQuery);
    }
}
