package com.cvte.scm.wip.domain.common.log.dto;

import com.cvte.csb.base.commons.OperatingUser;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.domain.common.log.entity.WipOperationLogEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * @author zy
 * @date 2020-05-22 16:55
 **/
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class WipLogDTO {

    private String module;

    private String referenceId;

    private String operation;

    private String detail;


    /**
     *
     * @param
     * @param module
     * @param referenceId
     * @param operation
     * @return
     **/
    public WipLogDTO(String module, String referenceId, String operation) {
        this(module, referenceId, operation, null);
    }

    /**
     *
     * @param module
     * @param referenceId
     * @param operation
     * @param detail
     * @return
     **/
    public WipLogDTO(String module, String referenceId, String operation, String detail) {
        this.module = module;
        this.referenceId = referenceId;
        this.operation = operation;
        this.detail = detail;
    }

    public WipOperationLogEntity buildEntity(OperatingUser operatingUser) {
        WipOperationLogEntity entity = new WipOperationLogEntity();
        BeanUtils.copyProperties(this, entity);
        entity.setId(UUIDUtils.getUUID())
                .setCrtTime(new Date())
                .setCrtName(operatingUser.getName())
                .setCrtUser(operatingUser.getAccount());
        return entity;
    }


}
