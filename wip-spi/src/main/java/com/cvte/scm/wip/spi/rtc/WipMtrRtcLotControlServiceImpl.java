package com.cvte.scm.wip.spi.rtc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.domain.common.deprecated.RestCallUtils;
import com.cvte.scm.wip.domain.common.token.service.AccessTokenService;
import com.cvte.scm.wip.domain.core.item.entity.ScmItemEntity;
import com.cvte.scm.wip.domain.core.item.entity.ScmItemOrgEntity;
import com.cvte.scm.wip.domain.core.item.repository.ScmItemOrgRepository;
import com.cvte.scm.wip.domain.core.item.service.ScmItemService;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqHeaderService;
import com.cvte.scm.wip.domain.core.rtc.service.WipMtrRtcLotControlService;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcLotControlVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcLotControlCodeEnum;
import com.cvte.scm.wip.infrastructure.boot.config.api.BsmApiInfoConfiguration;
import com.cvte.scm.wip.spi.subrule.dto.ApsResponse;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/17 16:50
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class WipMtrRtcLotControlServiceImpl implements WipMtrRtcLotControlService {

    private static final String OPTION_NO = "PUR_RULE_01";
    private static final String OPTION_VALUE = "1";

    private ScmItemOrgRepository scmItemOrgRepository;
    private AccessTokenService accessTokenService;
    private BsmApiInfoConfiguration bsmApiInfoConfiguration;
    private WipReqHeaderService wipReqHeaderService;
    private ScmItemService scmItemService;

    public WipMtrRtcLotControlServiceImpl(ScmItemOrgRepository scmItemOrgRepository, AccessTokenService accessTokenService, BsmApiInfoConfiguration bsmApiInfoConfiguration, WipReqHeaderService wipReqHeaderService, ScmItemService scmItemService) {
        this.scmItemOrgRepository = scmItemOrgRepository;
        this.accessTokenService = accessTokenService;
        this.bsmApiInfoConfiguration = bsmApiInfoConfiguration;
        this.wipReqHeaderService = wipReqHeaderService;
        this.scmItemService = scmItemService;
    }

    @Override
    public List<String> getLotControlItem(String organizationId, List<String> itemIdList) {
        if (ListUtil.empty(itemIdList)) {
            return Collections.emptyList();
        }
        List<ScmItemOrgEntity> itemOrgList = scmItemOrgRepository.selectByIds(organizationId, itemIdList);
        return itemOrgList.stream().filter(itemOrg -> WipMtrRtcLotControlCodeEnum.ACTIVE.getCode().equals(itemOrg.getLotControlCode())).map(ScmItemOrgEntity::getItemId).collect(Collectors.toList());
    }

    private List<String> getForceControlLot(String organizationId, String moId, List<String> itemIdList) {
        String productMinClass = getProductMinClass(organizationId, moId);
        List<WipMtrRtcLotControlVO> lotControlVOList = getLotControlByOptionNo(OPTION_NO, OPTION_VALUE);
        List<WipMtrRtcLotControlVO> filteredControlList = lotControlVOList.stream().filter(vo -> productMinClass.equals(vo.getProductClass())).collect(Collectors.toList());
        if (ListUtil.empty(filteredControlList)) {
            return Collections.emptyList();
        }
        List<String> controlItemMinClassList = filteredControlList.stream().map(WipMtrRtcLotControlVO::getMtrClass).collect(Collectors.toList());

        List<ScmItemEntity> itemEntityList = scmItemService.getByItemIds(organizationId, itemIdList);

        return itemEntityList.stream()
                .filter(item -> controlItemMinClassList.contains(item.getRdMinClassCode()))
                .map(ScmItemEntity::getItemId)
                .collect(Collectors.toList());
    }

    private List<WipMtrRtcLotControlVO> getLotControlByOptionNo(String optionNo, String optionValue) {
        String url = String.format("%s%s%s%s%s", bsmApiInfoConfiguration.getBaseUrl(), "/common_query/mtr/lot_control/", optionNo, "/", optionValue);
        String token;
        try {
            token = accessTokenService.getAccessToken();
        } catch (Exception e) {
            throw new ServerException("", "IAC鉴权服务不可用,无法获取批次强管控数据");
        }

        String restResponse = RestCallUtils.callRest(RestCallUtils.RequestMethod.GET, url, token, Collections.emptyMap());
        ApsResponse<List<WipMtrRtcLotControlVO>> response = JSON.parseObject(restResponse, new TypeReference<ApsResponse<List<WipMtrRtcLotControlVO>>>(){});
        if (!"0".equals(response.getStatus())) {
            throw new ParamsIncorrectException(String.format("BSM系统接口调用异常, 异常信息: %s", response.getMessage()));
        }
        return response.getData();
    }

    private String getProductMinClass(String organizationId, String moId) {
        WipReqHeaderEntity reqHeaderEntity = wipReqHeaderService.getBySourceId(moId);
        ScmItemEntity scmItemEntity = scmItemService.getByItemIds(organizationId, Collections.singletonList(reqHeaderEntity.getProductId())).get(0);
        return scmItemEntity.getRdMinClassCode();
    }

}
