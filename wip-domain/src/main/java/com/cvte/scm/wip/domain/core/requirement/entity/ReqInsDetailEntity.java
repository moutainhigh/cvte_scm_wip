package com.cvte.scm.wip.domain.core.requirement.entity;

import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.common.base.domain.Entity;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.enums.error.ReqInsErrEnum;
import com.cvte.scm.wip.common.utils.CodeableEnumUtils;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.requirement.factory.ReqInsDetailEntityFactory;
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInsDetailRepository;
import com.cvte.scm.wip.domain.core.requirement.repository.WipLotRepository;
import com.cvte.scm.wip.domain.core.requirement.util.ReqInsSplitHelper;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsBuildVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsDetailBuildVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipLotVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.InsOperationTypeEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ProcessingStatusEnum;
import com.google.common.annotations.VisibleForTesting;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 16:36
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Data
@Component
@Accessors(chain = true)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReqInsDetailEntity implements Entity<String> {

    @Resource
    private ReqInsDetailEntityFactory detailEntityFactory;

    private ReqInsDetailRepository detailRepository;
    private WipLotRepository wipLotRepository;

    public ReqInsDetailEntity(ReqInsDetailRepository detailRepository, WipLotRepository wipLotRepository) {
        this.detailRepository = detailRepository;
        this.wipLotRepository = wipLotRepository;
    }

    @Override
    public String getUniqueId() {
        return insDetailId;
    }

    private String insDetailId;

    private String insHeaderId;

    private String organizationId;

    private String sourceDetailId;

    private String moLotNo;

    private String itemIdOld;

    private String itemIdNew;

    private String wkpNo;

    private String posNo;

    private BigDecimal itemQty;

    private BigDecimal itemUnitQty;

    private String operationType;

    private String insStatus;

    private Date enableDate;

    private Date disableDate;

    private Date confirmDate;

    private String confirmedBy;

    private String invalidBy;

    private String invalidReason;

    private String executeResult;

    // 冗余目标投料单头ID
    private String aimHeaderId;

    // 冗余目标投料单批次
    private String aimReqLotNo;

    private String issueFlag;

    // 实际更改物料
    private String actualItemNo;

    private String itemNoOld;

    private String itemNoNew;

    public List<ReqInsDetailEntity> getByInstructionId(String insHeaderId) {
        return detailRepository.getByInsId(insHeaderId);
    }

    public ReqInsDetailEntity createDetail(ReqInsDetailBuildVO vo) {
        ReqInsDetailEntity detailEntity = detailEntityFactory.perfect(vo);
        detailRepository.insert(detailEntity);
        return detailEntity;
    }

    public ReqInsDetailEntity updateDetail(ReqInsDetailBuildVO vo) {
        ReqInsDetailEntity detailEntity = detailEntityFactory.perfect(vo);
        detailRepository.update(detailEntity);
        return detailEntity;
    }

    public void deleteInsDetail() {
        this.setInsStatus(ProcessingStatusEnum.CLOSE.getCode());
        if (StringUtils.isBlank(this.getInvalidBy())) {
            this.setInvalidBy(EntityUtils.getWipUserId());
        }
        detailRepository.update(this);
    }

    public List<ReqInsDetailEntity> batchCreateDetail(List<ReqInsDetailBuildVO> voList) {
        List<ReqInsDetailEntity> createList = new ArrayList<>();
        for (ReqInsDetailBuildVO detailBuildVO : voList) {
            createList.add(this.createDetail(detailBuildVO));
        }
        return createList;
    }

    public List<ReqInsDetailEntity> batchUpdateDetail(List<ReqInsDetailBuildVO> voList) {
        List<ReqInsDetailEntity> updateList = new ArrayList<>();
        for (ReqInsDetailBuildVO detailBuildVO : voList) {
            updateList.add(this.updateDetail(detailBuildVO));
        }
        return updateList;
    }

    public void batchDeleteDetail(List<ReqInsDetailEntity> entityList) {
        for (ReqInsDetailEntity entity : entityList) {
            entity.deleteInsDetail();
        }
    }


    /**
     * 批量保存指令
     * @since 2020/5/23 10:41 上午
     * @author xueyuting
     */
    public List<ReqInsDetailEntity> batchSaveInsDetail(ReqInsBuildVO vo, Boolean needDelete) {
        List<ReqInsDetailEntity> resultDetailEntityList = new ArrayList<>();
        // 查询数据库现有明细
        List<ReqInsDetailEntity> dbDetailEntityList = getByInstructionId(vo.getInsHeaderId());
        List<ReqInsDetailBuildVO> detailVoList = vo.getDetailList();
        List<String> sourceDetailIdList = detailVoList.stream().map(ReqInsDetailBuildVO::getSourceChangeDetailId).collect(Collectors.toList());
        // 可更新列表
        List<ReqInsDetailBuildVO> updateVoList = new ArrayList<>(detailVoList);
        if (ListUtil.notEmpty(dbDetailEntityList)) {
            Map<String, ReqInsDetailEntity> detailEntityMap = toMapBySourceId(dbDetailEntityList);
            Iterator<ReqInsDetailBuildVO> iterator = updateVoList.iterator();
            while (iterator.hasNext()) {
                ReqInsDetailBuildVO updateVo = iterator.next();
                ReqInsDetailEntity detailEntity = detailEntityMap.get(updateVo.getSourceChangeDetailId());
                if (Objects.nonNull(detailEntity)) {
                    // 将ID设置为数据库的才可更新
                    updateVo.setInsDetailId(detailEntity.getInsDetailId());
                } else {
                    // 数据库不存在的剔除后, 剩下的都是需要更新的
                    iterator.remove();
                }
            }
            if (ListUtil.notEmpty(updateVoList)) {
                resultDetailEntityList.addAll(this.batchUpdateDetail(updateVoList));
            }

            // 数据库存在但是vo列表不存在, 则删除
            dbDetailEntityList.removeIf(detailEntity -> sourceDetailIdList.contains(detailEntity.getSourceDetailId()));
            if (ListUtil.notEmpty(dbDetailEntityList) && needDelete) {
                this.batchDeleteDetail(dbDetailEntityList);
            } else {
                resultDetailEntityList.addAll(dbDetailEntityList);
            }
        }

        // 剩下的新增
        if (updateVoList.size() != detailVoList.size()) {
            Set<String> savedSourceIdSet = updateVoList.stream().map(ReqInsDetailBuildVO::getSourceChangeDetailId).collect(Collectors.toSet());
            detailVoList.removeIf(detailVo -> savedSourceIdSet.contains(detailVo.getSourceChangeDetailId()));
            if (ListUtil.notEmpty(detailVoList)) {
                resultDetailEntityList.addAll(this.batchCreateDetail(detailVoList));
            }
        }

        return resultDetailEntityList;
    }

    private Map<String, ReqInsDetailEntity> toMapBySourceId(List<ReqInsDetailEntity> billDetailEntityList) {
        Map<String, ReqInsDetailEntity> entityMap = new HashMap<>();
        billDetailEntityList.forEach(detailEntity -> entityMap.put(detailEntity.getSourceDetailId(), detailEntity));
        return entityMap;
    }

    public List<WipReqLineEntity> parseDetail(Map<String, List<WipReqLineEntity>> reqLineMap) {
        InsOperationTypeEnum typeEnum = CodeableEnumUtils.getCodeableEnumByCode(this.getOperationType(), InsOperationTypeEnum.class);
        if (Objects.isNull(typeEnum)) {
            throw new ParamsIncorrectException("投料指令变更类型异常");
        }
        List<WipLotVO> wipLotList = wipLotRepository.selectByHeaderId(this.getAimHeaderId());
        Map<String, WipLotVO> wipLotMap = new HashMap<>();
        wipLotList.forEach(lot -> wipLotMap.put(lot.getLotNumber(), lot));
        List<WipReqLineEntity> reqLineList = reqLineMap.get(this.getInsDetailId());

        // 记录实际更改物料
        String actualItemNo = reqLineList.stream().map(WipReqLineEntity::getItemNo).distinct().collect(Collectors.joining(","));
        this.setActualItemNo(Optional.ofNullable(actualItemNo).orElse(this.getItemNoOld()));

        return this.parseByType(reqLineList, wipLotMap, typeEnum);
    }

    private List<WipReqLineEntity> parseByType(List<WipReqLineEntity> reqLineList, Map<String, WipLotVO> wipLotMap, InsOperationTypeEnum typeEnum) {
        switch (typeEnum) {
            case ADD:
                return this.parseAddType(wipLotMap);
            case DELETE:
                return this.parseDeleteType(reqLineList);
            case REPLACE:
                return this.parseReplaceType(reqLineList, wipLotMap);
            case REDUCE:
                return this.parseReduceType(reqLineList, wipLotMap);
            case INCREASE:
                return this.parseIncreaseType(reqLineList, wipLotMap);
        }
        return Collections.emptyList();
    }

    private WipReqLineEntity buildReqLine(WipLotVO wipLotVO) {
        WipReqLineEntity reqLine = new WipReqLineEntity();
        reqLine.setHeaderId(this.getAimHeaderId())
                .setOrganizationId(this.getOrganizationId())
                .setWkpNo(this.getWkpNo())
                .setPosNo(this.getPosNo())
                .setItemId(this.getItemIdNew())
                .setItemNo(this.getItemNoNew())
                .setUnitQty(0.0)
                .setReqQty(0L)
                .setLotNumber(wipLotVO.getLotNumber())
                .setChangeType(ChangedTypeEnum.ADD.getCode());
        return reqLine;
    }

    private List<WipReqLineEntity> parseAddType(Map<String, WipLotVO> wipLotMap) {
        List<WipReqLineEntity> resultList = new ArrayList<>();
        if (StringUtils.isBlank(this.getMoLotNo()) || this.getAimReqLotNo().equals(this.getMoLotNo())) {
            // 小批次为空 或者 小批次=工单批次, 则新增所有批次
            if (Objects.isNull(wipLotMap) || wipLotMap.isEmpty()) {
                throw new ServerException(ReqInsErrEnum.TARGET_LOT_INVALID.getCode(), ReqInsErrEnum.TARGET_LOT_INVALID.getDesc());
            }
            resultList.addAll(wipLotMap.values().stream().map(this::buildReqLine).collect(Collectors.toList()));
        } else {
            // 否则只生成小批次
            WipLotVO filterWipLot = wipLotMap.get(this.getMoLotNo());
            if (Objects.isNull(filterWipLot)) {
                // 若小批次不存在, 报错
                throw new ServerException(ReqInsErrEnum.INVALID_INS.getCode(), ReqInsErrEnum.INVALID_INS.getDesc());
            }
            // 只会筛选出一个有效的小批次
            resultList.add(this.buildReqLine(filterWipLot));
        }
        if (StringUtils.isBlank(this.getWkpNo())) {
            // 如果工序为空, 则设置为默认工序10
            resultList.forEach(line -> line.setWkpNo("10"));
        }
        return ReqInsSplitHelper.allocateQty(this, resultList, wipLotMap, this.getItemUnitQty());
    }

    private List<WipReqLineEntity> parseDeleteType(List<WipReqLineEntity> reqLineList) {
        reqLineList.forEach(line -> line.setChangeType(ChangedTypeEnum.DELETE.getCode()));
        return reqLineList;
    }

    private List<WipReqLineEntity> parseReplaceType(List<WipReqLineEntity> reqLineList, Map<String, WipLotVO> wipLotMap) {
        if (StringUtils.isBlank(this.getItemIdNew())) {
            throw new ServerException(ReqInsErrEnum.KEY_NULL.getCode(), ReqInsErrEnum.KEY_NULL.getDesc() + "替换后物料不可为空");
        }
        // 获取除了物料ID和物料编码外其他主键均相同的投料行，这是因为这些投料行的替换后物料相同，第一个新增以后，后面的会报主键重复错误
        List<WipReqLineEntity> dupLineList = WipReqLineEntity.retrieveDuplicateExceptItem(reqLineList);
        if (ListUtil.notEmpty(dupLineList)) {
            reqLineList.removeAll(dupLineList);
        }
        if (ListUtil.notEmpty(reqLineList)) {
            reqLineList.forEach(line ->
                    line.setBeforeItemNo(line.getItemNo())
                            .setItemNo(this.getItemNoNew())
                            .setChangeType(ChangedTypeEnum.REPLACE.getCode()));
        }

        String originTypeEnum = this.getOperationType();
        WipReqLineEntity increaseLine = null;
        for (int i = 0; i < dupLineList.size(); i++) {
            WipReqLineEntity dupLine = dupLineList.get(i);
            // 减少
            WipReqLineEntity reduceLine = new WipReqLineEntity();
            BeanUtils.copyProperties(dupLine, reduceLine);
            this.setOperationType(InsOperationTypeEnum.REDUCE.getCode());
            reqLineList.addAll(this.parseByType(Collections.singletonList(dupLine), wipLotMap, InsOperationTypeEnum.REDUCE));
            // 增加
            this.setOperationType(InsOperationTypeEnum.INCREASE.getCode());
            List<WipReqLineEntity> increaseLineList = null;
            if (Objects.nonNull(increaseLine)) {
                increaseLine.setChangeType(null);
                increaseLineList = Collections.singletonList(increaseLine);
            }
            List<WipReqLineEntity> increasedLineList = this.parseByType(increaseLineList, wipLotMap, InsOperationTypeEnum.INCREASE);
            WipReqLineEntity increasedLine = increasedLineList.stream()
                    .filter(line -> ChangedTypeEnum.ADD.getCode().equals(line.getChangeType()) || ChangedTypeEnum.INCREASE.getCode().equals(line.getChangeType()))
                    .collect(Collectors.toList())
                    .get(0);
            increaseLine = new WipReqLineEntity();
            BeanUtils.copyProperties(increasedLine, increaseLine);
            reqLineList.addAll(increasedLineList);
        }
        this.setOperationType(originTypeEnum);
        return reqLineList;
    }

    private List<WipReqLineEntity> parseReduceType(List<WipReqLineEntity> reqLineList, Map<String, WipLotVO> wipLotMap) {
        return ReqInsSplitHelper.allocateQty(this, reqLineList, wipLotMap, this.getItemUnitQty().negate());
    }

    @VisibleForTesting
    List<WipReqLineEntity> parseIncreaseType(List<WipReqLineEntity> reqLineList, Map<String, WipLotVO> wipLotMap) {
        if (ListUtil.empty(reqLineList)) {
            // 如果投料行为空, 则新增
            return parseAddType(wipLotMap);
        }
        return ReqInsSplitHelper.allocateQty(this, reqLineList, wipLotMap, this.getItemUnitQty());
    }

    public void processSuccess() {
        this.setInsStatus(ProcessingStatusEnum.SOLVED.getCode());
        this.setConfirmedBy(EntityUtils.getWipUserId());
        this.setConfirmDate(new Date());
        this.setExecuteResult("成功");
        detailRepository.update(this);
    }

    public void batchProcessSuccess(List<ReqInsDetailEntity> insDetailList) {
        for (ReqInsDetailEntity insDetail : insDetailList) {
            insDetail.processSuccess();
        }
    }

    public void processFailed() {
        this.setInsStatus(ProcessingStatusEnum.EXCEPTION.getCode());
        detailRepository.update(this);
    }

    public void batchProcessFailed(List<ReqInsDetailEntity> insDetailList) {
        for (ReqInsDetailEntity insDetail : insDetailList) {
            insDetail.processFailed();
        }
    }

    public void revertStatus() {
        this.setInsStatus(ProcessingStatusEnum.PENDING.getCode())
                .setExecuteResult("")
                .setConfirmedBy("");
        if (StringUtils.isNotBlank(this.getInvalidBy())) {
            // 只还原用户作废的单据
            this.setInvalidBy("").setInvalidReason("");
        }
        detailRepository.update(this);
    }

    public void revertItem() {
        InsOperationTypeEnum operationTypeEnum = CodeableEnumUtils.getCodeableEnumByCode(this.getOperationType(), InsOperationTypeEnum.class);
        this.setOperationType(InsOperationTypeEnum.getOpposite(operationTypeEnum).getCode());
        if (InsOperationTypeEnum.REPLACE.equals(operationTypeEnum)) {
            String tempItemIdOld = this.getItemIdOld();
            String tempItemNoOld = this.getItemNoOld();
            this.setItemIdOld(this.getItemIdNew())
                    .setItemIdNew(tempItemIdOld)
                    .setItemNoOld(this.getItemNoNew())
                    .setItemNoNew(tempItemNoOld);
        }
    }

    @Override
    public String toString() {
        List<String> fieldPrintList = new ArrayList<>();
        BiConsumer<String, String> addNonNull = (p, v) -> {
            if (StringUtils.isNotBlank(v)) {
                fieldPrintList.add(p + "=" + v);
            }
        };
        addNonNull.accept("批次", this.getMoLotNo());
        addNonNull.accept("工序", this.getWkpNo());
        addNonNull.accept("物料", this.getItemNoNew());
        addNonNull.accept("位号", this.getPosNo());
        addNonNull.accept("变更类型", this.getOperationTypeDesc());
        return String.join(",", fieldPrintList);
    }

    private String getOperationTypeDesc() {
        InsOperationTypeEnum typeEnum = CodeableEnumUtils.getCodeableEnumByCode(this.getOperationType(), InsOperationTypeEnum.class);
        if (Objects.isNull(typeEnum)) {
            throw new ServerException(ReqInsErrEnum.INVALID_INS.getCode(), "无效的变更类型");
        }
        return typeEnum.getDesc();
    }

    public static ReqInsDetailEntity get() {
        return DomainFactory.get(ReqInsDetailEntity.class);
    }

}
