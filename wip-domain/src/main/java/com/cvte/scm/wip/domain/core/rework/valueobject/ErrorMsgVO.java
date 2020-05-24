package com.cvte.scm.wip.domain.core.rework.valueobject;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/4/9 19:25
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class ErrorMsgVO {

    private String id;

    private String message;

    public static String toMsg(List<ErrorMsgVO> errorMsgDTOList) {
        StringBuilder msgBuilder = new StringBuilder();
        for (ErrorMsgVO errorMsgVo : errorMsgDTOList) {
            msgBuilder.append("【").append(errorMsgVo.getId()).append(":").append(errorMsgVo.getMessage()).append("】");
        }
        return msgBuilder.toString();
    }
}
