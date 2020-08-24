package com.cvte.scm.wip.spi.changebill;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.enums.StatusEnum;
import com.cvte.scm.wip.domain.common.deprecated.RestCallUtils;
import com.cvte.scm.wip.domain.common.token.service.AccessTokenService;
import com.cvte.scm.wip.domain.core.changebill.service.SourceChangeBillService;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillBuildVO;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillDetailBuildVO;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillQueryVO;
import com.cvte.scm.wip.infrastructure.boot.config.api.EbsApiInfoConfiguration;
import com.cvte.scm.wip.infrastructure.changebill.mapper.WipCnBillMapper;
import com.cvte.scm.wip.infrastructure.changebill.mapper.dataobject.WipCnBillDO;
import com.cvte.scm.wip.spi.changebill.DTO.SourceChangeBillDTO;
import com.cvte.scm.wip.spi.common.EbsResponse;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 11:20
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class SourceChangeBillServiceImpl implements SourceChangeBillService {

    private WipCnBillMapper wipCnBillMapper;
    private AccessTokenService accessTokenService;
    private EbsApiInfoConfiguration ebsApiInfoConfiguration;

    public SourceChangeBillServiceImpl(WipCnBillMapper wipCnBillMapper, AccessTokenService accessTokenService, EbsApiInfoConfiguration ebsApiInfoConfiguration) {
        this.wipCnBillMapper = wipCnBillMapper;
        this.accessTokenService = accessTokenService;
        this.ebsApiInfoConfiguration = ebsApiInfoConfiguration;
    }

    @Override
    public List<ChangeBillBuildVO> querySourceChangeBill(ChangeBillQueryVO queryVO) {
        List<SourceChangeBillDTO> changeBillDTOList = requestEbsChangeBill(queryVO);
        if (ListUtil.empty(changeBillDTOList)) {
            return Collections.emptyList();
        }
        return parseDtoToVo(changeBillDTOList);
    }

    private List<SourceChangeBillDTO> requestEbsChangeBill(ChangeBillQueryVO queryVO) {
        String iacToken;
        try {
            iacToken = accessTokenService.getAccessToken();
        } catch (Exception e) {
            throw new ServerException("", "IAC鉴权服务不可用,无法从EBS同步返工单数据");
        }
        String url = ebsApiInfoConfiguration.getBaseUrl() + "/xxfnd/pubquery/getMoChangeOrder";
        Map<String, Object> innerParamMap = new HashMap<>();
        innerParamMap.put("token", iacToken);
        if (Objects.nonNull(queryVO.getOrganizationId())) {
            innerParamMap.put("organizationId", queryVO.getOrganizationId());
        }
        if (Objects.nonNull(queryVO.getLastUpdDate())) {
            String lastUpdDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(queryVO.getLastUpdDate());
            innerParamMap.put("lastUpdDate", lastUpdDate);
        }
        String innerParamStr = JSON.toJSONString(innerParamMap);
        Map<String, Object> outerParamMap = new HashMap<>();
        outerParamMap.put("paramsJson", innerParamStr);
        String restResponse = RestCallUtils.callRest(RestCallUtils.RequestMethod.GET, url, outerParamMap);

        EbsResponse<List<SourceChangeBillDTO>> ebsResponse = JSON.parseObject(restResponse, new TypeReference<EbsResponse<List<SourceChangeBillDTO>>>(){});
        if (!"S".equals(ebsResponse.getRtStatus())) {
            throw new ServerException("", "EBS接口请求异常");
        }
        return ebsResponse.getRtData();
    }

    private void filterChangedBill(Map<String, List<SourceChangeBillDTO>> sourceBillMap) {
        Iterator<Map.Entry<String, List<SourceChangeBillDTO>>> iterator = sourceBillMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<SourceChangeBillDTO>> sourceBillEntry = iterator.next();
            // 取更改单号相同的一个项 查询是否已有更改单
            SourceChangeBillDTO sourceBill = sourceBillEntry.getValue().get(0);
            WipCnBillDO queryBill = new WipCnBillDO();
            queryBill.setBillNo(sourceBill.getBillNo());
            queryBill.setOrganizationId(sourceBill.getOrganizationId());
            queryBill.setBillStatus(StatusEnum.NORMAL.getCode());
            List<WipCnBillDO> billDOList = wipCnBillMapper.select(queryBill);
            if (ListUtil.empty(billDOList)) {
                if (StatusEnum.CLOSE.getCode().equals(sourceBill.getBillStatus())) {
                    // 如果不存在有效的更改单, 且同步的更改单状态为 关闭, 则不处理
                    iterator.remove();
                }
                continue;
            }
            // 存在则更新其ID
            WipCnBillDO billDO = billDOList.get(0);
            sourceBillEntry.getValue().forEach(item -> item.setBillId(billDO.getBillId()));
            if (billDO.getUpdTime().equals(sourceBill.getLastUpdDate())) {
                iterator.remove();
            }
        }
    }

    @VisibleForTesting
    List<ChangeBillBuildVO> parseDtoToVo(List<SourceChangeBillDTO> sourceBillList) {
        List<ChangeBillBuildVO> billBuildVOList = new ArrayList<>();

        Map<String, List<SourceChangeBillDTO>> sourceBillMap = sourceBillList.stream()
                .collect(Collectors.groupingBy(bill -> bill.getBillNo() + bill.getOrganizationId()));
        this.filterChangedBill(sourceBillMap);

        for (Map.Entry<String, List<SourceChangeBillDTO>> entry : sourceBillMap.entrySet()) {
            List<SourceChangeBillDTO> sourceChangeBillDTOList = entry.getValue();

            // 随机取一个, 生成更改单头构造对象
            SourceChangeBillDTO randomOne = sourceChangeBillDTOList.get(0);
            ChangeBillBuildVO billBuildVO = SourceChangeBillDTO.buildHeaderVO(randomOne);

            // 生成所有更改单行构造对象
            List<ChangeBillDetailBuildVO> billDetailBuildVOList = new ArrayList<>();
            for (SourceChangeBillDTO sourceChangeBillDTO : sourceChangeBillDTOList) {
                ChangeBillDetailBuildVO detailBuildVO = SourceChangeBillDTO.buildDetailVO(sourceChangeBillDTO);
                detailBuildVO.setBillId(billBuildVO.getBillId());
                // EBS位号非结构化, 需要拆分
                List<ChangeBillDetailBuildVO> detailListSplitByPos = splitBillDetailByPos(detailBuildVO, ",");
                billDetailBuildVOList.addAll(detailListSplitByPos);
            }

            billBuildVO.setDetailVOList(billDetailBuildVOList);
            billBuildVOList.add(billBuildVO);
        }
        return billBuildVOList;
    }

    @VisibleForTesting
    List<ChangeBillDetailBuildVO> splitBillDetailByPos(ChangeBillDetailBuildVO vo, String splitter) {
        List<ChangeBillDetailBuildVO> resultList = new ArrayList<>();
        String posNo = vo.getPosNo();
        if (StringUtils.isBlank(posNo)) {
            posNo = "";
        }

        List<String> posNoList = Arrays.stream(posNo.split(splitter)).map(String::trim).collect(Collectors.toList());
        // 拆分后向上取整, 按顺序分配到位号上
        Map<String, BigDecimal> posItemQtyMap = splitQtyByPos(posNoList, vo.getItemQty(), 0);
        Map<String, BigDecimal> posUnitQtyMap = splitQtyByPos(posNoList, vo.getItemUnitQty(), 8);
        for (String splitPosNo : posNoList) {
            ChangeBillDetailBuildVO splitDetailBuildVo = new ChangeBillDetailBuildVO();
            BeanUtils.copyProperties(vo, splitDetailBuildVo);
            splitDetailBuildVo.setPosNo(StringUtils.isBlank(splitPosNo) ? null : splitPosNo.trim())
                    .setItemQty(posItemQtyMap.get(splitPosNo))
                    .setItemUnitQty(posUnitQtyMap.get(splitPosNo));

            resultList.add(splitDetailBuildVo);
        }
        return resultList;
    }

    private Map<String, BigDecimal> splitQtyByPos(List<String> posNoList, BigDecimal totalQty, Integer scale) {
        Map<String, BigDecimal> posQtyMap = new HashMap<>();
        if (Objects.isNull(totalQty)) {
            posNoList.forEach(posNo -> posQtyMap.put(posNo, null));
            return posQtyMap;
        }
        // 取整用于分配的数量
        BigDecimal remainQty = totalQty.setScale(scale + 1, RoundingMode.DOWN).setScale(scale, RoundingMode.UP);
        // 拆分后向下取整, 按顺序分配到位号上
        // 向下取整的原因: 3个位号用量为4, 若向上取整, 则前两个位号为2, 最后一个位号用量为0, 不合理
        BigDecimal splitQty = remainQty.divide(new BigDecimal(posNoList.size()), scale, RoundingMode.DOWN);
        Iterator<String> iterator = posNoList.iterator();
        while (iterator.hasNext()) {
            String splitPosNo = iterator.next();
            BigDecimal allocateQty = splitQty;
            if (!iterator.hasNext()) {
                // 最后一个时分配剩余数量
                allocateQty = remainQty;
            }
            posQtyMap.put(splitPosNo, allocateQty);
            remainQty = remainQty.subtract(allocateQty);
        }
        return posQtyMap;
    }

}
