package com.cvte.scm.wip.domain.core.scm.service;

import com.cvte.scm.wip.domain.core.scm.dto.query.SysOrgOrganizationVQuery;
import com.cvte.scm.wip.domain.core.scm.repository.ScmViewCommonRepository;
import com.cvte.scm.wip.domain.core.scm.vo.SysBuDeptVO;
import com.cvte.scm.wip.domain.core.scm.vo.SysOrgOrganizationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zy
 * @date 2020-05-11 17:36
 **/
@Service
public class ScmViewCommonService {

    @Autowired
    private ScmViewCommonRepository repository;

    public List<SysOrgOrganizationVO> listSysOrgOrganizationVO(SysOrgOrganizationVQuery query) {

        return repository.listSysOrgOrganizationVO(query);
    }

    public String getFactoryCodeById(String factoryId) {
        return repository.getFactoryCodeById(factoryId);
    }

    public SysBuDeptVO getSysBuDeptVO(String buCode, String deptCode) {
        return repository.getSysBuDeptVO(buCode, deptCode);
    }


}
