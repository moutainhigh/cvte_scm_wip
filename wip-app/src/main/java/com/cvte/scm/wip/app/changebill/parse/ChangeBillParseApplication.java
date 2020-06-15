package com.cvte.scm.wip.app.changebill.parse;

import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.base.domain.Application;
import com.cvte.scm.wip.common.enums.error.ReqInsErrEnum;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;
import com.cvte.scm.wip.domain.core.changebill.service.SourceChangeBillService;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillBuildVO;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillQueryVO;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeReqVO;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.service.CheckReqInsDomainService;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqHeaderService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsBuildVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
  *
  * @author  : xueyuting
  * @since    : 2020/5/21 17:11
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Component
public class ChangeBillParseApplication implements Application<ChangeBillQueryVO, String> {

    private SourceChangeBillService sourceChangeBillService;
    private WipReqHeaderService reqHeaderService;
    private CheckReqInsDomainService checkReqInsDomainService;

    public ChangeBillParseApplication(SourceChangeBillService sourceChangeBillService, WipReqHeaderService reqHeaderService, CheckReqInsDomainService checkReqInsDomainService) {
        this.sourceChangeBillService = sourceChangeBillService;
        this.reqHeaderService = reqHeaderService;
        this.checkReqInsDomainService = checkReqInsDomainService;
    }

    @Override
    public String doAction(ChangeBillQueryVO queryVO) {

        if (StringUtils.isBlank(queryVO.getOrganizationId())) {
            throw new ParamsIncorrectException("组织ID不可为空");
        }

        List<ChangeBillBuildVO> changeBillBuildVOList;

        // 接口和定时任务可能同时调用, 为避免并发引起的数据问题, 按业务组织加锁。 intern将字符串写入常量池, 保证每次拿到的是业务组织的同一个内存对象
        synchronized (queryVO.getOrganizationId().intern()) {
            // 获取EBS更改单
            changeBillBuildVOList = sourceChangeBillService.querySourceChangeBill(queryVO);

            if (ListUtil.empty(changeBillBuildVOList)) {
                return "";
            }

            for (ChangeBillBuildVO changeBillBuildVO : changeBillBuildVOList) {
                ChangeBillEntity billEntity = null;
                try {
                    // 生成更改单
                    billEntity = ChangeBillEntity.get().completeChangeBill(changeBillBuildVO);
                    // 生成指令
                    this.parseChangeBill(billEntity);
                } catch (RuntimeException re) {
                    if (Objects.isNull(billEntity)) {
                        billEntity = ChangeBillEntity.get();
                        billEntity.setBillId(changeBillBuildVO.getBillId())
                                .setBillNo(changeBillBuildVO.getBillNo())
                                .setMoId(changeBillBuildVO.getMoId());
                    }
                    billEntity.notifyEntity(re.getMessage());
                }
            }
        }

        return changeBillBuildVOList.stream().map(ChangeBillBuildVO::getBillNo).collect(Collectors.joining(","));
    }

    @Transactional("pgTransactionManager")
    void parseChangeBill(ChangeBillEntity billEntity) {
        // 获取目标投料单
        WipReqHeaderEntity reqHeaderEntity = reqHeaderService.getBySourceId(billEntity.getMoId());
        if (Objects.isNull(reqHeaderEntity)) {
            throw new ServerException(ReqInsErrEnum.TARGET_REQ_INVALID.getCode(), ReqInsErrEnum.TARGET_REQ_INVALID.getDesc());
        }
        ChangeReqVO reqVO = ChangeReqVO.buildVO(reqHeaderEntity);

        // 解析更改单
        ReqInsBuildVO instructionBuildVO = billEntity.parseChangeBill(reqVO);

        // 校验对应指令是否已执行或作废
        boolean insProcessed = checkReqInsDomainService.checkInsProcessed(instructionBuildVO);
        if (insProcessed) {
            throw new ServerException(ReqInsErrEnum.INS_IMMUTABLE.getCode(), String.format("%s,指令ID=%s", ReqInsErrEnum.INS_IMMUTABLE.getDesc(), instructionBuildVO.getInsHeaderId()));
        }
        // 生成投料单指令
        ReqInsEntity.get().completeInstruction(instructionBuildVO);
    }

}
