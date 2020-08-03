package com.cvte.scm.wip.app.req.ins.confirm;

import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.enums.ExecutionResultEnum;
import com.cvte.scm.wip.common.utils.CodeableEnumUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsDetailEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.InsOperationTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/19 16:43
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@AllArgsConstructor
public class ReqInsConfirmResultDTO {

    private ReqInsEntity insEntity;

    private ExecutionResultEnum exeResult;

    private String errMsg;

    public static String buildMsg(List<ReqInsConfirmResultDTO> resultList) {
        List<ReqInsConfirmResultDTO> successList = new ArrayList<>();
        List<ReqInsConfirmResultDTO> skipList = new ArrayList<>();
        List<ReqInsConfirmResultDTO> failedList = new ArrayList<>();
        for (ReqInsConfirmResultDTO result : resultList) {
            ExecutionResultEnum resultEnum = result.getExeResult();
            switch (resultEnum) {
                case SUCCESS:
                    successList.add(result);
                    break;
                case SKIP:
                    skipList.add(result);
                    break;
                case FAILED:
                    failedList.add(result);
                    break;
                default:
            }
        }

        List<String> msgList = new ArrayList<>();
        msgList.add("选择执行" + resultList.size() + "条");
        msgList.add("执行成功" + successList.size() + "条");
        if (ListUtil.notEmpty(skipList)) {
            msgList.add("跳过" + skipList.size() + "条");
        }
        if (ListUtil.notEmpty(failedList)) {
            StringBuilder failedMsgBuilder = new StringBuilder();
            failedMsgBuilder.append("执行失败" + failedList.size() + "条:");
            for (ReqInsConfirmResultDTO failed : failedList) {
                failedMsgBuilder.append("【").append(failed.getErrMsg()).append("】");
            }
            msgList.add(failedMsgBuilder.toString());
        }

        String originMsg = String.join(",", msgList);
        String successMsg = buildSuccessMsg(successList);
        return StringUtils.isNotBlank(successMsg) ? originMsg + ";" + successMsg : originMsg;
    }


    private static String buildSuccessMsg(List<ReqInsConfirmResultDTO> successList) {
        List<String> successMsgList = new ArrayList<>();
        for (ReqInsConfirmResultDTO successInsDTO : successList) {
            ReqInsEntity ins = successInsDTO.getInsEntity();
            List<ReqInsDetailEntity> detailList = ins.getDetailList();
            Map<String, List<ReqInsDetailEntity>> groupedDetailMap = detailList.stream().collect(Collectors.groupingBy(detail -> detail.getMoLotNo() + detail.getItemIdOld() + detail.getItemIdNew() + detail.getOperationType()));
            for (List<ReqInsDetailEntity> groupedDetail : groupedDetailMap.values()) {
                ReqInsDetailEntity firstElement = groupedDetail.get(0);

                // 批次
                String moLotNo = firstElement.getMoLotNo();
                if (StringUtils.isBlank(moLotNo)) {
                    moLotNo = ins.getAimReqLotNo();
                }

                // 位号
                String posNo = groupedDetail.stream().map(ReqInsDetailEntity::getPosNo).filter(StringUtils::isNotBlank).collect(Collectors.joining(","));

                // 操作类型
                String operationType = firstElement.getOperationType();
                InsOperationTypeEnum typeEnum = CodeableEnumUtils.getCodeableEnumByCode(operationType, InsOperationTypeEnum.class);
                // 更改目标物料
                String targetItemNo = "";
                // 更改后物料
                String suffixItemNo = "";
                switch (typeEnum) {
                    case REPLACE:
                        // 只有替换类型需要改后物料
                        suffixItemNo = firstElement.getItemNoNew();
                    case ADD:
                    case INCREASE:
                        targetItemNo = firstElement.getItemNoOld();
                        break;
                    case DELETE:
                    case REDUCE:
                        targetItemNo = firstElement.getItemNoNew();
                        break;
                }

                StringBuilder successMsgBuilder = new StringBuilder();
                successMsgBuilder.append(String.join("//", moLotNo, targetItemNo))
                        .append("//").append(StringUtils.isBlank(posNo) ? "空位号" : posNo)
                        .append(" ").append(typeEnum.getDesc())
                        .append(suffixItemNo)
                        .append(ExecutionResultEnum.SUCCESS.getDesc());

                successMsgList.add(successMsgBuilder.toString());
            }
        }
        return String.join(";", successMsgList);
    }

}
