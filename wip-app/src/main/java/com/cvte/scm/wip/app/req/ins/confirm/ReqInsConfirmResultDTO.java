package com.cvte.scm.wip.app.req.ins.confirm;

import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.enums.ExecutionResultEnum;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
        return String.join(",", msgList);
    }

}
