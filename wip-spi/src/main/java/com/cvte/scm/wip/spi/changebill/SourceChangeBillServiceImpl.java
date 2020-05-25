package com.cvte.scm.wip.spi.changebill;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
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
            innerParamMap.put("lastUpdDate", queryVO.getLastUpdDate());
        }
        String innerParamStr = JSON.toJSONString(innerParamMap);
        Map<String, Object> outerParamMap = new HashMap<>();
        outerParamMap.put("paramsJson", innerParamStr);
        String restResponse = RestCallUtils.callRest(RestCallUtils.RequestMethod.GET, url, outerParamMap);

        EbsResponse<List<SourceChangeBillDTO>> ebsResponse = JSON.parseObject(restResponse, new TypeReference<EbsResponse<List<SourceChangeBillDTO>>>(){});
        if (!"S".equals(ebsResponse.getRtStatus())) {
            throw new ServerException("", "EBS接口请求异常");
        }
        List<SourceChangeBillDTO> sourceBillList = ebsResponse.getRtData();
        filterChangedBill(sourceBillList);
        return sourceBillList;
    }

    private void filterChangedBill(List<SourceChangeBillDTO> sourceBillList) {
        Iterator<SourceChangeBillDTO> iterator = sourceBillList.iterator();
        while (iterator.hasNext()) {
            SourceChangeBillDTO sourceBill = iterator.next();
            WipCnBillDO queryBill = new WipCnBillDO();
            queryBill.setBillNo(sourceBill.getBillNo());
            List<WipCnBillDO> billDOList = wipCnBillMapper.select(queryBill);
            if (ListUtil.empty(billDOList)) {
                continue;
            }
            WipCnBillDO billDO = billDOList.get(0);
            // 存在ID
            sourceBill.setBillId(billDO.getBillId());
            if (billDO.getUpdTime().equals(sourceBill.getLastUpdDate())) {
                iterator.remove();
            }
        }
    }

    @VisibleForTesting
    List<ChangeBillBuildVO> parseDtoToVo(List<SourceChangeBillDTO> sourceBillList) {
        List<ChangeBillBuildVO> billBuildVOList = new ArrayList<>();

        Map<String, List<SourceChangeBillDTO>> sourceBillMap = sourceBillList.stream()
                .collect(Collectors.groupingBy(bill -> bill.getBillId() + bill.getOrganizationId()));
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
        if (StringUtils.isBlank(posNo) || !posNo.contains(splitter)) {
            resultList.add(vo);
            return resultList;
        }

        String[] posNoArr = posNo.split(splitter);
        BigDecimal itemQty = vo.getItemQty();
        if (Objects.isNull(itemQty)) {
            itemQty = BigDecimal.ZERO;
        }
        final BigDecimal originQty = itemQty;
        // 拆分后向上取整, 按顺序分配到位号上
        BigDecimal splitQty = itemQty.divide(new BigDecimal(posNoArr.length), 0, RoundingMode.CEILING);
        for (String splitPosNo : posNoArr) {
            BigDecimal allocateQty = itemQty.min(splitQty);
            BigDecimal allocateUnitQty = allocateQty.divide(originQty, 6, RoundingMode.DOWN);

            ChangeBillDetailBuildVO splitDetailBuildVo = new ChangeBillDetailBuildVO();
            BeanUtils.copyProperties(vo, splitDetailBuildVo);
            splitDetailBuildVo.setPosNo(splitPosNo.trim())
                    .setItemQty(allocateQty)
                    .setItemUnitQty(allocateUnitQty);

            resultList.add(splitDetailBuildVo);
            itemQty = itemQty.subtract(allocateQty);
        }
        return resultList;
    }

}
