package com.cvte.scm.wip.domain.core.ckd.service;

import com.alibaba.fastjson.JSONObject;
import com.cvte.csb.base.commons.OperatingUser;
import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.core.exception.client.params.SourceNotFoundException;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.csb.toolkit.date.DateUtils;
import com.cvte.scm.wip.common.utils.EmptyObjectUtils;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.common.utils.EnumUtils;
import com.cvte.scm.wip.common.utils.HostUtils;
import com.cvte.scm.wip.domain.common.attachment.dto.AttachmentDTO;
import com.cvte.scm.wip.domain.common.attachment.dto.AttachmentQuery;
import com.cvte.scm.wip.domain.common.attachment.dto.AttachmentVO;
import com.cvte.scm.wip.domain.common.attachment.service.WipAttachmentService;
import com.cvte.scm.wip.domain.common.base.WipBaseService;
import com.cvte.scm.wip.domain.common.log.constant.LogModuleConstant;
import com.cvte.scm.wip.domain.common.log.dto.WipLogDTO;
import com.cvte.scm.wip.domain.common.log.service.WipOperationLogService;
import com.cvte.scm.wip.domain.common.user.entity.UserBaseEntity;
import com.cvte.scm.wip.domain.common.user.service.UserService;
import com.cvte.scm.wip.domain.core.ckd.constants.WipMcTaskConstant;
import com.cvte.scm.wip.domain.core.ckd.dto.WipMcTaskSaveDTO;
import com.cvte.scm.wip.domain.core.ckd.dto.WipMcTaskUpdateDTO;
import com.cvte.scm.wip.domain.core.ckd.dto.WipMcTaskUpdateStatusDTO;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcLineStatusQuery;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcTaskLineQuery;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcTaskQuery;
import com.cvte.scm.wip.domain.core.ckd.dto.view.*;
import com.cvte.scm.wip.domain.core.ckd.entity.*;
import com.cvte.scm.wip.domain.core.ckd.enums.*;
import com.cvte.scm.wip.domain.core.ckd.handler.McTaskStatusUpdateHandlerFactory;
import com.cvte.scm.wip.domain.core.ckd.handler.McTaskStatusUpdateIHandler;
import com.cvte.scm.wip.domain.core.ckd.repository.WipMcTaskRepository;
import com.cvte.scm.wip.domain.core.scm.dto.query.SysOrgOrganizationVQuery;
import com.cvte.scm.wip.domain.core.scm.service.ScmViewCommonService;
import com.cvte.scm.wip.domain.core.scm.vo.SysBuDeptVO;
import com.cvte.scm.wip.domain.core.scm.vo.SysOrgOrganizationVO;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.dto.EbsInoutStockDTO;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.dto.EbsInoutStockResponse;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.enums.EbsDeliveryStatusEnum;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.enums.InterfaceActionEnum;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.enums.TxnSourceTypeNameEnum;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.service.EbsInvokeService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 服务实现类
 *
 * @author zy
 * @since 2020-04-28
 */
