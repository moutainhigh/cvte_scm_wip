package com.cvte.scm.wip.domain.core.rtc.service;

import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.constants.CommonDimensionConstant;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.utils.CodeableEnumUtils;
import com.cvte.scm.wip.common.utils.DateUtils;
import com.cvte.scm.wip.domain.common.user.service.UserService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.BillStatusEnum;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcPostLimitEntity;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcHeaderRepository;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcPostLimitRepository;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcHeaderTypeEnum;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcLineStatusEnum;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/9 11:26
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Component
public class CheckMtrRtcHeaderService {

    private WipMtrRtcHeaderRepository wipMtrRtcHeaderRepository;
    private UserService userService;
    private WipMtrRtcPostLimitRepository wipMtrRtcPostLimitRepository;

    public CheckMtrRtcHeaderService(WipMtrRtcHeaderRepository wipMtrRtcHeaderRepository, UserService userService, WipMtrRtcPostLimitRepository wipMtrRtcPostLimitRepository) {
        this.wipMtrRtcHeaderRepository = wipMtrRtcHeaderRepository;
        this.userService = userService;
        this.wipMtrRtcPostLimitRepository = wipMtrRtcPostLimitRepository;
    }

    public void checkBillStatus(String status) {
        BillStatusEnum statusEnum = CodeableEnumUtils.getCodeableEnumByCode(status, BillStatusEnum.class);
        if (Objects.isNull(statusEnum)) {
            throw new ParamsIncorrectException("非法工单状态");
        }
        switch (statusEnum) {
            case CLOSED:
            case CANCELLED:
            case FINISH:
                throw new ParamsIncorrectException("工单" + statusEnum.getDesc());
            default:
        }
    }

    public void checkBillQtyLower(BigDecimal billQty) {
        if (Objects.isNull(billQty) || billQty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ParamsIncorrectException("领料套数必须大于0");
        }
    }

    public void checkBillQtyUpper(BigDecimal billQty, BigDecimal originQty) {
        if (Objects.nonNull(billQty) && billQty.compareTo(originQty) > 0) {
            throw new ParamsIncorrectException("领料套数不能超过工单数量");
        }
    }

    public void checkReqFinished(WipMtrRtcHeaderEntity rtcHeader) {
        if (CollectionUtils.isEmpty(rtcHeader.getLineList().stream().map(WipMtrRtcLineEntity::getLineStatus).filter(WipMtrRtcLineStatusEnum.getUnPostStatus()::contains).collect(Collectors.toSet()))) {
            // 有效行为空
            String errMsg;
            WipMtrRtcHeaderTypeEnum typeEnum = CodeableEnumUtils.getCodeableEnumByCode(rtcHeader.getBillType(), WipMtrRtcHeaderTypeEnum.class);

            List<WipMtrRtcHeaderEntity> unPostRtcHeaderList = wipMtrRtcHeaderRepository.selectUnPost(rtcHeader);
            if (ListUtil.notEmpty(unPostRtcHeaderList)) {
                String billNoStr = unPostRtcHeaderList.stream().map(WipMtrRtcHeaderEntity::getBillNo).collect(Collectors.joining(","));
                errMsg = "工单没有物料可%s,存在未过账的单据:" + billNoStr;
            } else {
                if (StringUtils.isNotBlank(rtcHeader.getWkpNo())) {
                    errMsg = "所选工序物料已%s完成";
                } else {
                    errMsg = "工单物料已%s完成";
                }
            }
            errMsg = String.format(errMsg, Optional.ofNullable(typeEnum).orElse(WipMtrRtcHeaderTypeEnum.RECEIVE).getDesc());
            throw new ParamsIncorrectException(errMsg);
        }
    }

    /**
     * 校验单据是否被限制过账
     * @since 2020/10/23 5:41 下午
     * @author xueyuting
     */
    public void checkPostLimit(WipMtrRtcHeaderEntity rtcHeader) {
        String userDefaultDimensionId = userService.getDefaultDimensionByUserId(CurrentContext.getCurrentOperatingUser().getId());
        if (!CommonDimensionConstant.GC.equals(userDefaultDimensionId)) {
            // 非工厂用户不需要校验
            return;
        }

        WipMtrRtcPostLimitEntity queryRtcPostLimit = new WipMtrRtcPostLimitEntity();
        queryRtcPostLimit.setFactoryId(rtcHeader.getFactoryId())
                .setStatus(StatusEnum.NORMAL.getCode());
        WipMtrRtcPostLimitEntity rtcPostLimit = wipMtrRtcPostLimitRepository.selectOne(queryRtcPostLimit);
        if (Objects.isNull(rtcPostLimit)) {
            // 没有配置的工厂不需要校验
            return;
        }

        // 根据工厂过账时间限制的配置, 获取限制的时间点
        Date dateBeforeEndOfMonth = DateUtils.getBeforeEndOfMonth(rtcPostLimit.getLimitDay(), rtcPostLimit.getLimitHour());
        Date now = new Date();
        if (now.after(dateBeforeEndOfMonth)) {
            throw new ParamsIncorrectException(String.format("在月末最后%d天的%d点之后，不允许工厂做过账操作", rtcPostLimit.getLimitDay(), rtcPostLimit.getLimitHour()));
        }
    }

}
