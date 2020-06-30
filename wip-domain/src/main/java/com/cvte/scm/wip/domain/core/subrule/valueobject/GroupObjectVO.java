package com.cvte.scm.wip.domain.core.subrule.valueobject;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;

/**
 * @author : jf
 * Date    : 2019.02.18
 * Time    : 09:16
 * Email   ：jiangfeng7128@cvte.com
 */
@Data
@AllArgsConstructor
public class GroupObjectVO {
    private Object[] groupObjects;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GroupObjectVO) {
            return Arrays.deepEquals(((GroupObjectVO) obj).groupObjects, this.groupObjects);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.groupObjects);
    }
}
