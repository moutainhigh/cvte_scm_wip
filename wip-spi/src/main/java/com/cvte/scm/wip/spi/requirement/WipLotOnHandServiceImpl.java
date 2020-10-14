package com.cvte.scm.wip.spi.requirement;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
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

    @Override
    public List<WipMtrSubInvVO> getOnHand(String organizationId, String factoryId, String itemId, String[] lotNumbers) {
        List<WipMtrSubInvVO> subInvVOList = new ArrayList<>();
        String orgId = sysOrganizationMapper.getOrgIdById(organizationId);

        String iacToken = getIacToken();
        String poUrl = csbpApiInfoConfiguration.getBaseUrl() + "/po/getAsnDetail";
        String inoutUrl = csbpApiInfoConfiguration.getBaseUrl() + "/inv/inoutstock/inoutStock";
        for (String lotNumber : lotNumbers) {
            // 查询PO.ASN单据 https://kb.cvte.com/pages/viewpage.action?pageId=125166990
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("orgId", orgId);
            paramMap.put("itemId", itemId);
            paramMap.put("batchNumber", lotNumber);
            List<Map<String, Object>> poResponse = requestAndGetData(poUrl, iacToken, paramMap);
            boolean exists = false;
            for (Map<String, Object> data : poResponse) {
                String vendorLotNum = (String)data.get("vendorLotNum");
                if (lotNumber.equals(vendorLotNum)) {
                    BigDecimal supplyQty = new BigDecimal(Optional.ofNullable(data.get("quantityShipped")).orElse(0).toString());
                    subInvVOList.add(this.buildSubInvVO(organizationId, factoryId, (String)data.get("projectNumber"), itemId, vendorLotNum, supplyQty));
                    exists = true;
                }
            }
            if (exists) {
                continue;
            }

            // 查询INV.其他出入库单据 https://kb.cvte.com/pages/viewpage.action?pageId=131482997
            paramMap.put("organizationId", organizationId);
            List<Map<String, Object>> inoutResponse = requestAndGetData(inoutUrl, iacToken, paramMap);
            if (ListUtil.empty(inoutResponse)) {
                continue;
            }
            for (Map<String, Object> data : inoutResponse) {
                String lot = (String)data.get("lot");
                if (lotNumber.equals(lot)) {
                    BigDecimal supplyQty = new BigDecimal(Optional.ofNullable(data.get("planQty")).orElse(0).toString());
                    subInvVOList.add(this.buildSubInvVO(organizationId, factoryId, (String)data.get("projectNumber"), itemId, lot, supplyQty));
                }
            }
        }

        return subInvVOList;
    }

    private List<Map<String, Object>> requestAndGetData(String url, String iacToken, Map<String, Object> paramMap) {
        String poResponseStr = RestCallUtils.callRest(RestCallUtils.RequestMethod.GET, url, iacToken, paramMap);

        CommonResponse<List<Map<String, Object>>> poResponse = JSON.parseObject(poResponseStr, new TypeReference<CommonResponse<List<Map<String, Object>>>>(){});
        checkSuccess(poResponse);
        return poResponse.getData();
    }

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
