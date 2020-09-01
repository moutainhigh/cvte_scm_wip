package com.cvte.scm.wip.spi.requirement;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.scm.wip.common.enums.error.ReqLineErrEnum;
import com.cvte.scm.wip.domain.common.deprecated.RestCallUtils;
import com.cvte.scm.wip.domain.common.token.service.AccessTokenService;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotIssuedEntity;
import com.cvte.scm.wip.domain.core.requirement.service.LotIssuedWriteBackService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.LotIssuedOpTypeEnum;
import com.cvte.scm.wip.infrastructure.boot.config.api.EbsApiInfoConfiguration;
import com.cvte.scm.wip.infrastructure.item.mapper.ScmItemMapper;
import com.cvte.scm.wip.spi.common.EbsResponse;
import com.cvte.scm.wip.spi.requirement.dto.LotIssuedWriteBackDTO;
import org.springframework.stereotype.Service;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/7/28 10:11
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class LotIssuedWriteBackServiceImpl implements LotIssuedWriteBackService {

    private ScmItemMapper scmItemMapper;
    private AccessTokenService accessTokenService;
    private EbsApiInfoConfiguration ebsApiInfoConfiguration;

    public LotIssuedWriteBackServiceImpl(ScmItemMapper scmItemMapper, AccessTokenService accessTokenService, EbsApiInfoConfiguration ebsApiInfoConfiguration) {
        this.scmItemMapper = scmItemMapper;
        this.accessTokenService = accessTokenService;
        this.ebsApiInfoConfiguration = ebsApiInfoConfiguration;
    }

    @Override
    public void writeBack(LotIssuedOpTypeEnum opType, WipReqLotIssuedEntity reqLotIssued) {
        String itemId = scmItemMapper.getItemId(reqLotIssued.getItemNo());

        LotIssuedWriteBackDTO lotIssuedWriteBackDTO = new LotIssuedWriteBackDTO();
        lotIssuedWriteBackDTO.setHeaderId(reqLotIssued.getHeaderId())
                // ERP将所有领料批次存放在ATTRIBUTE3字段, 格式为 (lot:qty,...)
                .setLotNo(reqLotIssued.getMtrLotNo() + ":" + reqLotIssued.getIssuedQty().toString())
                .setWkpNo(reqLotIssued.getWkpNo())
                .setItemId(itemId)
                .setLotQty(reqLotIssued.getIssuedQty())
                .setOpType(opType.getCode());

        String jsonParam = JSON.toJSONString(lotIssuedWriteBackDTO);
        String url = ebsApiInfoConfiguration.getBaseUrl() + "/xxfnd/pubprocess/recordReqMtrLot";
        String token;
        try {
            token = accessTokenService.getAccessToken();
        } catch (Exception e) {
            throw new ServerException("", "IAC鉴权服务不可用,无法获取OCS可用量数据");
        }
        String restResponse = RestCallUtils.callRest(RestCallUtils.RequestMethod.POST, url, token, jsonParam);

        EbsResponse<String> ebsResponse = JSON.parseObject(restResponse, new TypeReference<EbsResponse<String>>(){});
        if (!"S".equals(ebsResponse.getRtStatus())) {
            throw new ServerException(ReqLineErrEnum.SYNC_FAILED.getCode(), ReqLineErrEnum.SYNC_FAILED.getDesc() + ":" + ebsResponse.getRtMessage());
        }
    }

}
