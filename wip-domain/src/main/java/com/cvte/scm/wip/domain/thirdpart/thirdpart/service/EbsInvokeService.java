package com.cvte.scm.wip.domain.thirdpart.thirdpart.service;


import com.alibaba.fastjson.JSONObject;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.ErrorUtils;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.scm.wip.domain.common.deprecated.RestCallUtils;
import com.cvte.scm.wip.domain.common.token.service.AccessTokenService;
import com.cvte.scm.wip.domain.thirdpart.thirdpart.dto.EbsInoutStockDTO;
import com.cvte.scm.wip.domain.thirdpart.thirdpart.dto.EbsInoutStockResponse;
import com.cvte.scm.wip.domain.thirdpart.thirdpart.dto.EbsInoutStockView;
import com.cvte.scm.wip.domain.thirdpart.thirdpart.dto.query.EbsInoutStockQuery;
import com.cvte.scm.wip.domain.thirdpart.thirdpart.exception.EbsInvokeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
public class EbsInvokeService {


    @Value("${ebs.csbp.host:https://itapitest.gz.cvte.cn/csbp}")
    private String host;

    @Value("${ebs.csbp.stock.inout.import.url:/inv/inoutstock/inoutStockImport}")
    private String inoutStockImportUrl;

    @Value("${ebs.csbp.stock.inout.query.url:/inv/inoutstock/inoutStock}")
    private String inoutStockQueryUtl;

    @Autowired
    private AccessTokenService accessTokenService;


    public List<EbsInoutStockView> listEbsInoutStockView(EbsInoutStockQuery query) {


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

            return response.getJSONArray("data").toJavaList(EbsInoutStockView.class);

        } catch (Exception e) {
            log.error("[listEbsInoutStockView] 调拨单查询失败: {}", ErrorUtils.getErrorMessage(e));
            throw new EbsInvokeException(e.getMessage());
        }

    }

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
