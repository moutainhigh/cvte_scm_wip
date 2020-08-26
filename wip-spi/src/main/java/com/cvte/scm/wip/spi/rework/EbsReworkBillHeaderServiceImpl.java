package com.cvte.scm.wip.spi.rework;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.core.interfaces.enums.DefaultStatusEnum;
import com.cvte.csb.sys.base.dto.request.SysUserQueryDTO;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.domain.common.deprecated.RestCallUtils;
import com.cvte.scm.wip.domain.common.token.service.AccessTokenService;
import com.cvte.scm.wip.domain.core.rework.entity.WipReworkBillHeaderEntity;
import com.cvte.scm.wip.domain.core.rework.entity.WipReworkBillLineEntity;
import com.cvte.scm.wip.domain.core.rework.entity.WipReworkMoEntity;
import com.cvte.scm.wip.domain.core.rework.repository.WipReworkMoRepository;
import com.cvte.scm.wip.domain.core.rework.service.EbsReworkBillHeaderService;
import com.cvte.scm.wip.domain.core.rework.valueobject.EbsReworkBillQueryVO;
import com.cvte.scm.wip.infrastructure.boot.config.api.EbsApiInfoConfiguration;
import com.cvte.scm.wip.infrastructure.client.common.dto.EbsResponse;
import com.cvte.scm.wip.infrastructure.client.common.dto.FeignResult;
import com.cvte.scm.wip.infrastructure.client.sys.base.SysUserApiClient;
import com.cvte.scm.wip.infrastructure.client.sys.base.dto.UserBaseDTO;
import com.cvte.scm.wip.spi.rework.dto.EbsRwkBillCreateDTO;
import com.cvte.scm.wip.spi.rework.dto.EbsRwkBillLineCreateDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 10:36
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class EbsReworkBillHeaderServiceImpl implements EbsReworkBillHeaderService {

    private EbsApiInfoConfiguration ebsApiInfoConfiguration;
    private AccessTokenService accessTokenService;
    private WipReworkMoRepository wipReworkMoRepository;

    @Autowired
    private SysUserApiClient sysUserApiClient;

    public EbsReworkBillHeaderServiceImpl(EbsApiInfoConfiguration ebsApiInfoConfiguration, AccessTokenService accessTokenService, WipReworkMoRepository wipReworkMoRepository) {
        this.ebsApiInfoConfiguration = ebsApiInfoConfiguration;
        this.accessTokenService = accessTokenService;
        this.wipReworkMoRepository = wipReworkMoRepository;
    }

    @Override
    public List<EbsReworkBillQueryVO> getEbsRwkBillH(Map<String, String> paramMap) {
        String jsonParamStr = JSON.toJSONString(paramMap);
        String response;
        // 获取数据
        try {
            response = getEbsRwkBillH(jsonParamStr);
        } catch (Exception e) {
            throw new ServerException("", "EBS数据接口请求异常");
        }

        // 返回值提取
        EbsResponse<List<EbsReworkBillQueryVO>> ebsResponse = JSON.parseObject(response, new TypeReference<EbsResponse<List<EbsReworkBillQueryVO>>>(){});
        if (!"S".equals(ebsResponse.getRtStatus())) {
            throw new ParamsIncorrectException(String.format("EBS系统接口调用异常, 异常信息: %s", ebsResponse.getRtMessage()));
        }
        return ebsResponse.getRtData();
    }

    /**
     * 获取EBS返工单数据
     * @since 2020/4/15 9:09 上午
     * @author xueyuting
     * @param
     */
    private String getEbsRwkBillH(String jsonParam) {
        String url = ebsApiInfoConfiguration.getBaseUrl() + "/xxfnd/pubquery/getrwkbill";
        String iacToken;
        try {
            iacToken = accessTokenService.getAccessToken();
        } catch (Exception e) {
            throw new ServerException("", "IAC鉴权服务不可用,无法从EBS同步返工单数据");
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("paramsJson", jsonParam);
        return RestCallUtils.callRest(RestCallUtils.RequestMethod.GET, url, iacToken, paramMap);
    }

    @Override
    public String syncToEbs(WipReworkBillHeaderEntity billHeader, List<WipReworkBillLineEntity> billLineList) {
        List<WipReworkBillLineEntity> currentBillLList = billLineList.stream().filter(line -> billHeader.getBillId().equals(line.getBillId())).collect(Collectors.toList());
        EbsRwkBillCreateDTO ebsRwkBillCreateDTO = new EbsRwkBillCreateDTO();
        BeanUtils.copyProperties(billHeader, ebsRwkBillCreateDTO);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        ebsRwkBillCreateDTO.setExpectDeliveryDate(Objects.nonNull(billHeader.getExpectDeliveryDate()) ? df.format(billHeader.getExpectDeliveryDate()) : null)
                .setExpectFinishDate(Objects.nonNull(billHeader.getExpectFinishDate()) ? df.format(billHeader.getExpectFinishDate()) : null);
        // 获取内勤对应的account，若为空，则直接使用创建人
        WipReworkMoEntity queryMo = createMoQueryEntity(billHeader, currentBillLList.get(0));
        List<WipReworkMoEntity> wipRwkMoList = wipReworkMoRepository.selectByEntity(queryMo);
        String account = null;
        if (ListUtil.notEmpty(wipRwkMoList)) {
            String omName = wipRwkMoList.get(0).getOmName();
            if (StringUtils.isNotBlank(omName)) {
                account = getAccountByOmName(omName);
            }
        }
        if (StringUtils.isBlank(account)) {
            FeignResult<UserBaseDTO> feignResult = sysUserApiClient.getSysUserDetail(billHeader.getCrtUser());
            account = feignResult.getData().getAccount();
        }

        ebsRwkBillCreateDTO.setUserNo(account)
                .setImportLnJson(EbsRwkBillLineCreateDTO.batchBuildDTO(currentBillLList))
                .setChangeCode(billHeader.getReworkReasonType())
                .setCustomerName(billHeader.getConsumerName());
        return creatEbsBill(ebsRwkBillCreateDTO);
    }

    private String creatEbsBill(EbsRwkBillCreateDTO ebsRwkBillCreateDTO) {
        // 获取iac-token
        String token;
        try {
            token = accessTokenService.getAccessToken();
        } catch (Exception e) {
            throw new ServerException("", "IAC鉴权服务不可用,无法同步返工单到EBS");
        }
        // 同步至EBS
        Map<String, EbsRwkBillCreateDTO> map = new HashMap<>();
        map.put("importHdrJson", ebsRwkBillCreateDTO);
        String jsonParam = JSON.toJSONString(map);
        String url = ebsApiInfoConfiguration.getBaseUrl() + "/xxfnd/pubprocess/creatermkbill";
        String response = RestCallUtils.callRest(RestCallUtils.RequestMethod.POST, url, token, jsonParam);
        EbsResponse<String> ebsResponse = JSON.parseObject(response, new TypeReference<EbsResponse<String>>(){});
        if (!"S".equals(ebsResponse.getRtStatus())) {
            return ebsResponse.getRtMessage();
        }
        return null;
    }

    private WipReworkMoEntity createMoQueryEntity(WipReworkBillHeaderEntity wipRwkBillH, WipReworkBillLineEntity wipRwkBillL) {
        WipReworkMoEntity queryRwkMo = new WipReworkMoEntity();
        queryRwkMo.setOrganizationId(Integer.parseInt(wipRwkBillH.getOrganizationId()))
                .setFactoryId(Integer.parseInt(wipRwkBillH.getFactoryId()))
                .setSourceLotNo(wipRwkBillL.getMoLotNo())
                .setProductNo(wipRwkBillL.getPriProductNo())
                .setProductModel(wipRwkBillL.getProductModel())
                .setLotStatus(wipRwkBillL.getMoLotStatus());
        return queryRwkMo;
    }

    private String getAccountByOmName(String omName) {
        SysUserQueryDTO userQueryDTO = new SysUserQueryDTO();
        userQueryDTO.setName(omName);
        FeignResult<Map<String, Object>> feignResult = sysUserApiClient.listUserByName(omName);
        if(DefaultStatusEnum.OK.getCode().equals(feignResult.getStatus())) {
            Object list = feignResult.getData().get("rows");
            if (Objects.isNull(list)) {
                return "";
            }
            List<UserBaseDTO> userBaseDTOList = JSON.parseArray(JSON.toJSONString(list), UserBaseDTO.class);
            if (ListUtil.empty(userBaseDTOList)) {
                return "";
            }
            return userBaseDTOList.get(0).getAccount();
        } else {
            return "";
        }
    }
    
}
