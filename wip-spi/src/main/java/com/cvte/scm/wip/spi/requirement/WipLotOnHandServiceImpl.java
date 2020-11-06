package com.cvte.scm.wip.spi.requirement;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.common.deprecated.RestCallUtils;
import com.cvte.scm.wip.domain.common.token.service.AccessTokenService;
import com.cvte.scm.wip.domain.core.requirement.service.WipLotOnHandService;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrSubInvVO;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.exception.EbsInvokeException;
import com.cvte.scm.wip.infrastructure.boot.config.api.CsbpApiInfoConfiguration;
import com.cvte.scm.wip.infrastructure.sys.org.mapper.SysOrganizationMapper;
import com.cvte.scm.wip.spi.common.CommonResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/10/13 10:58
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class WipLotOnHandServiceImpl implements WipLotOnHandService {

    private AccessTokenService accessTokenService;
    private CsbpApiInfoConfiguration csbpApiInfoConfiguration;
    private SysOrganizationMapper sysOrganizationMapper;

    public WipLotOnHandServiceImpl(AccessTokenService accessTokenService, CsbpApiInfoConfiguration csbpApiInfoConfiguration, SysOrganizationMapper sysOrganizationMapper) {
        this.accessTokenService = accessTokenService;
        this.csbpApiInfoConfiguration = csbpApiInfoConfiguration;
        this.sysOrganizationMapper = sysOrganizationMapper;
    }

    /**
     * 获取在途批次
     * @since 2020/11/6 3:55 下午
     * @author xueyuting
     */
    @Override
    public List<WipMtrSubInvVO> getOnHand(String organizationId, String itemId, Collection<String> lotNumbers) {
        // 查询PO.ASN单据 https://kb.cvte.com/pages/viewpage.action?pageId=125166990
        List<WipMtrSubInvVO> poAsnList = getPoAsn(organizationId, itemId, lotNumbers);
        List<WipMtrSubInvVO> onHandList = new ArrayList<>(poAsnList);

        // 查询INV.其他出入库单据 https://kb.cvte.com/pages/viewpage.action?pageId=131482997
        List<WipMtrSubInvVO> inoutList = getInout(organizationId, itemId, lotNumbers);
        onHandList.addAll(inoutList);

        return onHandList;
    }

    /**
     * 获取ASN在途批次
     * @since 2020/11/6 3:55 下午
     * @author xueyuting
     */
    private List<WipMtrSubInvVO> getPoAsn(String organizationId, String itemId, Collection<String> lotNumbers) {
        List<WipMtrSubInvVO> subInvVOList = new ArrayList<>();
        String orgId = sysOrganizationMapper.getOrgIdById(organizationId);
        String iacToken = getIacToken();
        String poUrl = csbpApiInfoConfiguration.getBaseUrl() + "/po/getAsnDetail";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("orgId", orgId);
        paramMap.put("itemId", itemId);
        paramMap.put("shipmentLineStatusCode", "EXPECTED,PARTIALLY RECEIVED");
        List<Map<String, Object>> poAsnLotList = requestAndGetData(poUrl, iacToken, paramMap, lotNumbers);

        for (Map<String, Object> data : poAsnLotList) {
            String vendorLotNum = (String)data.get("vendorLotNum");
            BigDecimal supplyQty = new BigDecimal(Optional.ofNullable(data.get("quantityShipped")).orElse(0).toString());
            subInvVOList.add(this.buildSubInvVO(organizationId, mapObjectToString(data.get("projectId")), mapObjectToString(data.get("projectNumber")), itemId, vendorLotNum, supplyQty));
        }
        return subInvVOList;
    }

    /**
     * 获取其他出入库的批次
     * @since 2020/11/6 3:55 下午
     * @author xueyuting
     */
    private List<WipMtrSubInvVO> getInout(String organizationId, String itemId, Collection<String> lotNumbers) {
        List<WipMtrSubInvVO> subInvVOList = new ArrayList<>();
        String iacToken = getIacToken();
        String inoutUrl = csbpApiInfoConfiguration.getBaseUrl() + "/inv/inoutstock/inoutStock";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("organizationId", organizationId);
        paramMap.put("itemId", itemId);
        paramMap.put("status", "BOOKED"); // 已确认状态的单据
        List<Map<String, Object>> inoutLotList = requestAndGetData(inoutUrl, iacToken, paramMap, lotNumbers);

        for (Map<String, Object> data : inoutLotList) {
            String lot = (String)data.get("lot");
            BigDecimal supplyQty = new BigDecimal(Optional.ofNullable(data.get("planQty")).orElse(0).toString());
            WipMtrSubInvVO inoutVO = this.buildSubInvVO(organizationId, mapObjectToString(data.get("projectId")), mapObjectToString(data.get("projectNumber")), itemId, lot, supplyQty);
            inoutVO.setLocatorCode((String)data.get("locatorCodeL"));
            subInvVOList.add(inoutVO);
        }
        return subInvVOList;
    }

    /**
     * 查询并返回接口数据
     * @since 2020/11/6 3:54 下午
     * @author xueyuting
     */
    private List<Map<String, Object>> requestAndGetData(String url, String iacToken, Map<String, Object> paramMap, Collection<String> lotNumbers) {
        String poResponseStr = "";
        if (CollectionUtils.isNotEmpty(lotNumbers)) {
            for (String lotNumber : lotNumbers) {
                paramMap.put("batchNumber", lotNumber);
                poResponseStr = RestCallUtils.callRest(RestCallUtils.RequestMethod.GET, url, iacToken, paramMap);
            }
        } else {
            fillTimeParam(paramMap);
            poResponseStr = RestCallUtils.callRest(RestCallUtils.RequestMethod.GET, url, iacToken, paramMap);
        }

        CommonResponse<List<Map<String, Object>>> response = JSON.parseObject(poResponseStr, new TypeReference<CommonResponse<List<Map<String, Object>>>>(){});
        checkSuccess(response);
        List<Map<String, Object>> responseData = response.getData();
        if (Objects.isNull(responseData)) {
            return Collections.emptyList();
        }
        return responseData;
    }

    /**
     * 为请求参数填充查询的起始和截止时间
     * @since 2020/11/6 3:53 下午
     * @author xueyuting
     */
    private void fillTimeParam(Map<String, Object> paramMap) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthBefore = now.minusMonths(1);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        paramMap.put("lastUpdateDateFr", df.format(oneMonthBefore));
        paramMap.put("lastUpdateDateTo", df.format(now));
    }

    /**
     * 生成在途实体
     * @since 2020/11/6 3:53 下午
     * @author xueyuting
     */
    private WipMtrSubInvVO buildSubInvVO(String organizationId, String factoryId, String factoryNo, String itemId, String lotNumber, BigDecimal supplyQty) {
        WipMtrSubInvVO subInvVO = new WipMtrSubInvVO();
        subInvVO.setOrganizationId(organizationId)
                .setFactoryId(factoryId)
                .setFactoryNo(factoryNo)
                .setInventoryItemId(itemId)
                .setLotNumber(lotNumber)
                .setSupplyQty(supplyQty);
        return subInvVO;
    }

    private void checkSuccess(CommonResponse commonResponse) {
        if (!"0".equals(commonResponse.getStatus())) {
            throw new EbsInvokeException(commonResponse.getMessage());
        }
    }

    private String mapObjectToString(Object data) {
        if (Objects.nonNull(data)) {
            return data.toString();
        }
        return "";
    }

    private String getIacToken() {
        String iacToken;
        try {
            iacToken = accessTokenService.getAccessToken();
        } catch (Exception e) {
            throw new ServerException("", "IAC鉴权服务不可用,无法获取在途数据");
        }
        return iacToken;
    }

}
