package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.domain.common.view.service.ViewService;
import com.cvte.scm.wip.domain.common.view.vo.DatabaseQueryVO;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqHeaderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 21:41
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class WipReqHeaderPageService {

    private WipReqHeaderRepository wipReqHeaderRepository;
    private ViewService viewService;

    public WipReqHeaderPageService(WipReqHeaderRepository wipReqHeaderRepository, ViewService viewService) {
        this.wipReqHeaderRepository = wipReqHeaderRepository;
        this.viewService = viewService;
    }

    public WipReqHeaderEntity getDetail(String headerId) {
        WipReqHeaderEntity queryHeader = new WipReqHeaderEntity().setHeaderId(headerId);
        List<WipReqHeaderEntity> wipReqHeaderList = wipReqHeaderRepository.selectList(queryHeader);
        if (ListUtil.empty(wipReqHeaderList)) {
            return null;
        }
        WipReqHeaderEntity reqHeader = wipReqHeaderList.get(0);
        DatabaseQueryVO databaseQuery = new DatabaseQueryVO();
        String databaseId = "xxscmcsb";
        String queryOrgNameTemplate = "SELECT So.ORG_NAME FROM XXSCMCSB.SYS_ORG So INNER JOIN XXSCMCSB.SYS_ORG_EXT Soe ON So.ID = Soe.ID WHERE Soe.EBS_CODE = %s";
        databaseQuery.setId(databaseId);
        databaseQuery.setSql(String.format(queryOrgNameTemplate, reqHeader.getOrganizationId()));
        List<Map<String, Object>> orgNameMapList = viewService.executeQuery(databaseQuery);
        if (ListUtil.notEmpty(orgNameMapList)) {
            Map<String, Object> orgNameMap = orgNameMapList.get(0);
            reqHeader.setOrganizationId((String) orgNameMap.get("ORG_NAME"));
        }
        databaseQuery.setSql(String.format(queryOrgNameTemplate, reqHeader.getFactoryId()));
        List<Map<String, Object>> factoryNameMapList = viewService.executeQuery(databaseQuery);
        if (ListUtil.notEmpty(factoryNameMapList)) {
            Map<String, Object> factoryNameMap = factoryNameMapList.get(0);
            reqHeader.setFactoryId((String) factoryNameMap.get("ORG_NAME"));
        }
        return reqHeader;
    }

}
