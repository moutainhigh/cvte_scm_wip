package com.cvte.scm.wip.domain.core.requirement.service;

import cn.hutool.core.util.ArrayUtil;
import com.cvte.csb.base.commons.OperatingUser;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.common.enums.AutoOperationIdentityEnum;
import com.cvte.scm.wip.common.enums.BooleanEnum;
import com.cvte.scm.wip.common.enums.ExecutionModeEnum;
import com.cvte.scm.wip.common.utils.CodeableEnumUtils;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqInterfaceEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqInterfaceRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedModeEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ProcessingStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.cvte.csb.toolkit.CollectionUtils.isEmpty;
import static com.cvte.csb.toolkit.StringUtils.format;
import static com.cvte.csb.toolkit.StringUtils.isNotEmpty;
import static com.cvte.scm.wip.common.enums.ExecutionModeEnum.SLOPPY;
import static com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ProcessingStatusEnum.PENDING;

/**
 * 投料单接口表服务实现类
 *
 * @author jf
 * @see "https://kb.cvte.com/pages/viewpage.action?pageId=168278828"
 * @since 2019-12-30
 */
@Service
@Slf4j
@Transactional(transactionManager = "pgTransactionManager")
public class WipReqInterfaceService {

    private static final Function<String, String> logFormat = s -> isNotEmpty(s) ? format("[scm-wip][interface] {}", s) : s;

    private WipReqLineService wipReqLinesService;

    private WipReqInterfaceRepository wipReqInterfaceRepository;

    public WipReqInterfaceService(WipReqLineService wipReqLinesService,
                                  WipReqInterfaceRepository wipReqInterfaceRepository) {
        this.wipReqLinesService = wipReqLinesService;
        this.wipReqInterfaceRepository = wipReqInterfaceRepository;
    }

    /**
     * 将指定ID的投料单接口数据状态修改为"不处理" {@link ProcessingStatusEnum#UNHANDLED}。
     */
    public void invalid(String... interfaceInIds) {
        updateStatus(Arrays.asList(interfaceInIds), new WipReqInterfaceEntity().setProceed(ProcessingStatusEnum.UNHANDLED.getCode()), EntityUtils.getWipUserId());
    }

    /**
     * 获取投料命令待确认列表数量
     */
    public int count(String headerId) {
        return wipReqInterfaceRepository.getNotSolvedNumber(headerId, BooleanEnum.YES.getCode());
    }

    /**
     * 执行接口表中所有未处理的数据
     */
    public void executeOmissionData() {
        OperatingUser user = new OperatingUser();
        user.setId("SCM-TRANSIT");
        List<WipReqInterfaceEntity> omissionInterfaceList = wipReqInterfaceRepository.selectOmissionData(PENDING.getCode());
        Map<String, List<WipReqInterfaceEntity>> omissionInterfaceMap = omissionInterfaceList.stream().collect(Collectors.groupingBy(WipReqInterfaceEntity::getGroupId));
        for (Map.Entry<String, List<WipReqInterfaceEntity>> entry : omissionInterfaceMap.entrySet()) {
            List<WipReqInterfaceEntity> groupOmissionInterfaceList = entry.getValue();
            scheduleChangedRequest(groupOmissionInterfaceList, SLOPPY, ChangedModeEnum.AUTOMATIC);
        }
    }

    /**
     * 根据接口主键ID，查询指定的投料单接口数据，并执行相应的变更操作。{@param interfaceInIds}与查询的数据接口ID不一致，就抛出异常。
     */
    public void changeByInterfaceIdIds(ExecutionModeEnum eMode, ChangedModeEnum cMode, String... interfaceInIds) {
        Function<Object[], List<WipReqInterfaceEntity>> selectByColumn;
        if (cMode == ChangedModeEnum.AUTOMATIC) {
            selectByColumn = ids -> wipReqInterfaceRepository.selectByInterfaceInIds((String[]) ids, PENDING.getCode());
        } else {
            selectByColumn = ids -> wipReqInterfaceRepository.selectByNeedConfirm((String[]) ids, BooleanEnum.YES.getCode());
        }
        changeByColumn(selectByColumn, eMode, cMode, interfaceInIds, WipReqInterfaceEntity::getInterfaceInId);
    }

    /**
     * 根据群ID，查询指定的投料单接口数据，并执行相应的变更操作。{@param interfaceInIds}与查询数据中群ID不一致，就抛出异常。
     */
    public void changeByGroupIds(ExecutionModeEnum eMode, ChangedModeEnum cMode, String... groupIds) {
        Function<Object[], List<WipReqInterfaceEntity>> selectByColumn = ids -> wipReqInterfaceRepository.selectByGroupIds((String[]) ids, PENDING.getCode());
        changeByColumn(selectByColumn, eMode, cMode, groupIds, WipReqInterfaceEntity::getGroupId);
    }

