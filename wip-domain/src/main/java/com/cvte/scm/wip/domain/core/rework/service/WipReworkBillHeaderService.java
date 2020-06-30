package com.cvte.scm.wip.domain.core.rework.service;

import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.enums.AutoOperationIdentityEnum;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.common.serial.SerialNoGenerationService;
import com.cvte.scm.wip.domain.common.user.entity.UserBaseEntity;
import com.cvte.scm.wip.domain.common.user.service.UserService;
import com.cvte.scm.wip.domain.common.view.entity.PageResultEntity;
import com.cvte.scm.wip.domain.core.rework.entity.WipReworkBillHeaderEntity;
import com.cvte.scm.wip.domain.core.rework.entity.WipReworkBillLineEntity;
import com.cvte.scm.wip.domain.core.rework.entity.WipReworkMoEntity;
import com.cvte.scm.wip.domain.core.rework.repository.WipReworkBillHeaderRepository;
import com.cvte.scm.wip.domain.core.rework.repository.WipReworkBillLineRepository;
import com.cvte.scm.wip.domain.core.rework.repository.WipReworkMoRepository;
import com.cvte.scm.wip.domain.core.rework.valueobject.*;
import com.cvte.scm.wip.domain.core.rework.valueobject.enums.WipMoReworkBillStatusEnum;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 11:19
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipReworkBillHeaderService {

    private static final String SERIAL_CODE = "SCM_WIP_MO_REWORK_BILL_NO";

    private SerialNoGenerationService serialNoGenerationService;
    private WipReworkBillHeaderRepository wipReworkBillHeaderRepository;
    private WipReworkBillLineRepository wipReworkBillLineRepository;
    private WipReworkMoRepository wipReworkMoRepository;
    private WipReworkBillLineService wipReworkBillLineService;
    private OcsRwkBillService ocsRwkBillService;
    private UserService userService;

    public WipReworkBillHeaderService(SerialNoGenerationService serialNoGenerationService, WipReworkBillHeaderRepository wipReworkBillHeaderRepository, WipReworkBillLineRepository wipReworkBillLineRepository, WipReworkMoRepository wipReworkMoRepository, WipReworkBillLineService wipReworkBillLineService, OcsRwkBillService ocsRwkBillService, UserService userService) {
        this.serialNoGenerationService = serialNoGenerationService;
        this.wipReworkBillHeaderRepository = wipReworkBillHeaderRepository;
        this.wipReworkBillLineRepository = wipReworkBillLineRepository;
        this.wipReworkMoRepository = wipReworkMoRepository;
        this.wipReworkBillLineService = wipReworkBillLineService;
        this.ocsRwkBillService = ocsRwkBillService;
        this.userService = userService;
    }

    public List<String> batchCreateBill(WipRwkBillHVO WipRwkBillHVO) {
        List<String> factoryIdList = WipRwkBillHVO.getBillLines().stream().map(WipRwkBillLVO::getFactoryId).distinct().collect(Collectors.toList());
        List<WipReworkBillHeaderEntity> newBillList = new ArrayList<>();
        List<WipReworkBillLineEntity> newBillLineList = new ArrayList<>();
        String userId = CurrentContext.getCurrentOperatingUser().getId();
        for (String factoryId : factoryIdList) {
            // 生成返工单据
            WipReworkBillHeaderEntity newBill = new WipReworkBillHeaderEntity();
            BeanUtils.copyProperties(WipRwkBillHVO, newBill);
            newBill.setBillId(UUIDUtils.get32UUID())
                    .setFactoryId(factoryId)
                    .setBillStatus(Optional.ofNullable(WipRwkBillHVO.getBillStatus()).orElse(WipMoReworkBillStatusEnum.NEW.getCode()));
            EntityUtils.writeStdCrtInfoToEntity(newBill, userId);
            newBillList.add(newBill);
            // 筛选出该工厂的生产批次
            List<WipRwkBillLVO> factoryParamBillLines = WipRwkBillHVO.getBillLines().stream().filter(line -> factoryId.equals(line.getFactoryId())).collect(Collectors.toList());
            // 生产单据行
            List<WipReworkBillLineEntity> factoryNewBillLines = generateBillLineByDTO(factoryParamBillLines, newBill.getBillId(), userId);
            newBillLineList.addAll(factoryNewBillLines);
        }
        String errorMsg = validateBillLot(newBillLineList);
        if (StringUtils.isNotBlank(errorMsg)) {
            log.info(errorMsg);
            throw new ParamsIncorrectException(errorMsg);
        }
        for (WipReworkBillHeaderEntity newBill : newBillList) {
            newBill.setBillNo(serialNoGenerationService.getNextSerialNumberByCode(SERIAL_CODE));
        }
        wipReworkBillHeaderRepository.batchInsertSelective(newBillList);
        wipReworkBillLineRepository.batchInsert(newBillLineList);
        if (ListUtil.notEmpty(newBillList)) {
            return newBillList.stream().map(WipReworkBillHeaderEntity::getBillNo).collect(Collectors.toList());
        }
        return null;
    }

    public void updateBill(WipRwkBillHVO reworkBill) {
        if (Objects.isNull(reworkBill) || StringUtils.isBlank(reworkBill.getBillId())) {
            throw new ParamsIncorrectException("单据ID不可为空");
        }
        if (ListUtil.empty(reworkBill.getBillLines())) {
            throw new ParamsIncorrectException("生产批次列表不可为空");
        }
        WipReworkBillHeaderEntity existReworkBill = wipReworkBillHeaderRepository.selectByBillId(reworkBill.getBillId());

        String userId = CurrentContext.getCurrentOperatingUser().getId();
        // 参数传入的单据行
        List<WipRwkBillLVO> paramLineList = reworkBill.getBillLines();
        List<String> paramLineLotNoList = paramLineList.stream().map(WipRwkBillLVO::getMoLotNo).collect(Collectors.toList());
        // 已保存的单据行
        List<WipReworkBillLineEntity> billLineList = wipReworkBillLineRepository.selectByBillId(reworkBill.getBillId());
        List<WipReworkBillLineEntity> updateLineList = new ArrayList<>();
        if (ListUtil.notEmpty(billLineList)) {
            // 更新的批次
            updateLineList.addAll(billLineList.stream().filter(line -> paramLineLotNoList.contains(line.getMoLotNo())).collect(Collectors.toList()));
            for (WipReworkBillLineEntity updateLine : updateLineList) {
                List<WipRwkBillLVO> lineFilteredByLotNo = paramLineList.stream().filter(line -> line.getMoLotNo().equals(updateLine.getMoLotNo())).collect(Collectors.toList());
                if (lineFilteredByLotNo.size() > 1) {
                    throw new ParamsIncorrectException(String.format("传入的生产批次号【%s】重复", updateLine.getMoLotNo()));
                }
                updateLine.setMoReworkQty(lineFilteredByLotNo.get(0).getMoReworkQty());
            }
            // 删除的批次
            List<WipReworkBillLineEntity> invalidLineList = billLineList.stream().filter(line -> !paramLineLotNoList.contains(line.getMoLotNo())).collect(Collectors.toList());
            for (WipReworkBillLineEntity invalidLine : invalidLineList) {
                invalidLine.setStatus(StatusEnum.CLOSE.getCode());
            }
            // 逻辑删除, 实际操作是更新
            updateLineList.addAll(invalidLineList);
            updateLineList.forEach(line -> EntityUtils.writeStdUpdInfoToEntity(line, userId));
            if (ListUtil.notEmpty(updateLineList)) {
                // 从参数中移除已处理的批次, 剩下的就是新增的批次
                List<String> updateLineLotNoList = updateLineList.stream().map(WipReworkBillLineEntity::getMoLotNo).collect(Collectors.toList());
                paramLineList.removeIf(paramLine -> updateLineLotNoList.contains(paramLine.getMoLotNo()));
            }
        }
        List<WipReworkBillLineEntity> insertLineList = new ArrayList<>();
        if (ListUtil.notEmpty(paramLineList)) {
            // 生成新增的生产批次
            insertLineList.addAll(generateBillLineByDTO(paramLineList, reworkBill.getBillId(), userId));
        }
        // 单据头数据更新
        BeanUtils.copyProperties(reworkBill, existReworkBill);
        EntityUtils.writeStdUpdInfoToEntity(existReworkBill, userId);
        wipReworkBillHeaderRepository.update(existReworkBill);
        // 单据行数据更新
        wipReworkBillLineRepository.batchUpdate(updateLineList);
        wipReworkBillLineRepository.batchInsert(insertLineList);
    }

    public void invalidBill(String billId) {
        if (StringUtils.isBlank(billId)) {
            throw new ParamsIncorrectException("单据ID为空");
        }
        WipReworkBillHeaderEntity reworkBill = wipReworkBillHeaderRepository.selectByBillId(billId);
        String userId = CurrentContext.getCurrentOperatingUser().getId();
        List<WipReworkBillLineEntity> reworkBillLines = wipReworkBillLineRepository.selectByBillId(billId);
        // 作废单据
        reworkBill.setBillStatus(WipMoReworkBillStatusEnum.INVALID.getCode());
        EntityUtils.writeStdUpdInfoToEntity(reworkBill, userId);
        wipReworkBillHeaderRepository.update(reworkBill);
        // 作废单据行
        for (WipReworkBillLineEntity reworkBillLine : reworkBillLines) {
            reworkBillLine.setStatus(StatusEnum.CLOSE.getCode());
            EntityUtils.writeStdUpdInfoToEntity(reworkBillLine, userId);
        }
        wipReworkBillLineRepository.batchUpdate(reworkBillLines);
    }

    public void batchInvalidBill(List<String> billIds) {
        // 去重
        billIds = billIds.stream().distinct().collect(Collectors.toList());
        for (String billId : billIds) {
            invalidBill(billId);
        }
    }

    public PageResultEntity getMoLotList(WipRwkMoVO moParam) {
        List<WipReworkMoEntity> moList = wipReworkMoRepository.selectByParam(moParam);

        PageInfo pageInfo = new PageInfo<>(moList);
        //获取总记录数
        long total = pageInfo.getTotal();
        PageResultEntity page = new PageResultEntity(moParam.getPageSize(), moParam.getPageNum(), total);

        if (ListUtil.empty(moList)) {
            return page;
        }
        Map<String, String> idToNameMap = new HashMap<>();
        List<String> organizationIdList = moList.stream().map(WipReworkMoEntity::getOrganizationId).map(Objects::toString).collect(Collectors.toList());
        List<String> factoryIdList = moList.stream().map(WipReworkMoEntity::getFactoryId).distinct().map(Objects::toString).collect(Collectors.toList());
        List<String> moLotNoList = moList.stream().map(WipReworkMoEntity::getSourceLotNo).distinct().collect(Collectors.toList());
        idToNameMap.putAll(wipReworkBillHeaderRepository.selectOrgAbbrNameMap(organizationIdList));
        idToNameMap.putAll(wipReworkBillHeaderRepository.selectFactoryNameMap(factoryIdList));
        // 按工厂+生产批次累计返工数量
        List<WipRwkBillLVO> sumRwkQtyList = wipReworkBillLineService.sumRwkQty(factoryIdList, moLotNoList);
        // 获取OCS可用量
        List<WipRwkAvailableQtyVO> availableQtyList = ocsRwkBillService.getAvailableQty(moParam, moList);
        List<MoLotVO> moLotVOList = new ArrayList<>();
        for (WipReworkMoEntity mo : moList) {
            MoLotVO moLotVO = new MoLotVO();
            generateMoLotVo(moLotVO, mo);
            long totalRwkQty = 0;
            if (ListUtil.notEmpty(sumRwkQtyList)) {
                // 筛选出 该工厂+生产批次 已返工的数量
                List<WipRwkBillLVO> filterSumRwkQty = sumRwkQtyList.stream().filter(line -> line.getFactoryId().equals(String.valueOf(mo.getFactoryId())) && line.getMoLotNo().equals(mo.getSourceLotNo())).collect(Collectors.toList());
                totalRwkQty = ListUtil.notEmpty(filterSumRwkQty) ? Optional.ofNullable(filterSumRwkQty.get(0).getMoReworkQty()).orElse(0L) : 0;
            }
            long orderReservedQty = 0;
            long ocsReservedQty = 0;
            long moAvailableQty = 0;
            if (ListUtil.notEmpty(availableQtyList)) {
                // 筛选该批次的可用量
                List<WipRwkAvailableQtyVO> moAvailableQtyList = availableQtyList.stream().filter(item -> item.getLot_number().equals(mo.getSourceLotNo())).collect(Collectors.toList());
                if (ListUtil.notEmpty(moAvailableQtyList)) {
                    orderReservedQty = moAvailableQtyList.stream().map(WipRwkAvailableQtyVO::getOrder_reserved_qty)
                            .mapToLong(longValue -> longValue != null ? longValue : 0).sum();
                    ocsReservedQty = moAvailableQtyList.stream().map(WipRwkAvailableQtyVO::getOcs_reserved_qty)
                            .mapToLong(longValue -> longValue != null ? longValue : 0).sum();
                    moAvailableQty = moAvailableQtyList.stream().map(WipRwkAvailableQtyVO::getAvailable_qty)
                            .mapToLong(longValue -> longValue != null ? longValue : 0).sum();
                }
            }
            moLotVO.setOrganizationName(idToNameMap.get(mo.getOrganizationId().toString()))
                    .setFactoryName(idToNameMap.get(String.valueOf(mo.getFactoryId())))
                    .setTotalRwkQty(totalRwkQty)
                    .setOrderReservedQty(orderReservedQty)
                    .setOcsReservedQty(ocsReservedQty)
                    .setAvailableQty(moAvailableQty);
            moLotVOList.add(moLotVO);
        }
        page.setList(moLotVOList);
        return page;
    }

    public List<ApiReworkBillVO> getBillList(ApiRwkBillVO apiRwkBillDTO) {
        List<String> billKeyList = new ArrayList<>();
        if (StringUtils.isBlank(apiRwkBillDTO.getSourceOrderId())) {
            throw new ParamsIncorrectException("订单ID不可为空");
        }
        List<String> billNoList = null;
        if (StringUtils.isNotBlank(apiRwkBillDTO.getBillNos())) {
            billNoList = Arrays.asList(apiRwkBillDTO.getBillNos().split(","));
        }
        List<WipReworkBillHeaderEntity> rwkBillHList = wipReworkBillHeaderRepository.selectBySourceOrderAndBillNo(apiRwkBillDTO.getSourceOrderId(), billNoList);
        if (ListUtil.notEmpty(rwkBillHList)) {
            billKeyList.addAll(rwkBillHList.stream().map(WipReworkBillHeaderEntity::getBillId).collect(Collectors.toList()));
        }
        if (ListUtil.empty(billKeyList)) {
            return null;
        }
        return wipReworkBillHeaderRepository.selectByKeyList(billKeyList);
    }

    public WipRwkBillVO getBillDetail(String billNo) {
        WipRwkBillVO billVO = new WipRwkBillVO();
        // 查单据
        WipReworkBillHeaderEntity bill = wipReworkBillHeaderRepository.selectByBillNo(billNo);
        if (Objects.isNull(bill)) {
            throw new ParamsIncorrectException(String.format("编号【%s】对应的单据不存在", billNo));
        }
        BeanUtils.copyProperties(bill, billVO);
        // 查该单据的所有有效单据行
        List<WipReworkBillLineEntity> billLineList = wipReworkBillLineRepository.selectByBillId(bill.getBillId());
        if (ListUtil.empty(billLineList)) {
            return billVO;
        }
        // 查询 单据对应的 生产批次信息
        String productNos = billLineList.stream().map(WipReworkBillLineEntity::getPriProductNo).collect(Collectors.joining(","));
        WipRwkMoVO queryMo = new WipRwkMoVO()
                .setOcsOrderId(bill.getSourceOrderId())
                .setProductNo(productNos)
                .setFactoryId(bill.getFactoryId())
                .setProductModel(billLineList.get(0).getProductModel())
                .setLotStatus(billLineList.get(0).getMoLotStatus())
                .setNeedPage(false);
        List<MoLotVO> moLotVOList = (List<MoLotVO>)this.getMoLotList(queryMo).getList();
        if (ListUtil.empty(moLotVOList)) {
            log.warn("已生成的单据行无法查询到对应的生产批次");
            return billVO;
        }
        // 已创建单据的所有批次号
        List<String> moLotNoList = billLineList.stream().map(WipReworkBillLineEntity::getMoLotNo).collect(Collectors.toList());
        // 查到的生产批次信息可能 比 该单据创建的单据行要多 所以排除没有在该单据中的生产批次
        moLotVOList.removeIf(moLotVO -> !moLotNoList.contains(moLotVO.getMoLotNo()));
        moLotVOList.forEach(item -> {
            List<WipReworkBillLineEntity> moBillLine = billLineList.stream().filter(line -> line.getMoLotNo().equals(item.getMoLotNo())).collect(Collectors.toList());
            if (ListUtil.notEmpty(moBillLine)) {
                item.setMoReworkQty(moBillLine.get(0).getMoReworkQty())
                        .setRemark(moBillLine.get(0).getRemark());
            }
        });
        billVO.setBillLines(moLotVOList);
        generateUserName(billVO);
        return billVO;
    }

    private void generateMoLotVo(MoLotVO moLotVO, WipReworkMoEntity mo) {
        moLotVO.setOrganizationId(mo.getOrganizationId().toString())
                .setFactoryId(String.valueOf(mo.getFactoryId()))
                .setMoLotNo(mo.getSourceLotNo())
                .setConsumerName(mo.getConsumerName())
                .setProductNo(mo.getProductNo())
                .setProductName(mo.getProductName())
                .setProductModel(mo.getProductModel())
                .setOmName(mo.getOmName())
                .setCustItemNum(mo.getCustItemNum())
                .setMoLotStatus(mo.getLotStatus())
                .setAvailableDays(mo.getAvailableDays())
                .setInvStockQty(mo.getInvStockQty())
                .setOnhandQty(mo.getOnhandQty())
                .setCnQtyRequired(mo.getCnQtyRequired())
                .setReStockQty(mo.getReStockQty());
    }

    private void generateUserName(WipRwkBillVO billVO) {
        if (StringUtils.isNotBlank(billVO.getCrtUser())) {
            String userName = getUserNameById(billVO.getCrtUser());
            if (StringUtils.isNotBlank(userName)) {
                billVO.setCrtUser(userName);
            }
        }
        if (StringUtils.isNotBlank(billVO.getUpdUser())) {
            String userName = getUserNameById(billVO.getUpdUser());
            if (StringUtils.isNotBlank(userName)) {
                billVO.setUpdUser(userName);
            }
        }
    }

    private String getUserNameById(String userId) {
        if (AutoOperationIdentityEnum.EBS.getCode().equals(userId)) {
            return null;
        }
        UserBaseEntity user = userService.getEnableUserInfo(userId);
        if (Objects.nonNull(user)) {
            return user.getName();
        }
        return null;
    }


    /**
     * 根据传入的生产批次生成单据行
     * @since 2020/4/3 11:22 上午
     * @author xueyuting
     * @param paramLineList 用于生成单据行的生产批次
     * @param billId 单据ID
     */
    private List<WipReworkBillLineEntity> generateBillLineByDTO(List<WipRwkBillLVO> paramLineList, String billId, String userId) {
        // 该方法将dto转为entity
        Function<WipRwkBillLVO, WipReworkBillLineEntity> generateLineByDto = line -> {
            WipReworkBillLineEntity newBillLine = new WipReworkBillLineEntity();
            BeanUtils.copyProperties(line, newBillLine);
            // 保存物料编号
            newBillLine.setPriProductNo(line.getProductNo())
                    .setSubProductNo(line.getProductNo());
            EntityUtils.writeStdCrtInfoToEntity(newBillLine, userId);
            newBillLine.setBillLineId(UUIDUtils.get32UUID())
                    .setStatus(StatusEnum.NORMAL.getCode());
            return newBillLine;
        };
        // 生成返工单据行
        List<WipReworkBillLineEntity> newBillLines = paramLineList.stream()
                .map(generateLineByDto)
                .collect(Collectors.toList());
        newBillLines.forEach(line -> line.setBillId(billId));
        return newBillLines;
    }

    /**
     * 校验录入的单据行
     * @since 2020/3/24 9:54 上午
     * @author xueyuting
     * @param billLines 单据行列表
     */
    private String validateBillLot(List<WipReworkBillLineEntity> billLines) {
        StringBuilder errorMsg = new StringBuilder();
        // 校验是否有重复录入的生产批次
        List<String> lotNoList = billLines.stream().collect(Collectors.groupingBy(WipReworkBillLineEntity::getMoLotNo, Collectors.counting()))
                .entrySet().stream()
                .filter(item -> item.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        if (ListUtil.notEmpty(lotNoList)) {
            errorMsg.append("录入的生产批次【").append(String.join(",", lotNoList)).append("】重复\n");
        }
        return errorMsg.toString();
    }

}