@Slf4j
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipMcTaskService extends WipBaseService<WipMcTaskEntity, WipMcTaskRepository> {

    @Value("${server.appId}")
    private String appId;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private WipMcTaskLineService wipMcTaskLineService;

    @Autowired
    private WipMcReqToTaskService wipMcReqToTaskService;

    @Autowired
    private WipMcWfService wipMcWfService;

    @Autowired
    private WipMcTaskVersionService wipMcTaskVersionService;

    @Autowired
    private WipMcTaskValidateService wipMcTaskValidateService;

    @Autowired
    private UserService userService;

    @Autowired
    private EbsInvokeService ebsInvokeService;

    @Autowired
    private ScmViewCommonService scmViewCommonService;

    @Autowired
    private WipMcInoutStockService wipMcInoutStockService;

    @Autowired
    private WipMcInoutStockLineService wipMcInoutStockLineService;

    @Autowired
    private WipOperationLogService wipOperationLogService;

    @Autowired
    private WipAttachmentService wipAttachmentService;

    public WipMcTaskEntity getById(String mcTaskId) {
        return repository.getById(mcTaskId);
    }

    public void updateStatusBySourceLine(WipMcTaskUpdateStatusDTO wipMcTaskUpdateStatus) {
        wipMcTaskUpdateStatus.validate();

        if (CollectionUtils.isEmpty(wipMcTaskUpdateStatus.getSourceLineIds())) {
            return;
        }

        List<WipMcTaskView> wipMcTaskViews = listWipMcTaskView(new WipMcTaskQuery().setSourceLineIds(wipMcTaskUpdateStatus.getSourceLineIds()));

        OperatingUser operatingUser = new OperatingUser();
        operatingUser.setName(wipMcTaskUpdateStatus.getOptUser());
        operatingUser.setId(wipMcTaskUpdateStatus.getOptUser());
        operatingUser.setAccount(wipMcTaskUpdateStatus.getOptUser());
        CurrentContext.setCurrentOperatingUser(operatingUser);

        Set<String> mcTaskIds = wipMcTaskViews.stream().map(WipMcTaskView::getMcTaskId).collect(Collectors.toSet());
        mcTaskIds.forEach(el -> this.updateStatus(el, wipMcTaskUpdateStatus.getUpdateToStatus()));
    }

    public void updateStatus(String taskId, String updateToStatus) {
        updateStatus(taskId, updateToStatus, true);
    }


    /**
     * 变更配料任务状态
     *
     * 目前会跳过生命周期状态转换的校验场景有：
     *  1. 配料任务状态取消锁定。专题会转换到配料任务前一个非锁定的状态
     *  2. 更新配料任务行信息，如果所有配料任务行都被失效的，会将配料任务作废
     *
     * @param taskId
     * @param updateToStatus
     * @param validateUpdateTO 仅控制是否做生命周期状态转换的校验，不控制注册在处理器中的特殊校验
     * @return void
     **/
    public void updateStatus(String taskId, String updateToStatus, boolean validateUpdateTO) {

        McTaskStatusEnum updateToStatusEnum = EnumUtils.getByCode(updateToStatus, McTaskStatusEnum.class);

        McTaskInfoView mcTaskInfoView = getMcTaskInfoView(taskId);

        if (validateUpdateTO) {
            wipMcTaskValidateService.validateUpdStatusTo(mcTaskInfoView.getStatus(), updateToStatus);
        }


        // 已注册的特殊状态变更校验
        McTaskStatusEnum curStatusEnum = EnumUtils.getByCode(mcTaskInfoView.getStatus(), McTaskStatusEnum.class);
        McTaskStatusUpdateIHandler mcTaskStatusUpdateIHandler =
                McTaskStatusUpdateHandlerFactory.getInstance(curStatusEnum, updateToStatusEnum);

        mcTaskStatusUpdateIHandler.validate(mcTaskInfoView);

        // 保存状态
        String mcWfId = wipMcWfService.saveTaskStatus(taskId, updateToStatusEnum);

        // 状态变更后续处理
        mcTaskStatusUpdateIHandler.handler(mcTaskInfoView);

        // 最后才更新头表信息，避免状态提前变更后校验出错
        WipMcTaskEntity wipMcTask = new WipMcTaskEntity();
        wipMcTask.setMcTaskId(mcTaskInfoView.getMcTaskId()).setMcWfId(mcWfId);
        EntityUtils.writeStdUpdInfoToEntity(wipMcTask, CurrentContext.getCurrentOperatingUser().getId());
        repository.updateSelectiveById(wipMcTask);

        if (McTaskStatusEnum.CHANGE.equals(curStatusEnum) && !McTaskStatusEnum.CHANGE.equals(updateToStatusEnum)) {
            // crm取消变更时会回滚到之前的状态，这里记录的日志信息需要特殊处理下
            wipOperationLogService.addLog(new WipLogDTO(LogModuleConstant.CKD,
                    taskId,
                    "解除锁定",
                    String.format("%s -> %s", curStatusEnum.getValue(), updateToStatusEnum.getValue())));
        } else {
            wipOperationLogService.addLog(new WipLogDTO(LogModuleConstant.CKD,
                    taskId,
                    updateToStatusEnum.getOptName()));
        }


    }

    public McTaskInfoView getMcTaskInfoView(String mcTaskId) {

        if (StringUtils.isBlank(mcTaskId)) {
            throw new ParamsRequiredException("配料任务单id不能为空");
        }

        McTaskInfoView mcTaskInfoView = repository.getMcTaskInfoView(mcTaskId);
        if (ObjectUtils.isNull(mcTaskInfoView)) {
            return null;
        }

        // 设入字典值
        if (StringUtils.isNotBlank(mcTaskInfoView.getStatus())) {
            mcTaskInfoView.setStatusName(EnumUtils.getByCode(mcTaskInfoView.getStatus(), McTaskStatusEnum.class).getValue());
        }
        if (StringUtils.isNotBlank(mcTaskInfoView.getMcFinishStatusCode())) {
            mcTaskInfoView.setMcFinishStatus(EnumUtils.getByCode(mcTaskInfoView.getMcFinishStatusCode(), McTaskFinishStatusEnums.class).getValue());
        }

        return mcTaskInfoView;
    }


    
    public WipMcTaskDetailView saveWipMcTaskSaveDTO(WipMcTaskSaveDTO wipMcTaskSaveDTO) {

        if (CollectionUtils.isEmpty(wipMcTaskSaveDTO.getMcReqs())) {
            throw new ParamsRequiredException("开立需求单数量不能为空");
        }

        if (CollectionUtils.isEmpty(wipMcTaskSaveDTO.getMcLines())) {
            throw new ParamsRequiredException("开立需求单行不能为空");
        }

        if (StringUtils.isBlank(wipMcTaskSaveDTO.getOptUser())) {
            throw new ParamsRequiredException("当前操作人不能为空");
        }

        // 设入当前处理
        OperatingUser operatingUser =  new OperatingUser();
        operatingUser.setAccount(wipMcTaskSaveDTO.getOptUser());
        operatingUser.setId(wipMcTaskSaveDTO.getOptUser());
        operatingUser.setName(wipMcTaskSaveDTO.getOptUser());
        CurrentContext.setCurrentOperatingUser(operatingUser);

        WipMcTaskDetailView wipMcTaskDetailView;
        WipMcOptTypeEnum wipMcOptTypeEnum = WipMcOptTypeEnum.getByCode(wipMcTaskSaveDTO.getOptType());
        switch (wipMcOptTypeEnum) {
            case ALONE:
                wipMcTaskDetailView = saveAloneWipMcTaskSaveDTO(wipMcTaskSaveDTO);
                break;
            case MERGE:
                wipMcTaskDetailView = saveMergeWipMcTaskSaveDTO(wipMcTaskSaveDTO);
                break;
            default:
                throw new ParamsIncorrectException("不支持的开立类型");
        }

        return wipMcTaskDetailView;
    }

    
    public void batchUpdate(WipMcTaskUpdateDTO wipMcTaskUpdateDTOList) {

        if (StringUtils.isBlank(wipMcTaskUpdateDTOList.getOptUser())) {
            throw new ParamsRequiredException("当前操作人不可为空");
        }

        if (CollectionUtils.isEmpty(wipMcTaskUpdateDTOList.getUpdateList())) {
            return;
        }

        List<String> mcTaskNos = validateAndGetMcTaskNos(wipMcTaskUpdateDTOList.getUpdateList());
        List<WipMcTaskEntity> wipMcTasks = repository.listWipMcTask(new WipMcTaskQuery().setMcTaskNos(mcTaskNos));
        Map<String, String> soNoAndIdMap =
                wipMcTasks.stream().collect(Collectors.toMap(WipMcTaskEntity::getMcTaskNo, WipMcTaskEntity::getMcTaskId));


        Date curDate = new Date();
        List<WipMcTaskEntity> updateList = modelMapper.map(wipMcTaskUpdateDTOList.getUpdateList(), new TypeToken<List<WipMcTaskEntity>>(){}.getType());
        updateList.forEach(el -> el.setMcTaskId(soNoAndIdMap.get(el.getMcTaskNo()))
                .setUpdUser(el.getUpdUser())
                .setUpdHost(HostUtils.getLocalHostAddress())
                .setUpdTime(curDate));

        repository.updateList(updateList);
    }

    
    public void inoutStock(TransactionTypeNameEnum transactionTypeNameEnum, String taskId, String versionId, List<String> mcTaskLineIds) {

        if (StringUtils.isBlank(taskId, versionId)) {
            throw new ParamsRequiredException("配料任务id/版本id不能为空");
        }

        if (CollectionUtils.isEmpty(mcTaskLineIds)) {
            throw new ParamsRequiredException("请先勾选需要调拨的配料任务行");
        }

        McTaskInfoView mcTaskInfoView = getMcTaskInfoView(taskId);
        if (ObjectUtils.isNull(mcTaskInfoView)) {
            throw new SourceNotFoundException("获取配料任务信息错误");
        }

        if (!versionId.equals(mcTaskInfoView.getVersionId())) {
            throw new ParamsIncorrectException("只能调拨最新版本的配料清单");
        }

        String inoutStockId = UUIDUtils.getUUID();
        List<WipMcTaskLineView> wipMcTaskLineViews = wipMcTaskLineService.listWipMcTaskLineView(
                new WipMcTaskLineQuery()
                        .setTaskIds(Arrays.asList(taskId))
                        .setTaskLineIds(mcTaskLineIds)
                        .setLineStatus(McTaskLineStatusEnum.NORMAL.getCode()));

        if (CollectionUtils.isEmpty(wipMcTaskLineViews)) {
            throw new ParamsIncorrectException("请选择需要调拨的行");
        }
        wipMcTaskValidateService.validateInoutStock(transactionTypeNameEnum, mcTaskInfoView, wipMcTaskLineViews);

        EbsInoutStockDTO ebsInoutStockDTO = generateEbsInoutStockDTO(transactionTypeNameEnum, inoutStockId, mcTaskInfoView, wipMcTaskLineViews);
        EbsInoutStockResponse ebsInoutStockResponse = ebsInvokeService.inoutStockImport(ebsInoutStockDTO);

        if (ObjectUtils.isNull(ebsInoutStockResponse) || ObjectUtils.isNull(ebsInoutStockResponse.getReturnInfo())) {
            return;
        }

        WipMcInoutStockEntity wipMcInoutStock = generateWipMcInoutStockByInoutStock(transactionTypeNameEnum,
                mcTaskInfoView.getMcTaskId(),
                ebsInoutStockResponse.getReturnInfo().getHeaderId(),
                ebsInoutStockResponse.getReturnInfo().getTicketNo());
        wipMcInoutStock.setInoutStockId(inoutStockId);

        if (CollectionUtils.isEmpty(ebsInoutStockResponse.getRtLines())) {
            return;
        }

        Map<String, EbsInoutStockDTO.LineDTO> lineDTOMap = ebsInoutStockDTO.getImportLnJson()
                .stream()
                .collect(Collectors.toMap(EbsInoutStockDTO.LineDTO::getInterfaceOrigSourceId, Function.identity()));

        List<WipMcInoutStockLineEntity> wipMcInoutStockLines = new ArrayList<>();
        for (EbsInoutStockResponse.StockLine stockLine : ebsInoutStockResponse.getRtLines()) {

            wipMcInoutStockLines.add(generateWipMcInoutStockLineByInoutStock(transactionTypeNameEnum,
                    inoutStockId, stockLine, lineDTOMap.get(stockLine.getInterfaceOrigSourceId())));
        }

        wipMcInoutStockLineService.insertList(wipMcInoutStockLines);
        wipMcInoutStockService.insertSelective(wipMcInoutStock);

        // 更新头表的更新时间
        WipMcTaskEntity wipMcTask = new WipMcTaskEntity().setMcTaskId(taskId);
        EntityUtils.writeStdUpdInfoToEntity(wipMcTask, CurrentContext.getCurrentOperatingUser().getId());
        repository.updateSelectiveById(wipMcTask);

        wipMcTaskVersionService.sync(taskId, false);


        String operation = TransactionTypeNameEnum.IN.equals(transactionTypeNameEnum) ?
                "调拨入库" : "调拨出库";
        wipOperationLogService.addLog(new WipLogDTO(LogModuleConstant.CKD,
                taskId,
                operation));

    }

    public void saveBatchAttachment(List<AttachmentDTO> attachmentSaveDTOList) {

        if (CollectionUtils.isEmpty(attachmentSaveDTOList)) {
            return;
        }

        wipAttachmentService.saveBatch(attachmentSaveDTOList);

        String detail = attachmentSaveDTOList.stream().map(AttachmentDTO::getFileName).collect(Collectors.joining("；"));
        WipLogDTO wipLogDTO = new WipLogDTO();
        wipLogDTO.setModule(LogModuleConstant.CKD)
                .setReferenceId(attachmentSaveDTOList.get(0).getReferenceId())
                .setOperation("上传附件")
                .setDetail(detail + " 附件上传");
        wipOperationLogService.addLog(wipLogDTO);

    }

    public void removeAttachmentById(String id) {
        AttachmentVO wipAttachment = wipAttachmentService.getAttachmentView(new AttachmentQuery().setId(id));
        if (ObjectUtils.isNull(wipAttachment)) {
            return;
        }
        wipAttachmentService.removeById(id);

        WipLogDTO wipLogDTO = new WipLogDTO();
        wipLogDTO.setModule(LogModuleConstant.CKD)
                .setReferenceId(wipAttachment.getReferenceId())
                .setOperation("删除附件")
                .setDetail(wipAttachment.getFileName() + " 附件删除");
        wipOperationLogService.addLog(wipLogDTO);
    }


    private WipMcInoutStockEntity generateWipMcInoutStockByInoutStock(TransactionTypeNameEnum transactionTypeNameEnum,
                                                          String taskId,
                                                          String headerId,
                                                          String deliveryNo) {
        WipMcInoutStockEntity wipMcInoutStock = new WipMcInoutStockEntity();
        wipMcInoutStock.setInoutStockId(UUIDUtils.getUUID())
                .setMcTaskId(taskId)
                .setHeaderId(headerId)
                .setHeaderNo(deliveryNo)
                .setStatus(EbsDeliveryStatusEnum.ENTER.getCode())
                .setType(transactionTypeNameEnum.name());
        EntityUtils.writeCurUserStdCrtInfoToEntity(wipMcInoutStock);
        return wipMcInoutStock;
    }

    private WipMcInoutStockLineEntity generateWipMcInoutStockLineByInoutStock(TransactionTypeNameEnum transactionTypeNameEnum,
                                                                              String inoutStockId,
                                                                              EbsInoutStockResponse.StockLine stockLine,
                                                                              EbsInoutStockDTO.LineDTO lineDTO) {
        WipMcInoutStockLineEntity wipMcInoutStockLine = new WipMcInoutStockLineEntity();
        wipMcInoutStockLine.setInoutStockLineId(stockLine.getInterfaceOrigSourceId())
                .setInoutStockId(inoutStockId)
                .setLineId(stockLine.getLineId())
                .setLineNo(stockLine.getLineNo())
                .setMcTaskLineId(stockLine.getMcTaskLineId())
                .setStatus(McTaskDeliveryStatusEnum.UN_POST.getCode())
                .setQty(Integer.parseInt(lineDTO.getPlanQty()))
                .setPostQty(Integer.parseInt(lineDTO.getActualQty()));
        EntityUtils.writeCurUserStdCrtInfoToEntity(wipMcInoutStockLine);

        return wipMcInoutStockLine;
    }

    
    public List<McTaskDeliveringStockView> listMcTaskDeliveringOutStockView() {

        return repository.listMcTaskDeliveringOutStockView();
    }

    
    public List<McTaskDeliveringStockView> listMcTaskDeliveringInStockView() {

        return repository.listMcTaskDeliveringInStockView();
    }


    
    public List<WipLineStatusView> listWipLineStatusView(WipMcLineStatusQuery query) {

        List<WipLineStatusView> wipLineStatusViews = repository.listWipLineStatusView(query);
        wipLineStatusViews.forEach(el -> {
            McTaskStatusEnum mcTaskStatusEnum = EnumUtils.getByCodeOrNull(el.getStatus(), McTaskStatusEnum.class);
            el.setStatusName(ObjectUtils.isNotNull(mcTaskStatusEnum) ? mcTaskStatusEnum.getValue() : null);
        });

        return wipLineStatusViews;
    }


    
    public List<WipMcTaskView> listWipMcTaskView(WipMcTaskQuery query) {

        return repository.listWipMcTaskView(query);
    }


    
    public List<String> listValidTaskIds(List<String> mcTaskIds) {

        if (CollectionUtils.isEmpty(mcTaskIds)) {
            return new ArrayList<>();
        }

        return repository.listValidTaskIds(mcTaskIds);
    }

    public Boolean isSpecClient(String mcTaskId) {
        if (StringUtils.isBlank(mcTaskId)) {
            return false;
        }

        return repository.isSpecClient(mcTaskId);
    }

    private EbsInoutStockDTO generateEbsInoutStockDTO(TransactionTypeNameEnum transactionTypeNameEnum,
                                                      String inoutStockId,
                                                      McTaskInfoView mcTaskInfoView,
                                                      List<WipMcTaskLineView> wipMcTaskLines) {

        int lineNo = 1;
        List<EbsInoutStockDTO.LineDTO> lines = new ArrayList<>();
        for (WipMcTaskLineView wipMcTaskLineView : wipMcTaskLines) {
            EbsInoutStockDTO.LineDTO line = new EbsInoutStockDTO.LineDTO();
            line.setInterfaceOrigSourceId(UUIDUtils.getUUID())
                    .setInterfaceAction(InterfaceActionEnum.INSERT.name())
                    .setLineNo(String.valueOf(lineNo))
                    .setItemNo(wipMcTaskLineView.getItemCode())
                    .setPlanQty(wipMcTaskLineView.getMcQty().toString())
                    .setActualQty(wipMcTaskLineView.getMcQty().toString())
                    .setMcTaskLineId(wipMcTaskLineView.getLineId());
            handlerDefaultValueOfInoutStockLineDTO(transactionTypeNameEnum, wipMcTaskLineView, line);
            lines.add(line);
            lineNo ++;
        }

        EbsInoutStockDTO ebsInoutStockDTO = new EbsInoutStockDTO();
        ebsInoutStockDTO.setInterfaceOrigSourceId(inoutStockId)
                .setInterfaceAction(InterfaceActionEnum.INSERT.name())
                .setTxnSourceTypeName(TxnSourceTypeNameEnum.INV.getName())
                .setTransactionTypeName(transactionTypeNameEnum.getName())
                .setBuName(mcTaskInfoView.getBuName())
                .setDeptName(mcTaskInfoView.getDeptName())
                .setOrganizationName(WipMcTaskConstant.PREFIX_INV + mcTaskInfoView.getEbsOrganizationCode())
                .setReqEmployeeNum(CurrentContext.getCurrentOperatingUser().getAccount())
                .setImportLnJson(lines)
                .setDescription(mcTaskInfoView.getMcTaskNo());
        return ebsInoutStockDTO;
    }

    /**
     * 1、调拨出库：物料仓（XX-01）---》在制仓（XX-257）
     * 对应的目的仓库是XX-257
     * 2、调拨入库：在制仓（XX-257）---》散料仓（XX-54）
     * 对应的目的仓库是XX-54
     *
     * @param
     * @param transactionTypeNameEnum
     * @param wipMcTaskLineView
     * @param lineDTO
     * @return void
     **/
    private void handlerDefaultValueOfInoutStockLineDTO(TransactionTypeNameEnum transactionTypeNameEnum,
                                                        WipMcTaskLineView wipMcTaskLineView,
                                                        EbsInoutStockDTO.LineDTO lineDTO) {
        switch (transactionTypeNameEnum) {
            case IN:
                lineDTO.setSubinventory(getInventoryPrefix(wipMcTaskLineView.getFactoryCode())
                                + WipMcTaskConstant.FACTORY_CODE_SEPARATOR +  WipMcTaskConstant.DEFAULT_STOREHOUSE_MAKE_CODE)
                        .setToSubinventory(getInventoryPrefix(wipMcTaskLineView.getFactoryCode())
                                + WipMcTaskConstant.FACTORY_CODE_SEPARATOR + WipMcTaskConstant.DEFAULT_STOREHOUSE_CKD_CODE)
                        .setLocatorCode(getLocatorCode(lineDTO.getSubinventory(), wipMcTaskLineView.getFactoryCode()));
                break;
            case OUT:
                lineDTO.setSubinventory(getInventoryPrefix(wipMcTaskLineView.getFactoryCode())
                                + WipMcTaskConstant.FACTORY_CODE_SEPARATOR + WipMcTaskConstant.DEFAULT_STOREHOUSE_METRIC_CODE)
                        .setToSubinventory(getInventoryPrefix(wipMcTaskLineView.getFactoryCode())
                                + WipMcTaskConstant.FACTORY_CODE_SEPARATOR + WipMcTaskConstant.DEFAULT_STOREHOUSE_MAKE_CODE)
                        .setLocatorCode(getLocatorCode(lineDTO.getSubinventory(), wipMcTaskLineView.getFactoryCode()));
                break;
            default:
                throw new ParamsIncorrectException("调拨类型错误");
        }

    }

    /**
     * 货位：仓库.工厂编码
     *
     * @param
     * @param subInventory
     * @param factoryCode
     * @return java.lang.String
     **/
    private String getLocatorCode(String subInventory, String factoryCode) {
        return String.format("%s.%s.", subInventory, factoryCode);
    }

    private String getInventoryPrefix(String factoryCode) {

        if (StringUtils.isBlank(factoryCode)) {
            return null;
        }

        return factoryCode.split(WipMcTaskConstant.FACTORY_CODE_SEPARATOR)[0];
    }

    private List<String> validateAndGetMcTaskNos(List<WipMcTaskUpdateDTO.Record> wipMcTaskUpdateDTOList) {
        List<String> mcTaskNos = new ArrayList<>();
        wipMcTaskUpdateDTOList.forEach(el -> {
            if (StringUtils.isBlank(el.getMcTaskNo())) {
                throw new ParamsRequiredException("配料任务编号不能为空");
            }

            if (mcTaskNos.contains(el.getMcTaskNo())) {
                throw new ParamsIncorrectException("配料任务编号出现重复：" + el.getMcTaskNo());
            }

            if (EmptyObjectUtils.isEmptyObject(el, Arrays.asList("mcTaskNo"))) {
                throw new ParamsRequiredException("配料任务更新值不可为空");
            }

            mcTaskNos.add(el.getMcTaskNo());
        });

        return mcTaskNos;
    }

    private WipMcTaskDetailView saveAloneWipMcTaskSaveDTO(WipMcTaskSaveDTO wipMcTaskSaveDTO) {

        wipMcTaskSaveDTO.validate();

        List<WipMcTaskSaveDTO.McReq> mcReqs = wipMcTaskSaveDTO.getMcReqs();
        WipMcTaskSaveDTO.McReq firstMcReq = wipMcTaskSaveDTO.getMcReqs().get(0);

        Set<String> soIdSet = new HashSet<>();

        mcReqs.forEach(el -> {
            el.validate();
            if (!(StringUtils.equals(el.getFactoryId(), firstMcReq.getFactoryId())
                    && StringUtils.equals(el.getBuCode(), firstMcReq.getBuCode())
                    && StringUtils.equals(el.getDeptCode(), firstMcReq.getDeptCode())
                    && StringUtils.equals(el.getOrganizationId(), firstMcReq.getOrganizationId())
                    && el.getMtrReadyTime().equals(firstMcReq.getMtrReadyTime())
            )) {
                log.error("[saveAloneWipMcTaskSaveDTO] 配料需求单行数据出现不一致：mcReqs[0]={}, mcReqs[i]={}",
                        JSONObject.toJSONString(firstMcReq), JSONObject.toJSONString(el));
                throw new ParamsIncorrectException("配料需求单行数据出现不一致，请检查");
            }

            soIdSet.add(el.getMcReqId());
        });

        SysBuDeptVO sysBuDeptDetailVO = scmViewCommonService.getSysBuDeptVO(firstMcReq.getBuCode(), firstMcReq.getDeptCode());
        if (ObjectUtils.isNull(sysBuDeptDetailVO)) {
            throw new SourceNotFoundException(
                    String.format("获取部门/事业错误：buCode=%s,deptCode=%s", firstMcReq.getBuCode(), firstMcReq.getDeptCode()));
        }

        // 头表
        WipMcTaskEntity wipMcTask = modelMapper.map(mcReqs.get(0), WipMcTaskEntity.class);
        wipMcTask.setMcTaskId(UUIDUtils.getUUID())
                .setMcTaskNo(getMcTaskNo(firstMcReq.getFactoryId()))
                .setBuName(sysBuDeptDetailVO.getBuName())
                .setDeptName(sysBuDeptDetailVO.getDeptName())
                .setBackOffice(getUserIdByAccount(wipMcTaskSaveDTO.getBackOffice()))
                .setOrgId(getOrgIdByOrganizationId(firstMcReq.getOrganizationId()))
                .setFinishStatus(McTaskFinishStatusEnums.UN_FINISH.getCode());
        EntityUtils.writeStdCrtInfoToEntity(wipMcTask, CurrentContext.getCurrentOperatingUser().getId());

        // 设入审核状态
        String mcWfId = wipMcWfService.saveTaskStatus(wipMcTask.getMcTaskId(), McTaskStatusEnum.CREATE);
        wipMcTask.setMcWfId(mcWfId);

        repository.insertSelective(wipMcTask);
        List<WipMcTaskLineEntity> wipMcTaskLines =
                wipMcTaskLineService.batchSave(wipMcTask.getMcTaskId(), wipMcTaskSaveDTO.getMcLines());
        wipMcReqToTaskService.saveTaskRel(wipMcTask.getMcTaskId(), soIdSet);

        // 新增版本信息
        wipMcTaskVersionService.add(wipMcTask.getMcTaskId(), modelMapper.map(wipMcTaskLines, new TypeToken<List<WipMcTaskLineView>>(){}.getType()));

        wipOperationLogService.addLog(new WipLogDTO(LogModuleConstant.CKD,
                wipMcTask.getMcTaskId(),
                "创建审批流程"));

        WipMcTaskDetailView wipMcTaskDetailView = modelMapper.map(wipMcTask, WipMcTaskDetailView.class);
        wipMcTaskDetailView.setOrganizationId(firstMcReq.getOrganizationId())
                .setStatus(McTaskStatusEnum.CREATE.getCode())
                .setChilds(modelMapper.map(wipMcTaskLines, new TypeToken<List<WipMcTaskLineView>>(){}.getType()));
        return wipMcTaskDetailView;
    }

    private WipMcTaskDetailView saveMergeWipMcTaskSaveDTO(WipMcTaskSaveDTO wipMcTaskSaveDTO) {

        WipMcTaskSaveDTO.McReq mcReq = wipMcTaskSaveDTO.getMcReqs().get(0);
        mcReq.validate();

        // 校验是否可以变更
        McTaskInfoView mcTaskInfoView = wipMcTaskValidateService.validateUpdOpt(mcReq.getMcTaskId());

        SysBuDeptVO sysBuDeptDetailVO = scmViewCommonService.getSysBuDeptVO(mcReq.getBuCode(), mcReq.getDeptCode());
        if (ObjectUtils.isNull(sysBuDeptDetailVO)) {
            throw new SourceNotFoundException(String.format("获取部门/事业错误：buCode=%s,deptCode=%s", mcReq.getBuCode(), mcReq.getDeptCode()));
        }

        WipMcTaskEntity wipMcTask = getByTaskId(mcReq.getMcTaskId());
        wipMcTask.setMtrReadyTime(mcReq.getMtrReadyTime())
                .setFactoryId(mcReq.getFactoryId())
                .setBuName(sysBuDeptDetailVO.getBuName())
                .setDeptName(sysBuDeptDetailVO.getDeptName())
                .setBackOffice(getUserIdByAccount(wipMcTaskSaveDTO.getBackOffice()))
                .setOrgId(getOrgIdByOrganizationId(mcReq.getOrganizationId()))
                .setFinishStatus(McTaskFinishStatusEnums.UN_FINISH.getCode())
                .setClientId(mcReq.getClientId());
        EntityUtils.writeStdUpdInfoToEntity(wipMcTask, CurrentContext.getCurrentOperatingUser().getId());

        List<WipMcReqToTaskEntity> wipMcReqToTasks = wipMcReqToTaskService.selectList(new WipMcReqToTaskEntity()
                .setMcTaskId(mcReq.getMcTaskId())
                .setMcReqId(mcReq.getMcReqId())
        );
        if (CollectionUtils.isEmpty(wipMcReqToTasks)) {
            throw new ParamsIncorrectException("只可合并相同单号");
        }

        repository.updateSelectiveById(wipMcTask);
        wipMcTaskLineService.batchSave(wipMcTask.getMcTaskId(), wipMcTaskSaveDTO.getMcLines());


        // 同步版本信息
        List<WipMcTaskLineView> wipMcTaskLines = wipMcTaskLineService
                .listWipMcTaskLineView(new WipMcTaskLineQuery().setTaskIds(Arrays.asList(mcReq.getMcTaskId())));
        wipMcTaskVersionService.sync(wipMcTask.getMcTaskId(), true, wipMcTaskLines);

        WipMcTaskDetailView wipMcTaskDetailView = modelMapper.map(wipMcTask, WipMcTaskDetailView.class);
        wipMcTaskDetailView.setOrganizationId(mcReq.getOrganizationId())
                .setStatus(mcTaskInfoView.getStatus())
                .setChilds(modelMapper.map(wipMcTaskLines, new TypeToken<List<WipMcTaskLineView>>(){}.getType()));

        wipOperationLogService.addLog(new WipLogDTO(LogModuleConstant.CKD,
                wipMcTask.getMcTaskId(),
                "合并开立工单"));
        return wipMcTaskDetailView;
    }




    private WipMcTaskEntity getByTaskId(String taskId) {

        WipMcTaskEntity wipMcTask = repository.selectById(taskId);
        if (ObjectUtils.isNull(wipMcTask)) {
            throw new SourceNotFoundException("配料任务不存在");
        }

        return wipMcTask;
    }




    /**
     * 	工厂简称+年月+3位流水码
     *
     * @param factoryId
     * @return java.lang.String
     **/
    private String getMcTaskNo(String factoryId) {

        String factoryCode = scmViewCommonService.getFactoryCodeById(factoryId);
        if (StringUtils.isBlank(factoryCode)) {
            throw new SourceNotFoundException("获取工厂信息错误");
        }

        StringBuilder serialNo = new StringBuilder();
        serialNo.append(factoryCode)
                .append(DateUtils.DateToString(new Date(), "yyyyMM"));

        Integer currentEndWithSerialNo = repository.getCurrentSerialNo(serialNo.toString());

        serialNo.append(String.format("%03d", currentEndWithSerialNo + 1));

        return serialNo.toString();
    }



    /**
     * 根据用户域账号获取用户id
     *
     * @param account
     * @return java.lang.String
     **/
    private String getUserIdByAccount(String account) {
        if (ObjectUtils.isNull(account)) {
            return null;
        }

        UserBaseEntity sysUser = userService.getUserByAccount(account);
        if (ObjectUtils.isNull(sysUser)) {
            throw new SourceNotFoundException("获取用户" + account + "信息错误");
        }
        return sysUser.getId();
    }


    /**
     * 通过ebs组织id获取scm组织id
     *
     * @param organizationId
     * @return java.lang.String
     **/
    private String getOrgIdByOrganizationId(String organizationId) {

        if (StringUtils.isBlank(organizationId)) {
            throw new ParamsRequiredException("ebs组织id不能为空");
        }

        List<SysOrgOrganizationVO> sysOrgOrganizationVS = scmViewCommonService.listSysOrgOrganizationVO(
                new SysOrgOrganizationVQuery().setEbsOrganizationIds(Arrays.asList(organizationId)));
        if (CollectionUtils.isEmpty(sysOrgOrganizationVS)) {
            throw new SourceNotFoundException("获取组织" + organizationId + "信息失败");
        }
        return sysOrgOrganizationVS.get(0).getOrgId();
    }





}
