package com.cvte.scm.wip.controller.requirement.admin;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.app.req.ins.confirm.ReqInsConfirmApplication;
import com.cvte.scm.wip.app.req.ins.invalid.ReqInsInvalidApplication;
import com.cvte.scm.wip.domain.common.view.vo.SysViewPageParamVO;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
import com.cvte.scm.wip.domain.core.requirement.service.CheckReqInsDomainService;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqLinePageService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsBuildVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ProcessingStatusEnum;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/2 11:16
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@RestController
@Api(tags = "待确认列表")
@RequestMapping("/admin/req/ins")
public class ReqInsController {

    private ReqInsConfirmApplication reqInsConfirmApplication;
    private WipReqLinePageService wipReqLinePageService;
    private ReqInsInvalidApplication reqInsInvalidApplication;
    private CheckReqInsDomainService checkReqInsDomainService;

    public ReqInsController(ReqInsConfirmApplication reqInsConfirmApplication, WipReqLinePageService wipReqLinePageService, ReqInsInvalidApplication reqInsInvalidApplication, CheckReqInsDomainService checkReqInsDomainService) {
        this.reqInsConfirmApplication = reqInsConfirmApplication;
        this.wipReqLinePageService = wipReqLinePageService;
        this.reqInsInvalidApplication = reqInsInvalidApplication;
        this.checkReqInsDomainService = checkReqInsDomainService;
    }

    @PostMapping("/confirm")
    public RestResponse confirm(@RequestBody String... insHeaderId) {
        return new RestResponse().setData(reqInsConfirmApplication.doAction(insHeaderId));
    }

    @PostMapping("/info")
    public RestResponse reqInsInfo(@RequestBody SysViewPageParamVO sysViewPageParam) {
        return new RestResponse().setData(wipReqLinePageService.reqInsInfo(sysViewPageParam));
    }

    @GetMapping("/count/{aimHeaderId}")
    public RestResponse count(@PathVariable("aimHeaderId") String aimHeaderId) {
        List<String> statusList = new ArrayList<>();
        statusList.add(ProcessingStatusEnum.PENDING.getCode());
        statusList.add(ProcessingStatusEnum.EXCEPTION.getCode());
        return new RestResponse().setData(ReqInsEntity.get().getByAimHeaderId(aimHeaderId, statusList).size());
    }

    @PostMapping("/invalid")
    public RestResponse invalid(@RequestBody List<ReqInsBuildVO> voList) {
        reqInsInvalidApplication.doAction(voList);
        return new RestResponse();
    }

    @PostMapping("/isPrepared")
    public RestResponse isPrepared(@RequestBody List<String> insHeaderId) {
        String msg = null;
        try {
            checkReqInsDomainService.checkInsPrepared(insHeaderId);
        } catch (ParamsIncorrectException pie) {
            msg = pie.getMessage();
        }
        return new RestResponse().setData(msg);
    }

}
