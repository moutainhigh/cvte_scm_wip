package com.cvte.scm.wip.domain.core.rework.service;

import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.enums.AutoOperationIdentityEnum;
import com.cvte.scm.wip.common.enums.error.ReworkBillErrEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.rework.entity.WipReworkBillHeaderEntity;
import com.cvte.scm.wip.domain.core.rework.entity.WipReworkBillLineEntity;
import com.cvte.scm.wip.domain.core.rework.repository.WipReworkBillHeaderRepository;
import com.cvte.scm.wip.domain.core.rework.repository.WipReworkBillLineRepository;
import com.cvte.scm.wip.domain.core.rework.valueobject.*;
import com.cvte.scm.wip.domain.core.rework.valueobject.enums.WipMoReworkBillStatusEnum;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/24 11:50
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class WipReworkBillSyncService {

    private WipReworkBillHeaderRepository wipReworkBillHeaderRepository;
    private WipReworkBillLineRepository wipReworkBillLineRepository;
    private EbsReworkBillHeaderService ebsReworkBillHeaderService;

    public WipReworkBillSyncService(WipReworkBillHeaderRepository wipReworkBillHeaderRepository, WipReworkBillLineRepository wipReworkBillLineRepository, EbsReworkBillHeaderService ebsReworkBillHeaderService) {
        this.wipReworkBillHeaderRepository = wipReworkBillHeaderRepository;
        this.wipReworkBillLineRepository = wipReworkBillLineRepository;
        this.ebsReworkBillHeaderService = ebsReworkBillHeaderService;
    }

    public String syncBillFromEbs(Date lastDate, String billNo) {
        if (Objects.isNull(lastDate) && StringUtils.isBlank(billNo)) {
            throw new ParamsIncorrectException("参数不可全为空");
        }
        // 参数准备
        Map<String, String> paramMap = new HashMap<>();
        if (Objects.nonNull(lastDate)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String lastDateStr = simpleDateFormat.format(lastDate);
            paramMap.put("lastDate", lastDateStr);
        }
        if (Objects.nonNull(billNo)) {
            paramMap.put("piBillNo", billNo);
        }

        // 获取ebs数据
        List<EbsReworkBillQueryVO> ebsBillHList = ebsReworkBillHeaderService.getEbsRwkBillH(paramMap);
        if (ListUtil.empty(ebsBillHList)) {
            return "EBS数据无更新";
        }
        ebsBillHList.forEach(ebsBillH -> ebsBillH.setBill_no(StringUtils.sub(ebsBillH.getBill_no(), 0, 14)));

        // 批量更新
        List<String> billNoList = ebsBillHList.stream().map(EbsReworkBillQueryVO::getBill_no).collect(Collectors.toList());
        List<WipReworkBillHeaderEntity> billHeaderList = wipReworkBillHeaderRepository.selectByBillNo(billNoList);
        List<WipReworkBillHeaderEntity> updatedBillHList = filterUpdatedBillH(billHeaderList, ebsBillHList);
        if (ListUtil.notEmpty(updatedBillHList)) {
            wipReworkBillHeaderRepository.batchUpdate(updatedBillHList);
            return String.format("同步EBS数据成功, 共%d条", updatedBillHList.size());
        }
        return "EBS数据无更新";
    }

    public String syncBillToEbs(List<String> billNoList) {
        List<WipReworkBillHeaderEntity> billHList = wipReworkBillHeaderRepository.selectByBillNo(billNoList);
        // 校验是否有无效的单据号
        List<ErrorMsgVO> errMsgDTOList = new ArrayList<>();
        List<ErrorMsgVO> warningDTOList = new ArrayList<>();
        List<String> notExistNoList = existBillListContainNos(billHList, billNoList);
        if (ListUtil.notEmpty(notExistNoList)) {
            notExistNoList.forEach(no -> errMsgDTOList.add(new ErrorMsgVO().setId(no).setMessage("不存在")));
            throw new ParamsIncorrectException(ErrorMsgVO.toMsg(errMsgDTOList));
        }
        // 获取所有单据行
        List<String> billIdList = billHList.stream().map(WipReworkBillHeaderEntity::getBillId).collect(Collectors.toList());
        List<WipReworkBillLineEntity> billLList = wipReworkBillLineRepository.selectByBillId(billIdList);
        // 同步返工单到EBS
        for (WipReworkBillHeaderEntity billH : billHList) {
            if (!(WipMoReworkBillStatusEnum.NEW.getCode().equals(billH.getBillStatus()) || WipMoReworkBillStatusEnum.REJECT.getCode().equals(billH.getBillStatus()))) {
                // 非新建或驳回状态的单据不需要同步(用户更新唛头)
                warningDTOList.add(new ErrorMsgVO().setId(billH.getBillNo()).setMessage("单据已提交EBS, 仅更新唛头"));
                continue;
            }
            String errorMsg = ebsReworkBillHeaderService.syncToEbs(billH, billLList);
            if (StringUtils.isNotBlank(errorMsg)) {
                errMsgDTOList.add(new ErrorMsgVO().setId(billH.getBillNo()).setMessage(errorMsg));
            }

            billH.setBillStatus(WipMoReworkBillStatusEnum.SUBMIT.getCode());
            EntityUtils.writeStdUpdInfoToEntity(billH, EntityUtils.getWipUserId());
        }
        if (ListUtil.notEmpty(errMsgDTOList)) {
            throw new ServerException(ReworkBillErrEnum.SYNC_TO_EBS.getCode(), ReworkBillErrEnum.SYNC_TO_EBS.getDesc() + ",原因:" + ErrorMsgVO.toMsg(errMsgDTOList));
        }

        wipReworkBillHeaderRepository.batchUpdate(billHList);
        if (ListUtil.notEmpty(warningDTOList)) {
            return ErrorMsgVO.toMsg(warningDTOList);
        } else {
            return "单据已成功提交至EBS";
        }
    }

    /**
     * 筛选并更新WIP的返工单
     * @since 2020/4/14 4:07 下午
     * @author xueyuting
     */
    private List<WipReworkBillHeaderEntity> filterUpdatedBillH(List<WipReworkBillHeaderEntity> wipBillHList, List<EbsReworkBillQueryVO> ebsBillHList) {
        if (ListUtil.empty(wipBillHList)) {
            return null;
        }
        List<WipReworkBillHeaderEntity> updatedBillHList = new ArrayList<>();
        // 过滤SCM不存在的数据
        List<String> billNoList = wipBillHList.stream().map(WipReworkBillHeaderEntity::getBillNo).collect(Collectors.toList());
        List<EbsReworkBillQueryVO> filterEbsBillHList = ebsBillHList.stream().filter(bill -> billNoList.contains(bill.getBill_no())).collect(Collectors.toList());
        // 造map, 方便获取对象
        Map<String, EbsReworkBillQueryVO> ebsBillHMap = new HashMap<>(filterEbsBillHList.size());
        for (EbsReworkBillQueryVO ebsBillH : filterEbsBillHList) {
            ebsBillHMap.put(ebsBillH.getBill_no(), ebsBillH);
        }
        // 更新数据
        for (WipReworkBillHeaderEntity wipBillH : wipBillHList){
            EbsReworkBillQueryVO ebsBillH = ebsBillHMap.get(wipBillH.getBillNo());
            if (wipBillH.getUpdDate().equals(ebsBillH.getLast_update_date())) {
                // 更新时间相同, 无需更新
                continue;
            }
            WipReworkBillHeaderEntity updatedBillH = new WipReworkBillHeaderEntity().setBillId(wipBillH.getBillId());
            this.updateBillHByDTO(updatedBillH, ebsBillH);
            updatedBillHList.add(updatedBillH);
        }
        return updatedBillHList;
    }

    /**
     * 更新WIP返工单
     * @since 2020/4/14 4:08 下午
     * @author xueyuting
     */
    private void updateBillHByDTO(WipReworkBillHeaderEntity wipBillH, EbsReworkBillQueryVO ebsBillH) {
        wipBillH.setBillStatus(ebsBillH.getBill_status())
                .setReworkType(ebsBillH.getRework_type())
                .setReworkReasonType(ebsBillH.getRework_reason_type())
                .setReworkReason(ebsBillH.getRework_reason())
                .setReworkDesc(ebsBillH.getRework_desc())
                .setExpectFinishDate(ebsBillH.getDate_required())
                .setExpectDeliveryDate(ebsBillH.getProduct_out_date())
                .setRejectDealType(ebsBillH.getRejects_deal_type())
                .setGoodDealType(ebsBillH.getGoods_deal_type())
                .setRejectMtrDealType(ebsBillH.getReject_mtr_deal_type())
                .setGoodMtrDealType(ebsBillH.getGood_mtr_deal_type())
                .setUpdDate(ebsBillH.getLast_update_date())
                .setUpdUser(AutoOperationIdentityEnum.EBS.getCode());
    }

    /**
     * 返回不存在的返工单单据号
     * @since 2020/4/9 2:19 下午
     * @author xueyuting
     */
    private List<String> existBillListContainNos(List<WipReworkBillHeaderEntity> existBillList, List<String> billNoList) {
        if (ListUtil.empty(existBillList)) {
            return billNoList;
        }
        List<String> existBillNoList = existBillList.stream().map(WipReworkBillHeaderEntity::getBillNo).collect(Collectors.toList());
        return billNoList.stream().filter(billNo -> !existBillNoList.contains(billNo)).collect(Collectors.toList());
    }

}
