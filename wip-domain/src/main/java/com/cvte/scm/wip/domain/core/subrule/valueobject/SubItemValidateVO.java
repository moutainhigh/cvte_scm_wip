package com.cvte.scm.wip.domain.core.subrule.valueobject;

import com.cvte.csb.toolkit.StringUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Objects;
import java.util.function.BiFunction;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/25 09:37
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class SubItemValidateVO {

    private String ruleNo;

    private String organizationId;

    private String beforeItemId;

    private String afterItemId;

    private String beforeItemNo;

    private String afterItemNo;

    private String adaptItem;

    @Override
    public int hashCode() {
        String result = ruleNo + organizationId + beforeItemNo + afterItemNo + adaptItem;
        return result.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (Objects.nonNull(obj) && obj instanceof SubItemValidateVO) {
            SubItemValidateVO vo = (SubItemValidateVO) obj;
            return nullOrEquals().apply(this.getRuleNo(), vo.getRuleNo())
                    && nullOrEquals().apply(this.getOrganizationId(), vo.getOrganizationId())
                    && nullOrEquals().apply(this.getBeforeItemNo(), vo.getBeforeItemNo())
                    && nullOrEquals().apply(this.getAfterItemNo(), vo.getAfterItemNo())
                    && nullOrEquals().apply(this.getAdaptItem(), vo.getAdaptItem());
        }
        return false;
    }

    private static BiFunction<String, String, Boolean> nullOrEquals() {
        return (String p, String v) -> StringUtils.isBlank(p) || p.equals(v);
    }

}
