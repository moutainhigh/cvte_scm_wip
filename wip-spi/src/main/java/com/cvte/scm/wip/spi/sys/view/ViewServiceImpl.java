package com.cvte.scm.wip.spi.sys.view;

import com.cvte.csb.sys.database.dto.request.DatabaseQueryDTO;
import com.cvte.csb.sys.view.dto.bean.PageResult;
import com.cvte.csb.sys.view.dto.request.SysViewPageParamDTO;
import com.cvte.scm.wip.domain.common.view.entity.PageResultEntity;
import com.cvte.scm.wip.domain.common.view.service.ViewService;
import com.cvte.scm.wip.domain.common.view.vo.DatabaseQueryVO;
import com.cvte.scm.wip.domain.common.view.vo.SysViewPageParamVO;
import com.cvte.scm.wip.infrastructure.client.sys.view.SysViewApiClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 10:01
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class ViewServiceImpl implements ViewService {

    private SysViewApiClient sysViewApiClient;

    public ViewServiceImpl(SysViewApiClient sysViewApiClient) {
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
