package com.cvte.scm.wip.spi.sys.ebs;


import com.alibaba.fastjson.JSONObject;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.ErrorUtils;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.scm.wip.domain.common.deprecated.RestCallUtils;
import com.cvte.scm.wip.domain.common.token.service.AccessTokenService;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.dto.EbsInoutStockDTO;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.dto.EbsInoutStockResponse;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.dto.EbsInoutStockVO;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.dto.query.EbsInoutStockQuery;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.exception.EbsInvokeException;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.service.EbsInvokeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zy
 * @date 2020-05-11 09:43
 **/
@Slf4j
@Service
public class EbsInvokeServiceImpl implements EbsInvokeService {


    @Value("${ebs.csbp.host}")
    private String host;

    @Value("${ebs.csbp.stock.inout.import.url}")
    private String inoutStockImportUrl;

    @Value("${ebs.csbp.stock.inout.query.url}")
    private String inoutStockQueryUtl;

    @Autowired
    private AccessTokenService accessTokenService;


    /**
     * 查询调拨单
     * https://kb.cvte.com/pages/viewpage.action?pageId=131482997
     *
     * @param query
     * @return java.util.List<com.cvte.scm.wip.domain.core.thirdpart.ebs.dto.EbsInoutStockVO>
     **/
    public List<EbsInoutStockVO> listEbsInoutStockView(EbsInoutStockQuery query) {


        Map<String, Object> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(query.getTicketNoList())) {
            map.put("ticketNoList", query.getTicketNoList());
        }
        try {
            String responseStr = RestCallUtils.callRest(RestCallUtils.RequestMethod.GET,
                    host + inoutStockQueryUtl,
                    accessTokenService.getAccessToken(),
                    map
            );
            JSONObject response = JSONObject.parseObject(responseStr);
            if (!"0".equals(response.getString("status"))) {
                throw new EbsInvokeException("调拨单创建失败：" + response.getString("message"));
            }

            if (ObjectUtils.isNull(response.getJSONArray("data"))) {
                return new ArrayList<>();
            }
            return response.getJSONArray("data").toJavaList(EbsInoutStockVO.class);

        } catch (Exception e) {
            log.error("[listEbsInoutStockView] 调拨单查询失败: {}", ErrorUtils.getErrorMessage(e));
            throw new EbsInvokeException(e.getMessage());
        }

    }

    /**
     * 创建调拨单
     * https://kb.cvte.com/pages/viewpage.action?pageId=131483121
     *
     * @param
     * @param ebsInoutStockDTO
     * @return com.cvte.scm.wip.domain.core.thirdpart.ebs.dto.EbsInoutStockResponse
     **/
    public EbsInoutStockResponse inoutStockImport(EbsInoutStockDTO ebsInoutStockDTO) {

        JSONObject request = new JSONObject();
        request.put("importHdrJson", ebsInoutStockDTO);

        JSONObject response;
        String responseStr = null;
        try {

            responseStr = RestCallUtils.callRest(RestCallUtils.RequestMethod.POST,
                    host + inoutStockImportUrl,
                    accessTokenService.getAccessToken(),
                    request.toJSONString());
            response = JSONObject.parseObject(responseStr);
            if (!"0".equals(response.getString("status"))) {
                throw new EbsInvokeException("调拨单创建失败：" + response.getString("message"));
            }

            JSONObject returnInfo = response.getJSONObject("data").getJSONObject("returnInfo");
            if (ObjectUtils.isNull(returnInfo) || "E".equals(returnInfo.getString("rtStatus"))) {
                String errMsg = ObjectUtils.isNotNull(returnInfo) ? returnInfo.getString("rtMessage") : "";
                throw new EbsInvokeException("调拨单创建失败: " + errMsg);
            }

            return afterHandler(ebsInoutStockDTO, response.getObject("data", EbsInoutStockResponse.class));
        } catch (Exception e) {
            log.error("[inoutStockImport] 调拨单创建失败: request={}\n response={}\nerrMsg={}"
                    , request.toJSONString(), responseStr, ErrorUtils.getErrorMessage(e));
            throw new EbsInvokeException(e.getMessage());
        }
    }

    private EbsInoutStockResponse afterHandler(EbsInoutStockDTO ebsInoutStockDTO, EbsInoutStockResponse ebsInoutStockResponse) {

        if (ObjectUtils.isNull(ebsInoutStockResponse)
                || CollectionUtils.isEmpty(ebsInoutStockResponse.getRtLines())
                || ObjectUtils.isNull(ebsInoutStockDTO)
                || CollectionUtils.isEmpty(ebsInoutStockDTO.getImportLnJson())) {
            return ebsInoutStockResponse;
        }

        List<EbsInoutStockDTO.LineDTO> reqeustLines = ebsInoutStockDTO.getImportLnJson();
        Map<String, String> mcTaskLineIdMap = reqeustLines.stream()
                .collect(Collectors.toMap(EbsInoutStockDTO.LineDTO::getInterfaceOrigSourceId, EbsInoutStockDTO.LineDTO::getMcTaskLineId));


        for (EbsInoutStockResponse.StockLine stockLine : ebsInoutStockResponse.getRtLines()) {
            stockLine.setMcTaskLineId(mcTaskLineIdMap.get(stockLine.getInterfaceOrigSourceId()));
        }

        return ebsInoutStockResponse;
    }
}
