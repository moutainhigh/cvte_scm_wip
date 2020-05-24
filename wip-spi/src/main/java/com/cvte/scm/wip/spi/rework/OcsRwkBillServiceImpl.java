package com.cvte.scm.wip.spi.rework;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.common.deprecated.RestCallUtils;
import com.cvte.scm.wip.domain.common.token.service.AccessTokenService;
import com.cvte.scm.wip.domain.core.rework.entity.WipReworkMoEntity;
import com.cvte.scm.wip.domain.core.rework.service.OcsRwkBillService;
import com.cvte.scm.wip.domain.core.rework.valueobject.WipRwkAvailableQtyVO;
import com.cvte.scm.wip.domain.core.rework.valueobject.WipRwkMoVO;
import com.cvte.scm.wip.domain.core.rework.valueobject.enums.WipMoReworkLotStatusEnum;
import com.cvte.scm.wip.infrastructure.boot.config.api.OcsApiInfoConfiguration;
import com.cvte.scm.wip.spi.rework.DTO.OcsResponseDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/24 16:18
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class OcsRwkBillServiceImpl implements OcsRwkBillService {

    private AccessTokenService accessTokenService;
    private OcsApiInfoConfiguration ocsApiInfoConfiguration;

    public OcsRwkBillServiceImpl(AccessTokenService accessTokenService, OcsApiInfoConfiguration ocsApiInfoConfiguration) {
        this.accessTokenService = accessTokenService;
        this.ocsApiInfoConfiguration = ocsApiInfoConfiguration;
    }

    @Override
    public List<WipRwkAvailableQtyVO> getAvailableQty(WipRwkMoVO rwkMoDTO, List<WipReworkMoEntity> rwkMoList) {
        if (StringUtils.isBlank(rwkMoDTO.getOcsOrderId())) {
            return null;
        }
        String token;
        try {
            token = accessTokenService.getAccessToken();
        } catch (Exception e) {
            throw new ServerException("", "IAC鉴权服务不可用,无法获取OCS可用量数据");
        }

        List<WipRwkAvailableQtyVO> availableQtyDTOList = new ArrayList<>();
        for (WipReworkMoEntity rwkMo : rwkMoList) {
            WipRwkAvailableQtyVO lotStatusDTO = new WipRwkAvailableQtyVO();

            String priKey = generateOcsPriKey(rwkMo, rwkMo.getLotStatus());

            lotStatusDTO.setOcs_order_id(rwkMoDTO.getOcsOrderId())
                    .setPri_key(priKey);
            availableQtyDTOList.add(lotStatusDTO);
            // APPS.XXOCS_ITEM_LOTS_V4还会使用100这个值作为批次状态
            WipRwkAvailableQtyVO antherStatusDTO = new WipRwkAvailableQtyVO();
            priKey = generateOcsPriKey(rwkMo, "100");
            antherStatusDTO.setOcs_order_id(rwkMoDTO.getOcsOrderId())
                    .setPri_key(priKey);
            availableQtyDTOList.add(antherStatusDTO);
        }
        String jsonParamStr = JSON.toJSONString(availableQtyDTOList);
        String url = ocsApiInfoConfiguration.getBaseUrl() + "/stockmm/available_qty";
        String resetResponse = RestCallUtils.callRest(RestCallUtils.RequestMethod.POST, url, token, jsonParamStr);
        OcsResponseDTO<List<WipRwkAvailableQtyVO>> result = JSON.parseObject(resetResponse, new TypeReference<OcsResponseDTO<List<WipRwkAvailableQtyVO>>>(){});
        return result.getDatas();
    }

    /**
     * 将工单信息拼成视图APPS.XXOCS_ITEM_LOTS_V4的主键
     * @since 2020/4/7 10:35 上午
     * @author xueyuting
     * @param wipRwkMo 工单信息
     * @param billStatus 批次状态
     */
    private String generateOcsPriKey(WipReworkMoEntity wipRwkMo, String billStatus) {
        StringBuilder priKeyBuilder = new StringBuilder();
        priKeyBuilder.append(wipRwkMo.getOrganizationId()).append("_")
                .append(wipRwkMo.getProductId()).append("_")
                .append(wipRwkMo.getFactoryId()).append("_")
                .append(wipRwkMo.getSourceLotNo()).append("_");
        WipMoReworkLotStatusEnum lotStatusEnum = WipMoReworkLotStatusEnum.getMode(billStatus);
        priKeyBuilder.append(lotStatusEnum == null ? billStatus : lotStatusEnum.getCode());
        return priKeyBuilder.toString();
    }
}
