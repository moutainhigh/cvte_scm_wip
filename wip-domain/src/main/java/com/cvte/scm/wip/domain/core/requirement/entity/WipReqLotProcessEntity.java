package com.cvte.scm.wip.domain.core.requirement.entity;

import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.base.domain.Entity;
import com.cvte.scm.wip.common.enums.YoNEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.common.base.BaseModel;
import com.cvte.scm.wip.domain.core.item.service.ScmItemService;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLotProcessRepository;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqLotIssuedService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqLotProcessVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.LotIssuedLockTypeEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ProcessingStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/4 10:47
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Component
@Accessors(chain = true)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WipReqLotProcessEntity extends BaseModel implements Entity<String> {

    private WipReqLotProcessRepository wipReqLotProcessRepository;
    private WipReqLotIssuedService wipReqLotIssuedService;

    @Override
    public String getUniqueId() {
        return this.id;
    }

    private String id;

    private String organizationId;

    private String wipEntityId;

    private String itemId;

    private String itemNo;

    private String wkpNo;

    private String mtrLotNo;

    private Long issuedQty;

    private String lockStatus;

    private String lockMode;

    private String processStatus;

    private String processResult;

    private String sourceCode;

    private String crtUser;

    private Date crtDate;

    private String updUser;

    private Date updDate;

    public WipReqLotProcessEntity(WipReqLotProcessRepository wipReqLotProcessRepository, WipReqLotIssuedService wipReqLotIssuedService) {
        this.wipReqLotProcessRepository = wipReqLotProcessRepository;
        this.wipReqLotIssuedService = wipReqLotIssuedService;
    }

    public void create(WipReqLotProcessVO vo) {
        this.setId(UUIDUtils.get32UUID())
                .setProcessStatus(ProcessingStatusEnum.PENDING.getCode());
        EntityUtils.writeStdCrtInfoToEntity(this, Optional.ofNullable(vo.getOptUser()).orElse(EntityUtils.getWipUserId()));
        wipReqLotProcessRepository.insert(this);
    }

    public WipReqLotIssuedEntity process(boolean headerExists, boolean lotExists) {
        WipReqLotIssuedEntity lotIssuedEntity = null;
        if (!headerExists) {
            return null;
        }
        String errMsg = null;
        if (lotExists) {
            this.setProcessStatus(ProcessingStatusEnum.EXCEPTION.getCode());
            errMsg = "批次已被锁定";
        } else {
            lotIssuedEntity = new WipReqLotIssuedEntity();
            BeanUtils.copyProperties(this, lotIssuedEntity);
            lotIssuedEntity.setId(null)
                    .setHeaderId(this.getWipEntityId())
                    .setLockStatus(Optional.ofNullable(this.getLockStatus()).orElse(YoNEnum.Y.getCode()))
                    // 设置锁定类型为手动
                    .setLockType(LotIssuedLockTypeEnum.MANUAL.getCode());
            try {
                wipReqLotIssuedService.save(lotIssuedEntity);
                this.setProcessStatus(ProcessingStatusEnum.SOLVED.getCode());
            } catch (RuntimeException re) {
                errMsg = re.getMessage();
                this.setProcessStatus(ProcessingStatusEnum.EXCEPTION.getCode());
            }
        }
        this.setProcessResult(errMsg);
        EntityUtils.writeStdUpdInfoToEntity(this, EntityUtils.getWipUserId());
        wipReqLotProcessRepository.updateSelectiveById(this);
        return lotIssuedEntity;
    }

    /**
     * 最简单的得到自己新的实例
     * 复杂的请使用Factory
     */
    public static WipReqLotProcessEntity get() {
        return DomainFactory.get(WipReqLotProcessEntity.class);
    }

}
