package com.cvte.scm.wip.controller.rtc.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.controller.rtc.admin.dto.WipMtrRtcLotViewDTO;
import com.cvte.scm.wip.domain.common.view.vo.SysViewPageParamVO;
import com.cvte.scm.wip.domain.core.rtc.service.WipMtrRtcViewService;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcQueryVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrSubInvVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/17 19:24
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@RestController
@RequestMapping("/admin/rtc/view")
public class WipMtrRtcViewController {

    private WipMtrRtcViewService wipMtrRtcViewService;

    public WipMtrRtcViewController(WipMtrRtcViewService wipMtrRtcViewService) {
        this.wipMtrRtcViewService = wipMtrRtcViewService;
    }

    @PostMapping("/line")
    public RestResponse lineView(@RequestBody SysViewPageParamVO sysViewPageParam) {
        return new RestResponse().setData(wipMtrRtcViewService.lineView(sysViewPageParam));
    }

    @PostMapping("/lot")
    public RestResponse lotView(@RequestBody WipMtrRtcLotViewDTO rtcLotViewDTO) {
        WipMtrRtcQueryVO lotQuery = new WipMtrRtcQueryVO();
        lotQuery.setOrganizationId(rtcLotViewDTO.getOrganizationId())
                .setFactoryId(rtcLotViewDTO.getFactoryId())
                .setItemId(rtcLotViewDTO.getItemId())
                .setBillType(rtcLotViewDTO.getBillType())
                .setMoId(rtcLotViewDTO.getMoId())
                .setLineId(rtcLotViewDTO.getLineId())
                .setInvpNo(rtcLotViewDTO.getInvpNo());
        return new RestResponse().setData(wipMtrRtcViewService.lotView(lotQuery, rtcLotViewDTO.getLotNumber()));
    }

}
