package com.cvte.scm.wip.domain.core.requirement.valueobject;

import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.common.enums.error.ReqInsErrEnum;
import com.cvte.scm.wip.common.utils.ClassUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsDetailEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/25 17:15
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class WipReqLineKeyQueryVO {

    private String lineId;

    private String headerId;

    private String organizationId;

    private String lotNumber;

    private String wkpNo;

    private String itemId;

    private String itemNo;

    private String posNo;

    public static WipReqLineKeyQueryVO build(ReqInsDetailEntity entity) {
        if (StringUtils.isBlank(entity.getMoLotNo()) && StringUtils.isBlank(entity.getWkpNo()) && StringUtils.isBlank(entity.getItemIdNew()) && StringUtils.isNotBlank(entity.getPosNo())) {
            throw new ServerException(ReqInsErrEnum.KEY_NULL.getCode(), ReqInsErrEnum.KEY_NULL.getDesc() + ",删除范围过大,指令:ID=" + entity.getInsDetailId());
        }
        WipReqLineKeyQueryVO keyQueryVO = new WipReqLineKeyQueryVO();
        keyQueryVO.setHeaderId(entity.getAimHeaderId())
                .setOrganizationId(entity.getOrganizationId())
                .setLotNumber(entity.getMoLotNo())
                .setWkpNo(entity.getWkpNo())
                .setItemId(entity.getItemIdOld())
                .setPosNo(entity.getPosNo());
        return keyQueryVO;
    }

    @Override
    public String toString() {
        List<String> fieldPrintList = new ArrayList<>();
        BiConsumer<String, String> addNonNull = (p, v) -> {
            if (StringUtils.isNotBlank(v)) {
                fieldPrintList.add(p + "=" + v);
            }
        };
        Field[] fields = WipReqLineKeyQueryVO.class.getDeclaredFields();
        for (Field field : fields) {
            addNonNull.accept(field.getName(), (String)ClassUtils.getFieldValue(this, field));
        }
        return String.join(",", fieldPrintList);
    }

}
