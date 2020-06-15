package com.cvte.scm.wip.infrastructure.requirement.repository;

import com.cvte.scm.wip.infrastructure.deprecated.BaseBatchMapper;
import com.cvte.scm.wip.domain.core.requirement.entity.XxwipMoInterfaceEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.XxwipMoInterfaceRepository;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.XxwipMoInterfaceDO;
import oracle.jdbc.OracleTypes;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/18 19:00
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class XxwipMoInterfaceRepositoryImpl implements XxwipMoInterfaceRepository {

    private BaseBatchMapper batchMapper;

    public XxwipMoInterfaceRepositoryImpl(@Qualifier("ORACLE_ERP_TEST_BATCH_MAPPER") BaseBatchMapper batchMapper) {
        this.batchMapper = batchMapper;
    }

    @Override
    public void batchInsert(List<XxwipMoInterfaceEntity> moInterfaceList) {
        List<XxwipMoInterfaceDO> moInterfaceDOList = XxwipMoInterfaceDO.batchBuildDO(moInterfaceList);
        batchMapper.insert(moInterfaceDOList);
    }

    @Override
    public String[] callProcedure(String pWipId, String pGroupId) {
        return batchMapper.execute(conn -> {
            @SuppressWarnings("SqlResolve") String procedure = "CALL XXAPS.XXWIP_INTERFACE_PKG.P_UPDATE_MO_ITEM(?,?,?,?)";
            CallableStatement cs = conn.prepareCall(procedure);
            cs.setString(1, pWipId);
            cs.setString(2, pGroupId);
            cs.registerOutParameter(3, OracleTypes.VARCHAR);// po_code
            cs.registerOutParameter(4, OracleTypes.VARCHAR);// po_message
            return cs;
        }, action -> {
            action.execute();
            return new String[]{action.getString(3), action.getString(4)};
        });
    }

}
