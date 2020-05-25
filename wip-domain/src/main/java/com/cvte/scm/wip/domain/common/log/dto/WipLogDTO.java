package com.cvte.scm.wip.domain.common.log.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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


}