    /**
     * 对指定字段的数据执行变更操作。{@param columns}与查询数据中字段值不一致，就抛出异常。
     *
     * @param getColumn      获取实体类字段的方法{@link Function}
     * @param columns        指定字段值数组
     * @param selectByColumn 根据指定字段值查询数据的方法{@link Function}
     * @param eMode          执行模式，{@link ExecutionModeEnum#STRICT}出现错误直接抛出异常，{@link ExecutionModeEnum#SLOPPY}忽略出现的错误
     */
    private void changeByColumn(Function<Object[], List<WipReqInterfaceEntity>> selectByColumn, ExecutionModeEnum eMode,
                                ChangedModeEnum cMode, Object[] columns, Function<WipReqInterfaceEntity, Object> getColumn) {
        if (ArrayUtil.isEmpty(columns)) {
            throw new ParamsIncorrectException("数据为空！");
        }
        List<WipReqInterfaceEntity> wipReqInterfaceList = selectByColumn.apply(columns);
        Set<Object> columnSet = wipReqInterfaceList.stream().map(getColumn).collect(Collectors.toSet());
        Object[] errorColumns = Arrays.stream(columns).filter(id -> !columnSet.contains(id)).toArray(Object[]::new);
        if (errorColumns.length > 0) {
            log.error(logFormat.apply(format("变更失败，存在错误数据。错误数据 = {}；", Arrays.toString(errorColumns))));
            throw new ParamsIncorrectException("变更失败，存在错误数据；");
        }
        String[] errorIds = wipReqInterfaceList.stream().filter(in -> CodeableEnumUtils.inValid(in.getChangeType(), ChangedTypeEnum.class))
                .map(WipReqInterfaceEntity::getInterfaceInId).toArray(String[]::new);
        if (errorIds.length > 0) {
            log.error(logFormat.apply(format("变更类型出现错误，接口ID = {}；", Arrays.toString(errorIds))));
            throw new ParamsIncorrectException("糟糕，系统出现了未知错误，速联系相关人员；");
        }
        scheduleChangedRequest(wipReqInterfaceList, eMode, cMode);
    }

    /**
     * 根据变更类型调度指定变更操作。如果变更类型与{@link ChangedTypeEnum}的code不一致，则直接抛出异常。
     *
     * @param wipReqInterfaceList 接口数据
     * @param eMode               执行模式。严格模式，遇到错误直接抛出异常；松散模式，遇到错误，剔除错误数据继续执行。
     */
    private void scheduleChangedRequest(List<WipReqInterfaceEntity> wipReqInterfaceList, ExecutionModeEnum eMode, ChangedModeEnum cMode) {
        if (isEmpty(wipReqInterfaceList)) {
            return;
        }
        /* 降序执行变更请求 */
        wipReqInterfaceList.sort((i1, i2) -> i2.getPriority() - i1.getPriority());
        /* 处理接口表数据的变更类型问题 */
        processChangeType(wipReqInterfaceList, eMode);
        /* groupingBy不能处理key为null的数据，如果前面的变更类型校验被删除，这部分必须转换为EnumMap。*/
        Map<ChangedTypeEnum, List<WipReqInterfaceEntity>> wipReqInterfaceMap = wipReqInterfaceList.stream()
                .collect(Collectors.groupingBy(in -> CodeableEnumUtils.getCodeableEnumByCode(in.getChangeType(), ChangedTypeEnum.class)));
        /* 根据变更类型调度不同的变更执行器，并执行变更操作 */
        for (Map.Entry<ChangedTypeEnum, List<WipReqInterfaceEntity>> entry : wipReqInterfaceMap.entrySet()) {
            change(entry.getValue(), getChangedExecutor(entry.getKey(), eMode, cMode));
        }
    }

    /**
     * 处理接口表数据的变更类型逻辑，如果变更不符合，执行模式为严格，直接报错，执行模式为松散，则删除此错误数据。
     */
    private void processChangeType(List<WipReqInterfaceEntity> wipReqInterfaceList, ExecutionModeEnum eMode) {
        Iterator<WipReqInterfaceEntity> iter = wipReqInterfaceList.iterator();
        List<String> invalidData = new LinkedList<>();
        while (iter.hasNext()) {
            WipReqInterfaceEntity wipReqInterface = iter.next();
            ChangedTypeEnum cType = CodeableEnumUtils.getCodeableEnumByCode(wipReqInterface.getChangeType(), ChangedTypeEnum.class);
            if (cType == null) {
                if (eMode == ExecutionModeEnum.STRICT) {
                    log.error(format("[scm-wip][interface] 接口表出现了错误的变更类型：id = {}；", wipReqInterface.getInterfaceInId()));
                    throw new ParamsIncorrectException("抱歉，系统出现未知错误，速联系相关人员。");
                }
                invalidData.add(wipReqInterface.getInterfaceInId());
                iter.remove();
            }
        }
        if (invalidData.size() > 0) {
            log.error(format("[scm-wip][interface] 接口表出现了错误的变更类型：id = {}。", invalidData));
        }
    }

