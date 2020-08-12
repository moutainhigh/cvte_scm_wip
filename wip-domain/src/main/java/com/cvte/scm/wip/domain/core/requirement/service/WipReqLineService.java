package com.cvte.scm.wip.domain.core.requirement.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.enums.ExecutionModeEnum;
import com.cvte.scm.wip.common.utils.CodeableEnumUtils;
import com.cvte.scm.wip.common.utils.CurrentContextUtils;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.common.utils.ValidateUtils;
import com.cvte.scm.wip.domain.core.item.service.ScmItemService;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.XxwipMoInterfaceEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqHeaderRepository;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLineRepository;
import com.cvte.scm.wip.domain.core.requirement.repository.XxwipMoInterfaceRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.BillStatusEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedModeEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.OperationSeqNumEnum;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static com.cvte.csb.toolkit.CollectionUtils.isEmpty;
import static com.cvte.csb.toolkit.StringUtils.format;
import static com.cvte.csb.toolkit.StringUtils.isNotEmpty;
import static com.cvte.csb.toolkit.UUIDUtils.getUUID;
import static com.cvte.scm.wip.common.utils.EntityUtils.getWipUserId;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 20:40
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipReqLineService {

    private static final BiFunction<String, ChangedTypeEnum, String> logFormat = (errorMsg, type) ->
            isNotEmpty(errorMsg) ? format("[scm-wip][line][{}] {}", type.getCode(), errorMsg) : errorMsg;

    private static final String DEFAULT_SOURCE_CODE = "SCM-WIP";

    /**
     * 一个位标识，用于获取枚举 {@link BillStatusEnum} 中 "草稿和已确定" 的 code 值列表。
     */
    private static final int DRAFT_CONFIRMED = 3;

    /**
     * 一个位标识，用于获取枚举 {@link BillStatusEnum} 中 "草稿、已确定、已备料和已领料" 的 code 值列表。
     */
    private static final int DRAFT_CONFIRMED_PREPARED_ISSUED = 15;

    private ScmItemService scmItemService;
    private WipReqLineRepository wipReqLineRepository;
    private WipReqLogService wipReqLogService;
    private WipReqHeaderRepository wipReqHeaderRepository;
    private WipReqPrintLogService wipReqPrintLogService;
    private XxwipMoInterfaceRepository xxwipMoInterfaceRepository;
    private WipReqLineSplitService wipReqLineSplitService;

    public WipReqLineService(ScmItemService scmItemService, WipReqLineRepository wipReqLineRepository, WipReqLogService wipReqLogService, WipReqHeaderRepository wipReqHeaderRepository, WipReqPrintLogService wipReqPrintLogService, XxwipMoInterfaceRepository xxwipMoInterfaceRepository, WipReqLineSplitService wipReqLineSplitService) {
        this.scmItemService = scmItemService;
        this.wipReqLineRepository = wipReqLineRepository;
        this.wipReqLogService = wipReqLogService;
        this.wipReqHeaderRepository = wipReqHeaderRepository;
        this.wipReqPrintLogService = wipReqPrintLogService;
        this.xxwipMoInterfaceRepository = xxwipMoInterfaceRepository;
        this.wipReqLineSplitService = wipReqLineSplitService;
    }


    /**
     * 添加一条投料单行数据。详情可参见KB文档：https://kb.cvte.com/pages/viewpage.action?pageId=168289967
     */
    public String addOne(WipReqLineEntity wipReqLine, ExecutionModeEnum eMode, ChangedModeEnum cMode, boolean isLog) {
        return addMany(singletonList(wipReqLine), eMode, cMode, isLog, getWipUserId())[0];
    }

    /**
     * 添加多条投料单行数据。详情可参见KB文档：https://kb.cvte.com/pages/viewpage.action?pageId=168289967
     */
    public String[] addMany(List<WipReqLineEntity> wipReqLineList, ExecutionModeEnum eMode, ChangedModeEnum cMode, boolean isLog, String userId) {
        Function<List<WipReqLineEntity>, String[]> validateAndGetData = getValidator(
                wipReqLineList, ChangedTypeEnum.ADD, this::validateAndGetAddedData);
        Consumer<WipReqLineEntity> complete = line -> completeAddedData(line, userId);
        Consumer<WipReqLineEntity> manipulate = wipReqLineRepository::insertSelective;
        ChangedParameters parameters = new ChangedParameters().setType(ChangedTypeEnum.ADD).setLog(isLog).setComplete(complete)
                .setValidateAndGetData(validateAndGetData).setManipulate(manipulate).setEMode(eMode).setCMode(cMode);
        return change(parameters);
    }

    /**
     * 校验需要被添加的投料单行数据，返回错误信息，并将待添加的投料单行数据插入到参数 {@param addedData} 中。
     * <p>
     * 校验组织和物料编码时，顺便将物料ID插入参数 {@param wipReqLine} 中 (不优雅，但胜在实用😂)。
     */
    private String validateAndGetAddedData(WipReqLineEntity wipReqLine, List<WipReqLineEntity> addedData) {
        String errorMsg, itemId;
        if (isNotEmpty(errorMsg = validateIndex(wipReqLine.setLineStatus(BillStatusEnum.CONFIRMED.getCode())))) {
            return format("您添加的数据有点小问题，{}", errorMsg);
        } else if (CodeableEnumUtils.inValid(wipReqLine.getWkpNo(), OperationSeqNumEnum.class)) {
            return "添加失败，您填写的工序号错误，请您修改无误后再添加；";
        } else if (CollectionUtils.isNotEmpty(wipReqLineRepository.selectByExample(wipReqLineRepository.createCustomExample(wipReqLine)))) {
            return "抱歉，您添加的投料单行已存在；";
        } else if (StringUtils.isEmpty(itemId = scmItemService.getItemId(wipReqLine.getOrganizationId(), wipReqLine.getItemNo()))) {
            return "添加失败，您填写的组织或物料编码错误，请您修改无误后再添加；";
        } else if (StringUtils.isEmpty(wipReqHeaderRepository.getSourceId(wipReqLine.getHeaderId()))) {
            return "添加失败，您填写的投料单头ID错误，请您修改无误后再添加；";
        } else if (!wipReqHeaderRepository.existLotNumber(wipReqLine.getHeaderId(), wipReqLine.getLotNumber())) {
            return "添加失败，您填写的批次号错误，请您修改无误后再添加；";
        }
        addedData.add(wipReqLine.setItemId(itemId));
        return "";
    }

    private String[] validateManualLimitItem(List<WipReqLineEntity> reqLineList, ChangedTypeEnum type) {
        String[] errorMsg = new String[]{};
        String organizationId = null;
        for (WipReqLineEntity line : reqLineList) {
            if (StringUtils.isNotBlank(line.getOrganizationId())) {
                organizationId = line.getOrganizationId();
                break;
            }
        }
        List<String> itemNoList = reqLineList.stream().map(WipReqLineEntity::getItemNo).distinct().collect(toList());
        List<String> outRangeItemNoList = new ArrayList<>();
        List<String> limitItemClassList = wipReqLineRepository.selectOutRangeItemList(type.getCode(), organizationId, itemNoList, outRangeItemNoList);
        if (ListUtil.notEmpty(outRangeItemNoList)) {
            String msg = String.format("只有【%s】类物料允许%s", String.join("、", limitItemClassList), type.getDesc());
            errorMsg = new String[]{msg};
        }
        return errorMsg;
    }

    /**
     * 补全被添加投料单行数据的字段信息，诸如主键、状态等。
     */
    private void completeAddedData(WipReqLineEntity wipReqLine, String userId) {
        // 对于被替换或者更新的数据，若原始行ID存在，则直接使用已有的即可
        wipReqLine.setLineId(getUUID()).setEnableDate(new Date())
                .setOriginalLineId(ofNullable(wipReqLine.getOriginalLineId()).orElse(wipReqLine.getLineId()));
        EntityUtils.writeStdCrtInfoToEntity(wipReqLine, userId);
    }

    /**
     * 根据指定的字段条件取消指定投料单，其字段必须为索引信息。
     * <p>
     * 详情可参见KB文档：https://kb.cvte.com/pages/viewpage.action?pageId=167105981
     */
    public String[] cancelledByConditions(List<WipReqLineEntity> wipReqLineList, ExecutionModeEnum eMode, ChangedModeEnum cMode, boolean isLog, String userId) {
        Function<List<WipReqLineEntity>, String[]> validateAndGetData = getValidator(wipReqLineList, ChangedTypeEnum.DELETE, this::validateAndGetCancelledData);
        return cancel(validateAndGetData, eMode, cMode, isLog, userId);
    }

    /**
     * 校验需要被取消的投料单行数据，返回错误信息，并将待取消的投料单行数据添加到参数 {@param cancelledData} 中。
     * <p>
     * 校验期间更新待取消投料单行数据中的来源编号。
     */
    private String validateAndGetCancelledData(WipReqLineEntity wipReqLine, List<WipReqLineEntity> cancelledData) {
        Example example = wipReqLineRepository.createCustomExample(wipReqLine);
        List<WipReqLineEntity> wipReqLines;
        if (isNull(example) || isEmpty(wipReqLines = wipReqLineRepository.selectByExample(example))) {
            log.error(logFormat.apply(format("根据删除条件无法查询待删除投料单行，条件 = [{}]；", EntityUtils.getEntityPrintInfo(wipReqLine)), ChangedTypeEnum.DELETE));
            return "删除失败，请您检查删除条件是否正确；";
        }
        wipReqLines.forEach(line -> line.setSourceCode(wipReqLine.getSourceCode()).setGroupId(wipReqLine.getGroupId()));
        cancelledData.addAll(wipReqLines);
        return "";
    }

    /**
     * 根据投料单行ID取消指定投料单行数据。
     * <p>
     * 详情可参见KB文档：https://kb.cvte.com/pages/viewpage.action?pageId=167105981
     */
    public String[] cancelledByLineIds(ExecutionModeEnum eMode, ChangedModeEnum cMode, boolean isLog, String... lineIds) {
        Function<List<WipReqLineEntity>, String[]> validateAndGetData = lines -> new String[]{validateAndGetCancelledData(lineIds, lines)};
        return cancel(validateAndGetData, eMode, cMode, isLog, getWipUserId());
    }

    /**
     * 取消投料单行数据的帮助方法。
     */
    private String[] cancel(Function<List<WipReqLineEntity>, String[]> validateAndGetData, ExecutionModeEnum eMode, ChangedModeEnum cMode, boolean isLog, String userId) {
        Consumer<WipReqLineEntity> complete = line -> completeCancelledData(line, userId);
        Consumer<WipReqLineEntity> manipulate = wipReqLineRepository::updateSelectiveById;
        ChangedParameters parameters = new ChangedParameters().setType(ChangedTypeEnum.DELETE).setLog(isLog).setComplete(complete)
                .setValidateAndGetData(validateAndGetData).setManipulate(manipulate).setEMode(eMode).setCMode(cMode);
        return change(parameters);
    }

    /**
     * 校验投料单行ID数组 {@param lineIds} ，并将校验正确的待删除数据添加到参数 {@param cancelledData} 中，最后返回校验错误的信息。
     * <p>
     * 直接根据参数 {@param lineIds} 获取指定的投料单行数据，接着将查询数据的行ID转换为 {@link Set}，最后利用其寻找
     * 数据库中不存在的 line_id，并返回错误信息。
     */
    private String validateAndGetCancelledData(String[] lineIds, List<WipReqLineEntity> cancelledData) {
        if (ArrayUtil.isEmpty(lineIds)) {
            return "删除的数据为空；";
        }
        Example example = wipReqLineRepository.createExample();
        example.createCriteria().andIn("lineId", Arrays.asList(lineIds));
        List<WipReqLineEntity> wipReqLineEntities = wipReqLineRepository.selectByExample(example);
        List<WipReqLineEntity> cancelList = wipReqLineEntities.stream().filter(WipReqLineEntity::isProcessing).collect(toList());
        cancelledData.addAll(cancelList);
        if (cancelList.size() != wipReqLineEntities.size()) {
            List<WipReqLineEntity> invalidLineStatusList = wipReqLineEntities.stream()
                    .filter(el -> !el.isProcessing())
                    .collect(Collectors.toList());
            log.error(logFormat.apply(format("待删除的数据中，存在无效的投料单行ID，行ID = {}；",
                    invalidLineStatusList.stream().map(WipReqLineEntity::getLineId).collect(Collectors.joining(","))), ChangedTypeEnum.DELETE));
            BillStatusEnum billStatusEnum = CodeableEnumUtils.getCodeableEnumByCode(invalidLineStatusList.get(0).getLineStatus(), BillStatusEnum.class);

            return billStatusEnum.getDesc() + "的投料行不允许删除；";
        }
        return "";
    }

    /**
     * 补全待取消数据的信息。
     * <p>
     * 1、设置行状态为取消；2、当前行ID设置为行版本，便于错误追溯；3、设置作废时间；4、写入操作用户的信息
     */
    private void completeCancelledData(WipReqLineEntity wipReqLine, String userId) {
        wipReqLine.setLineVersion(wipReqLine.getLineId()).setLineStatus(BillStatusEnum.CANCELLED.getCode()).setDisableDate(new Date());
        EntityUtils.writeStdUpdInfoToEntity(wipReqLine, userId);
    }

    /**
     * 根据投料单行ID备料指定投料单行数据。
     */
    public String preparedByWipReqLines(WipReqLineEntity wipReqLine, ExecutionModeEnum eMode, ChangedModeEnum cMode, boolean isLog) {
        String userId = getWipUserId();
        Function<List<WipReqLineEntity>, String[]> validateAndGetData = lines -> new String[]{validateAndGetPreparedData(wipReqLine, lines)};
        Consumer<WipReqLineEntity> complete = line -> EntityUtils.writeStdUpdInfoToEntity(line.setLineStatus(BillStatusEnum.PREPARED.getCode()), userId);
        Consumer<WipReqLineEntity> manipulate = wipReqLineRepository::updateSelectiveById;
        ChangedParameters parameters = new ChangedParameters().setType(ChangedTypeEnum.UPDATE).setLog(isLog).setComplete(complete)
                .setValidateAndGetData(validateAndGetData).setManipulate(manipulate).setEMode(eMode).setCMode(cMode);
        String errorMessage = Stream.of(change(parameters)).filter(StringUtils::isNotEmpty).findAny().orElse(null);
        if (nonNull(errorMessage)) {
            return errorMessage;
        }
        /* 打印后设置头表数据为已备料 */
        wipReqHeaderRepository.updateStatusById(BillStatusEnum.PREPARED.getCode(), wipReqLine.getHeaderId());
        return EntityUtils.handleErrorMessage(wipReqPrintLogService.add(wipReqLine), eMode);
    }

    /**
     * 根据指定字段条件查询指定投料单行数据，将对应的投料单行数据加载到备料数据中 {@param preparedData}，并返回校验信息。
     */
    @SneakyThrows
    private String validateAndGetPreparedData(WipReqLineEntity line, List<WipReqLineEntity> preparedData) {
        if (isNull(line)) {
            return "备料失败，投料单行查询条件为空；";
        } else if (StringUtils.isEmpty(wipReqHeaderRepository.getSourceId(line.getHeaderId()))) {
            return "备料失败，投料单行查询条件中头ID错误；";
        }
        line.setSourceCode(null);
        List<WipReqLineEntity> reqLineEntityList;
        try {
            reqLineEntityList = wipReqLineRepository.selectByColumnAndStatus(line, DRAFT_CONFIRMED);
        } catch (ParamsIncorrectException pe) {
            return "备料失败，" + pe.getMessage();
        }
        preparedData.addAll(reqLineEntityList);
        return "";
    }

    /**
     * 执行变更投料单行数据的核心方法。如果有错误，则根据具体的执行模式来进行处理。
     */
    private String[] change(ChangedParameters parameters) {
        List<WipReqLineEntity> changedLines = new ArrayList<>();
        String[] errorMessages = handleErrorMessages(parameters.validateAndGetData.apply(changedLines), parameters.eMode);

        if (ChangedModeEnum.MANUAL.equals(parameters.cMode)) {
            // 手工变更限制物料类别
            List<String> errMsgList = Arrays.asList(errorMessages);
            errMsgList.addAll(Arrays.asList(handleErrorMessages(validateManualLimitItem(changedLines, parameters.type), parameters.eMode)));
            errorMessages = errMsgList.toArray(new String[0]);
        }

        // 操作的数据可能重复，避免操作异常，故执行去重操作。
        changedLines = changedLines.stream().distinct().collect(toList());
        ChangedTypeEnum type = parameters.type;
        String groupId = UUIDUtils.getUUID();

        List<XxwipMoInterfaceEntity> moInterfaceList = new ArrayList<>();
        XxwipMoInterfaceEntity moInterface;
        boolean changeFailed = false;
        List<String> errMsgList = new ArrayList<>();
        for (WipReqLineEntity afterLine : changedLines) {
            afterLine.setGroupId(ofNullable(afterLine.getGroupId()).orElse(groupId));
            WipReqLineEntity beforeLine = ofNullable(wipReqLineRepository.selectById(afterLine.getLineId())).orElse(afterLine);
            parameters.complete.accept(afterLine);
            try {
                parameters.manipulate.accept(afterLine);
            } catch (RuntimeException re) {
                changeFailed = true;
                if (!errMsgList.contains(re.getMessage())) {
                    errMsgList.add(re.getMessage());
                }
                afterLine.setExecuteResult(re.getMessage());
                continue;
            }
            if (nonNull(moInterface = createXxwipMoInterface(afterLine, type.getCode()))) {
                moInterfaceList.add(moInterface);
            }
            if (parameters.isLog) {
                wipReqLogService.addWipReqLog(beforeLine, afterLine, type);
                log.info("投料单行表{}成功：行ID = {}; ", type.getDesc(), afterLine.getLineId());
            }
        }
        if (changeFailed) {
            throw new ParamsIncorrectException(type.getDesc() + "失败:" + String.join(",", errMsgList));
        }
        writeChangedDataToEBS(moInterfaceList, groupId);
        return errorMessages;
    }

    /**
     * 将变更数据回写到 EBS 系统。
     */
    private void writeChangedDataToEBS(List<XxwipMoInterfaceEntity> moInterfaceList, String groupId) {
        if (isEmpty(moInterfaceList)) {
            return;
        }
        String wipEntityId = moInterfaceList.get(0).getWipEntityId();
        if (moInterfaceList.stream().anyMatch(s -> !wipEntityId.equals(s.getWipEntityId()))) {
            throw new ParamsIncorrectException("变更数据中不能存在不同的投料单头 ID 。");
        }
        xxwipMoInterfaceRepository.batchInsert(moInterfaceList);
        if (groupId.equals(moInterfaceList.get(0).getGroupId())) {
            String errorMessage = executeProcedure(groupId, wipEntityId);
            if (errorMessage.length() > 0) {
                /* 这里暂时不去手动删除已写入的 XxwipMoInterface 的数据，因为存储过程出错几率较小，这部分脏数据可以不用处理。 */
                throw new ParamsIncorrectException(errorMessage);
            }
        }
        log.info("成功写入 {} 条 WIP 工单接口数据。", moInterfaceList.size());
    }

    /**
     * 调用存储过程重试的次数
     */
    private static final int ATTEMPT_NUMBER = 2;

    /**
     * 调用回写工单信息的存储过程
     */
    private String executeProcedure(String groupId, String wipEntityId) {
        String[] poInfo = EntityUtils.retry(() -> xxwipMoInterfaceRepository.callProcedure(wipEntityId, groupId), ATTEMPT_NUMBER, "调用 XXAPS.XXWIP_INTERFACE_PKG.P_UPDATE_MO_ITEM 存储过程");
        if (poInfo == null || poInfo.length != 2) {
            return "糟糕，储存过程出现了未知错误，速联系相关人员。";
        } else if ("error".equalsIgnoreCase(poInfo[0])) {
            return poInfo[1];
        }
        return "";
    }

    /**
     * 根据投料单信息创建工单接口数据对象
     */
    private XxwipMoInterfaceEntity createXxwipMoInterface(WipReqLineEntity wipReqLine, String opType) {
        String wipEntityId = wipReqHeaderRepository.getSourceId(wipReqLine.getHeaderId());
        /* 因为有些未发放工单的Header_Id可能不存在WipReqHeader表，这部分暂时不写入EBS */
        if (isNull(wipEntityId)) {
            log.error(format("[scm-wip][lines] 未发放工单无 Header_Id ，line_id = {}。", wipReqLine.getLineId()));
            return null;
        }
        XxwipMoInterfaceEntity moInterface = new XxwipMoInterfaceEntity();
        BeanUtil.copyProperties(wipReqLine, moInterface);
        moInterface.setInterfaceId(UUIDUtils.getUUID()).setItemQty(wipReqLine.getReqQty()).setWipEntityId(wipEntityId)
                .setOperationType(opType).setExecuteCode("1");
        EntityUtils.writeStdCrtInfoToEntity(moInterface, wipReqLine.getUpdUser());
        return moInterface;
    }

    /**
     * 通用的校验方法，主要校验数据是否为空、数据格式以及数据来源。
     */
    private String universalValidate(WipReqLineEntity wipReqLine, ChangedTypeEnum type) {
        String errorMsg;
        if (isNull(wipReqLine)) {
            return format("{}的数据为空；", type.getDesc());
        } else if (isNotEmpty(errorMsg = ValidateUtils.validate(wipReqLine))) {
            return format("请您注意数据格式的规范，{}", errorMsg); // Hibernate Validator错误信息结尾带了";"号。
        } else if (StringUtils.isEmpty(CurrentContextUtils.getOrEmptyOperatingUser().getId())) {
            if (StringUtils.isEmpty(wipReqLine.getSourceCode())) {
                return format("{}数据的来源编码为空；", type.getDesc());
            }
            wipReqLine.setSourceCode(DEFAULT_SOURCE_CODE);
        }
        return "";
    }

    /**
     * 校验内部的替换数据，并返回校验错误的信息，将正确的替换数据添加到 {@param replacedData} 。
     */
    private String validateAndGetReplacedData(WipReqLineEntity wipReqLine, List<WipReqLineEntity> replacedData) {
        String beforeItemNo = wipReqLine.getBeforeItemNo(), afterItemNo = wipReqLine.getItemNo(), errorMsg;
        if (StringUtils.isEmpty(beforeItemNo)) {
            return "替换失败，替换前的物料编码为空，请您仔细修改后重试；";
        } else if (StringUtils.isEmpty(afterItemNo)) {
            return "替换失败，替换后的物料编码为空，请您仔细修改后重试；";
        } else if (beforeItemNo.equals(afterItemNo)) {
            return "替换失败，替换前后的物料编码相同，请您仔细修改后重试；";
        } else if (isNotEmpty(errorMsg = validateIndex(wipReqLine))) {
            return format("抱歉，您替换后的数据有点小问题，{}", errorMsg);
        }
        // 直接更新原投料行会导致 投料指令执行时 EBS接口表报 ITEM_ID为空异常
        WipReqLineEntity queryEntity = new WipReqLineEntity();
        BeanUtils.copyProperties(wipReqLine, queryEntity);
        Example example = wipReqLineRepository.createCustomExample(queryEntity.setItemNo(beforeItemNo).setItemId(null));
        if (nonNull(example) && StringUtils.isNotEmpty(wipReqLine.getLineId())) {
            example.getOredCriteria().get(0).andEqualTo("lineId", wipReqLine.getLineId());
        }
        List<WipReqLineEntity> wipReqLines = wipReqLineRepository.selectByExample(example);
        if (isEmpty(wipReqLines)) {
            log.error(logFormat.apply(format("替换条件无法查询待替换的投料单行数据，替换条件 = [{}]；", EntityUtils.getEntityPrintInfo(wipReqLine)), ChangedTypeEnum.REPLACE));
            return "替换失败，请您仔细检查替换条件是否正确；";
        } else if (wipReqLines.size() > 1) {
            log.error(logFormat.apply(format("投料单行数据表出现了脏数据，异常行ID = {}；", Arrays.toString(toLineIds(wipReqLines))), ChangedTypeEnum.REPLACE));
            return "糟糕，系统出现了未知错误，速联系相关人员；";
        }
        WipReqLineEntity dbWipReqLine = wipReqLines.get(0);
        if (!dbWipReqLine.isProcessing()) {
            BillStatusEnum billStatusEnum = CodeableEnumUtils.getCodeableEnumByCode(dbWipReqLine.getLineStatus(), BillStatusEnum.class);
            return "替换失败，" + billStatusEnum.getDesc() + "的投料单无法执行替换操作；";
        }
        dbWipReqLine.setSourceCode(wipReqLine.getSourceCode())
                .setBeforeItemNo(beforeItemNo)
                .setItemNo(afterItemNo)
                // 投料指令执行时保证只调用一次存储过程
                .setGroupId(wipReqLine.getGroupId());
        replacedData.add(dbWipReqLine);
        return "";
    }

    /**
     * 替换指定的投料单行数据。详情可参考KB文档：https://kb.cvte.com/pages/viewpage.action?pageId=168739975
     */
    public String[] replace(List<WipReqLineEntity> wipReqLineList, ExecutionModeEnum eMode, ChangedModeEnum cMode, boolean isLog, String userId) {
        String errorMessage = processReplacementExcessStock(wipReqLineList);
        if (errorMessage.length() > 0) {
            throw new ParamsIncorrectException(errorMessage);
        }
        Function<List<WipReqLineEntity>, String[]> validateAndGetData = getValidator(wipReqLineList, ChangedTypeEnum.REPLACE, this::validateAndGetReplacedData);
        Consumer<WipReqLineEntity> complete = line -> EntityUtils.writeStdUpdInfoToEntity(line, getWipUserId());
        Consumer<WipReqLineEntity> manipulate = line -> {
            String lineId = line.getLineId(), afterItemNo = line.getItemNo();
            cancelledByConditions(singletonList(line.setItemNo(line.getBeforeItemNo()).setItemId(null)), ExecutionModeEnum.STRICT, cMode, false, userId);
            addOne(line.setBeforeLineId(lineId).setItemNo(afterItemNo), ExecutionModeEnum.STRICT, cMode, false);
        };
        ChangedParameters parameters = new ChangedParameters().setType(ChangedTypeEnum.REPLACE).setLog(isLog).setComplete(complete)
                .setValidateAndGetData(validateAndGetData).setManipulate(manipulate).setEMode(eMode).setCMode(cMode);
        return change(parameters);
    }

    /**
     * 工序替换
     *
     * @param wipReqLineList
     * @param eMode
     * @param cMode
     * @param isLog
     * @param userId
     * @return java.lang.String[]
     **/
    public String[] replaceWkp(List<WipReqLineEntity> wipReqLineList, ExecutionModeEnum eMode, ChangedModeEnum cMode, boolean isLog, String userId) {

        Function<List<WipReqLineEntity>, String[]> validateAndGetData = getValidator(wipReqLineList, ChangedTypeEnum.WKP_REPLACE, this::validateAndGetReplacedWkpNoData);
        Consumer<WipReqLineEntity> complete = line -> EntityUtils.writeStdUpdInfoToEntity(line, getWipUserId());
        Consumer<WipReqLineEntity> manipulate = line -> {
            String lineId = line.getLineId(), afterWkpNo = line.getWkpNo();
            cancelledByConditions(singletonList(line.setWkpNo(line.getBeforeWkpNo()).setItemId(null)), ExecutionModeEnum.STRICT, cMode, false, userId);
            addOne(line.setBeforeLineId(lineId).setWkpNo(afterWkpNo), ExecutionModeEnum.STRICT, cMode, false);
        };
        ChangedParameters parameters = new ChangedParameters().setType(ChangedTypeEnum.WKP_REPLACE).setLog(isLog).setComplete(complete)
                .setValidateAndGetData(validateAndGetData).setManipulate(manipulate).setEMode(eMode).setCMode(cMode);
        return change(parameters);
    }


    /**
     * 校验内部的替换数据，并返回校验错误的信息，将正确的替换数据添加到 {@param replacedData} 。
     */
    private String validateAndGetReplacedWkpNoData(WipReqLineEntity wipReqLine, List<WipReqLineEntity> replacedData) {
        String beforeWkpNo = wipReqLine.getBeforeWkpNo(), afterWkpNo = wipReqLine.getWkpNo(), errorMsg;
        if (StringUtils.isEmpty(beforeWkpNo)) {
            return "替换失败，替换前的工序为空，请您仔细修改后重试；";
        } else if (StringUtils.isEmpty(afterWkpNo)) {
            return "替换失败，替换后的工序为空，请您仔细修改后重试；";
        } else if (beforeWkpNo.equals(afterWkpNo)) {
            return "替换失败，替换前后的工序相同，请您仔细修改后重试；";
        } else if (isNotEmpty(errorMsg = validateIndex(wipReqLine))) {
            return format("抱歉，您替换后的数据有点小问题，{}", errorMsg);
        }

        Example example = wipReqLineRepository.createCustomExample(wipReqLine.setWkpNo(beforeWkpNo).setItemId(null));
        if (nonNull(example) && StringUtils.isNotEmpty(wipReqLine.getLineId())) {
            example.getOredCriteria().get(0).andEqualTo("lineId", wipReqLine.getLineId());
        }
        List<WipReqLineEntity> wipReqLines = wipReqLineRepository.selectByExample(example);
        if (isEmpty(wipReqLines)) {
            log.error(logFormat.apply(format("替换条件无法查询待替换的投料单行数据，替换条件 = [{}]；", EntityUtils.getEntityPrintInfo(wipReqLine)), ChangedTypeEnum.WKP_REPLACE));
            return "替换失败，请您仔细检查替换条件是否正确；";
        } else if (wipReqLines.size() > 1) {
            log.error(logFormat.apply(format("投料单行数据表出现了脏数据，异常行ID = {}；", Arrays.toString(toLineIds(wipReqLines))), ChangedTypeEnum.WKP_REPLACE));
            return "糟糕，系统出现了未知错误，速联系相关人员；";
        }

        WipReqLineEntity dbWipReqLine = wipReqLines.get(0);
        if (CodeableEnumUtils.getCodeableEnumByCode(dbWipReqLine.getLineStatus(), BillStatusEnum.class) == BillStatusEnum.ISSUED) {
            return "替换失败，已领料的投料单无法执行替换操作。";
        }
        replacedData.add(dbWipReqLine.setSourceCode(wipReqLine.getSourceCode()).setBeforeWkpNo(beforeWkpNo).setWkpNo(afterWkpNo));
        return "";
    }

    /**
     * 处理替换时的呆料问题
     */
    private String processReplacementExcessStock(List<WipReqLineEntity> wipReqLineList) {
        if (CollectionUtil.isNotEmpty(wipReqLineList)) {
            HashMap<String, String> itemNoMap = new HashMap<>();
            for (WipReqLineEntity line : wipReqLineList) {
                String beforeItemNo = itemNoMap.putIfAbsent(line.getItemNo(), line.getBeforeItemNo());
                if (isNotEmpty(beforeItemNo) && !beforeItemNo.equals(line.getBeforeItemNo())) {
                    return "抱歉，系统不允许不同物料替换为同一个物料。";
                }
            }
        }
        return "";
    }

    public void executeByChangeBill(List<WipReqLineEntity> wipReqLineList, ExecutionModeEnum eMode, ChangedModeEnum cMode, boolean isLog, String userId) {
        wipReqLineList = sortLineByChangeType(wipReqLineList);
        Function<List<WipReqLineEntity>, String[]> validateAndGetData = getValidator(wipReqLineList, ChangedTypeEnum.EXECUTE, this::simpleAddChangedData);
        Consumer<WipReqLineEntity> complete = line -> EntityUtils.writeStdUpdInfoToEntity(line, getWipUserId());
        Consumer<WipReqLineEntity> manipulate = line -> {
            ChangedTypeEnum typeEnum = CodeableEnumUtils.getCodeableEnumByCode(line.getChangeType(), ChangedTypeEnum.class);
            if (Objects.isNull(typeEnum)) {
                throw new ParamsIncorrectException("更改类型不存在或为空");
            }
            switch (typeEnum) {
                case ADD:
                    addOne(line, eMode, cMode, false);
                    break;
                case DELETE:
                    cancelledByConditions(singletonList(line), eMode, cMode, false, userId);
                    break;
                case REPLACE:
                    replace(singletonList(line), eMode, cMode, false, userId);
                    break;
                case UPDATE:
                    update(singletonList(line), eMode, cMode, false, userId);
                    break;
                case REDUCE:
                    reduce(singletonList(line), eMode, cMode, false, userId);
                    break;
                case INCREASE:
                    increase(singletonList(line), eMode, cMode, false, userId);
                    break;
                default:
                    throw new ParamsIncorrectException("不支持的投料行变更类型:" + typeEnum.getDesc());
            }
        };
        ChangedParameters parameters = new ChangedParameters().setType(ChangedTypeEnum.EXECUTE).setLog(isLog).setComplete(complete)
                .setValidateAndGetData(validateAndGetData).setManipulate(manipulate).setEMode(eMode).setCMode(cMode);
        change(parameters);
    }

    /**
     * 减少数量, 不写库, 只写入接口表
     */
    public String[] reduce(List<WipReqLineEntity> wipReqLineList, ExecutionModeEnum eMode, ChangedModeEnum cMode, boolean isLog, String userId) {
        Function<List<WipReqLineEntity>, String[]> validateAndGetData = getValidator(wipReqLineList, ChangedTypeEnum.REDUCE, this::simpleAddChangedData);
        Consumer<WipReqLineEntity> manipulate = line -> line.setUpdUser(userId);
        Consumer<WipReqLineEntity> complete = line -> line.setUpdUser(userId);
        ChangedParameters parameters = new ChangedParameters().setType(ChangedTypeEnum.DELETE).setLog(isLog).setComplete(complete)
                .setValidateAndGetData(validateAndGetData).setManipulate(manipulate).setEMode(eMode).setCMode(cMode);
        return change(parameters);
    }

    /**
     * 增加数量, 不写库, 只写入接口表
     */
    public String[] increase(List<WipReqLineEntity> wipReqLineList, ExecutionModeEnum eMode, ChangedModeEnum cMode, boolean isLog, String userId) {
        Function<List<WipReqLineEntity>, String[]> validateAndGetData = getValidator(wipReqLineList, ChangedTypeEnum.INCREASE, this::simpleAddChangedData);
        Consumer<WipReqLineEntity> manipulate = line -> line.setUpdUser(userId);
        Consumer<WipReqLineEntity> complete = line -> line.setUpdUser(userId);
        ChangedParameters parameters = new ChangedParameters().setType(ChangedTypeEnum.ADD).setLog(isLog).setComplete(complete)
                .setValidateAndGetData(validateAndGetData).setManipulate(manipulate).setEMode(eMode).setCMode(cMode);
        return change(parameters);
    }

    private List<WipReqLineEntity> sortLineByChangeType(List<WipReqLineEntity> reqLineEntityList) {
        List<WipReqLineEntity> sortedList = new ArrayList<>();
        Map<String, List<WipReqLineEntity>> groupedLineMap = reqLineEntityList.stream().collect(Collectors.groupingBy(WipReqLineEntity::getChangeType));
        Consumer<String> addGroupedLine = changeType -> {
            List<WipReqLineEntity> groupedLine = groupedLineMap.get(changeType);
            if (ListUtil.notEmpty(groupedLine)) {
                sortedList.addAll(groupedLine);
            }
        };
        addGroupedLine.accept(ChangedTypeEnum.DELETE.getCode());
        addGroupedLine.accept(ChangedTypeEnum.ADD.getCode());
        addGroupedLine.accept(ChangedTypeEnum.REPLACE.getCode());
        addGroupedLine.accept(ChangedTypeEnum.UPDATE.getCode());
        addGroupedLine.accept(ChangedTypeEnum.REDUCE.getCode());
        addGroupedLine.accept(ChangedTypeEnum.INCREASE.getCode());
        return sortedList;
    }

    private String simpleAddChangedData(WipReqLineEntity wipReqLine, List<WipReqLineEntity> changedData) {
        changedData.add(wipReqLine);
        return "";
    }

    /**
     * 修改指定的投料单行数据。详情可参考KB文档：https://kb.cvte.com/pages/viewpage.action?pageId=168739875
     */
    public String[] update(List<WipReqLineEntity> wipReqLineList, ExecutionModeEnum eMode, ChangedModeEnum cMode, boolean isLog, String userId) {
        Function<List<WipReqLineEntity>, String[]> validateAndGetData = getValidator(wipReqLineList, ChangedTypeEnum.UPDATE, this::validateAndGetUpdateData);
        Consumer<WipReqLineEntity> manipulate = wipReqLineRepository::updateSelectiveById;
        Consumer<WipReqLineEntity> complete = line -> {
            EntityUtils.writeStdUpdInfoToEntity(line, userId);
            if (Objects.nonNull(line.getIssuedQty()) && line.getIssuedQty() > 0) {
                // 领料数量大于0, 更新状态为已领料
                boolean needChangeFlag = BillStatusEnum.CLOSED.getCode().equals(line.getLineStatus())
                        || BillStatusEnum.CANCELLED.getCode().equals(line.getLineStatus())
                        || BillStatusEnum.ISSUED.getCode().equals(line.getLineStatus());
                if (!needChangeFlag) {
                    line.setLineStatus(BillStatusEnum.ISSUED.getCode());
                }
            }
            if (Objects.nonNull(line.getIssuedQty()) && line.getIssuedQty() <= 0 && BillStatusEnum.ISSUED.getCode().equals(line.getLineStatus())) {
                line.setLineStatus(BillStatusEnum.PREPARED.getCode());
            }
            if (Objects.nonNull(line.getReqQty()) && line.getReqQty() == 0) {
                // 需求数量减少为0, 则更新状态为取消
                completeCancelledData(line, userId);
            }
        };
        ChangedParameters parameters = new ChangedParameters().setType(ChangedTypeEnum.UPDATE).setLog(isLog).setComplete(complete)
                .setValidateAndGetData(validateAndGetData).setManipulate(manipulate).setEMode(eMode).setCMode(cMode);
        return change(parameters);
    }

    /**
     * 校验待更新的查询条件，并返回校验错误的信息，将查询的待更新数据添加于参数 {@param updateData} 中。
     */
    private String validateAndGetUpdateData(WipReqLineEntity wipReqLine, List<WipReqLineEntity> updateData) {
        Example example;
        List<WipReqLineEntity> wipReqLineList;
        if (isNull(example = wipReqLineRepository.createCustomExample(wipReqLine)) || isEmpty(wipReqLineList = wipReqLineRepository.selectByExample(example))) {
            log.error(logFormat.apply(format("更新条件无法查询待更新的投料单行数据，更新条件 = [{}]；", EntityUtils.getEntityPrintInfo(wipReqLine)), ChangedTypeEnum.UPDATE));
            return "更新失败，请您检查更新条件是否正确；";
        }
        String lineId = wipReqLine.getLineId();
        if (isNotEmpty(lineId)) {
            WipReqLineEntity dbWipReqLine;
            if (wipReqLineList.size() != 1) {
                log.error(logFormat.apply(format("更新后的索引数据不完备，更新后数据 = {}；", EntityUtils.getEntityPrintInfo(wipReqLine)), ChangedTypeEnum.UPDATE));
                return "更新失败，更新后的数据存在冲突，速联系相关人员；";
            }
            dbWipReqLine = wipReqLineList.get(0);
            if (!lineId.equals(dbWipReqLine.getLineId())) {
                log.error(logFormat.apply(format("更新前后的投料单行ID不一致，更新前的行ID = {}，更新后的行ID = {}；", dbWipReqLine.getLineId(), lineId), ChangedTypeEnum.UPDATE));
                return "更新失败，更新前后的数据不一致，速联系相关人员；";
            }
        }
        wipReqLineList.forEach(line -> copyProperties(wipReqLine, line, EntityUtils.IGNORE_NULL_VALUE_OPTION));
        updateData.addAll(wipReqLineList);
        return "";
    }

    /**
     * 校验投料单行数据的索引值是否正确，即投料单头、组织、小批次号、工序号、物料编号是否为空，行状态不能为已取消。
     */
    private String validateIndex(WipReqLineEntity wipReqLine) {
        StringBuilder indexErrorMsg = new StringBuilder();
        BiFunction<String, String, String> format = (s1, s2) -> StringUtils.isEmpty(s1) ? s2 + "为空，" : "";
        indexErrorMsg.append(format.apply(wipReqLine.getHeaderId(), "投料单头ID"))
                .append(format.apply(wipReqLine.getOrganizationId(), "组织ID"))
                .append(format.apply(wipReqLine.getLotNumber(), "小批次号"))
                .append(format.apply(wipReqLine.getWkpNo(), "工序号"))
                .append(format.apply(wipReqLine.getItemNo(), "物料编码"));
        if (StringUtils.isNotEmpty(wipReqLine.getLineStatus())) {
            BillStatusEnum status = CodeableEnumUtils.getCodeableEnumByCode(wipReqLine.getLineStatus(), BillStatusEnum.class);
            if (isNull(status)) {
                indexErrorMsg.append("投料单行状态错误，");
            } else if (status == BillStatusEnum.CLOSED || status == BillStatusEnum.CANCELLED) {
                indexErrorMsg.append(format("投料单行状态为{}，", status.getDesc()));
            }
        }
        if (indexErrorMsg.length() > 0) {
            indexErrorMsg.setCharAt(indexErrorMsg.length() - 1, '；');
        }
        return indexErrorMsg.toString();
    }

    /**
     * 将投料单行数据列表 {@link WipReqLineEntity} 转换为行ID数组
     */
    private String[] toLineIds(List<WipReqLineEntity> wipReqLineList) {
        // 该方法用于查询，无需用distinct方法去重。
        return ofNullable(wipReqLineList).orElse(Collections.emptyList()).stream()
                .map(WipReqLineEntity::getLineId).filter(StringUtils::isNotEmpty).toArray(String[]::new);
    }

    /**
     * 校验投料单行数据的正确性，如果校验器 {@param v1} 校验成功，则继续使用校验器 {@param v2} 进行校验。以第一个校验器的校验结果为准。
     */
    private String validate(WipReqLineEntity line, Function<WipReqLineEntity, String> v1, Function<WipReqLineEntity, String> v2) {
        String errorMessage = v1.apply(line);
        return isNotEmpty(errorMessage) ? errorMessage : v2.apply(line);
    }

    /**
     * 获取投料单行数据的校验器。该校验器能校验多条投料单行数据，并返回所有错误数据的验证信息。
     */
    private Function<List<WipReqLineEntity>, String[]> getValidator(List<WipReqLineEntity> data, ChangedTypeEnum type,
                                                               BiFunction<WipReqLineEntity, List<WipReqLineEntity>, String> v) {
        if (CollectionUtils.isEmpty(data)) {
            return changedData -> new String[]{"变更数据为空。"};
        }
        log.info(data.toString());
        return changedData -> data.stream().map(line -> validate(line, e -> universalValidate(e, type), e -> v.apply(e, changedData)))
                .toArray(String[]::new);
    }

    private String[] handleErrorMessages(String[] errorMessages, ExecutionModeEnum mode) {
        int size = errorMessages.length;
        if (size == 1) {
            errorMessages[0] = EntityUtils.handleErrorMessage(errorMessages[0], mode);
            return errorMessages;
        }
        StringBuilder info = new StringBuilder();
        for (int i = 0; i < size; ++i) {
            String errorMessage = errorMessages[i];
            if (errorMessage.length() > 0) {
                info.append(i + 1).append('、').append(errorMessage).append(System.lineSeparator());
            }
        }
        if (info.length() == 0) {
            return errorMessages;
        } else if (mode == ExecutionModeEnum.STRICT) {
            throw new ParamsIncorrectException(info.toString());
        }
        log.error(info.toString());
        return errorMessages;
    }


    /**
     * 分割并保存数据
     *
     * @param wipReqLineEntity
     * @return void
     **/
    private void splitAndSaveEntity(WipReqLineEntity wipReqLineEntity) {
        List<WipReqLineEntity> wipReqLineEntities = wipReqLineSplitService.splitByItemWkpPos(Arrays.asList(wipReqLineEntity));
        wipReqLineEntities.forEach(wipReqLineRepository::insertSelective);
    }

    /**
     * 解决{@link WipReqLineService#change(ChangedParameters)}多参数的问题
     */
    @Data
    @Accessors(chain = true)
    private class ChangedParameters {
        private Function<List<WipReqLineEntity>, String[]> validateAndGetData;
        private Consumer<WipReqLineEntity> complete;
        private Consumer<WipReqLineEntity> manipulate;
        private boolean isLog;
        private ChangedTypeEnum type;
        private ExecutionModeEnum eMode;
        private ChangedModeEnum cMode;
    }
}