    /**
     * 根据变更类型，获取指定的变更执行器。
     *
     * @param type  变更类型
     * @param eMode 执行模式
     * @return 变更执行器的消费对象
     */
    private Function<List<WipReqInterfaceEntity>, String[]> getChangedExecutor(ChangedTypeEnum type, ExecutionModeEnum eMode, ChangedModeEnum cMode) {
        Function<List<WipReqInterfaceEntity>, List<WipReqLineEntity>> to = in -> in.stream().map(this::toWipReqLines).collect(Collectors.toList());
        switch (type) {
            case ADD:
                return ins -> wipReqLinesService.addMany(to.apply(ins), eMode, cMode, true, AutoOperationIdentityEnum.WIP.getCode());
            case DELETE:
                return ins -> wipReqLinesService.cancelledByConditions(to.apply(ins), eMode, cMode, true, AutoOperationIdentityEnum.WIP.getCode());
            case UPDATE:
                return ins -> wipReqLinesService.update(to.apply(ins), eMode, cMode, true, AutoOperationIdentityEnum.WIP.getCode());
            case REPLACE:
                return ins -> wipReqLinesService.replace(to.apply(ins), eMode, cMode, true, AutoOperationIdentityEnum.WIP.getCode());
            default:
                log.error(logFormat.apply(format("[scm-wip][interface] 变更类型出现异常数据：{}({})；", type.getCode(), type.getDesc())));
                throw new ParamsIncorrectException("糟糕，系统出现了未知错误，速联系相关人员；");
        }
    }

    /**
     * 变更的核心函数，执行变更操作、记录错误信息，并将执行情况回写接口表
     * <p>
     * 执行模式为{@link ExecutionModeEnum#SLOPPY}，变更出现错误数据时，直接忽略错误数据，继续执行正确数据。
     * 执行模式为{@link ExecutionModeEnum#STRICT}，变更出现错误数据时，直接抛出错误信息。
     *
     * @param wipReqInterfaceList 接口数据
     * @param changedExecutor     新增、删除、修改以及替换的执行器，将执行函数作为一等公民，间接实现策略模式。
     */
    private void change(List<WipReqInterfaceEntity> wipReqInterfaceList, Function<List<WipReqInterfaceEntity>, String[]> changedExecutor) {
        if (isEmpty(wipReqInterfaceList)) {
            return;
        }
        List<String> solvedIdList = new ArrayList<>();
        List<WipReqInterfaceEntity> exceptionInterfaceList = new ArrayList<>();
        String[] errorMessages = changedExecutor.apply(wipReqInterfaceList);
        for (int i = errorMessages.length - 1; i >= 0; --i) {
            String errorMsg = errorMessages[i];
            if (StringUtils.isEmpty(errorMsg)) {
                solvedIdList.add(wipReqInterfaceList.get(i).getInterfaceInId());
            } else {
                exceptionInterfaceList.add(wipReqInterfaceList.get(i).setExceptionReason(errorMsg));
            }
        }
        updateStatus(solvedIdList, new WipReqInterfaceEntity().setProceed(ProcessingStatusEnum.SOLVED.getCode()).setExceptionReason(""), AutoOperationIdentityEnum.WIP.getCode());
        exceptionInterfaceList.forEach(in -> EntityUtils.writeStdUpdInfoToEntity(in.setProceed(ProcessingStatusEnum.EXCEPTION.getCode()), AutoOperationIdentityEnum.WIP.getCode()));
        wipReqInterfaceRepository.batchUpdate(exceptionInterfaceList);
    }

    /**
     * 将接口数据转换为行数据。
     */
    private WipReqLineEntity toWipReqLines(WipReqInterfaceEntity wipReqInterface) {
        Function<Integer, Integer> getIfValid = value -> Optional.ofNullable(value).filter(v -> v >= 0).orElse(null);
        return new WipReqLineEntity().setLineId(isNotEmpty(wipReqInterface.getLineId()) ? wipReqInterface.getLineId() : null)
                .setHeaderId(wipReqInterface.getHeaderId()).setOrganizationId(wipReqInterface.getOrganizationId())
                .setLotNumber(wipReqInterface.getLotNumber()).setWkpNo(wipReqInterface.getWkpNo()).setPosNo(wipReqInterface.getPosNo())
                .setItemNo(Optional.ofNullable(wipReqInterface.getItemNoNew()).orElse(wipReqInterface.getItemNo()))
                .setBeforeItemNo(wipReqInterface.getItemNo()).setSourceCode(wipReqInterface.getSourceCode())
                .setReqQty(getIfValid.apply(wipReqInterface.getItemQty())).setUnitQty(wipReqInterface.getUnitQty())
                .setIssuedQty(wipReqInterface.getIssuedQty());
    }

    /**
     * 修改投料单接口的处理状态
     */
    private void updateStatus(List<String> ids, WipReqInterfaceEntity in, String userId) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        EntityUtils.writeStdUpdInfoToEntity(in, userId);
        wipReqInterfaceRepository.updateByIdSelective(in, ids);
    }
}