package com.cvte.scm.wip.domain.core.ckd.dto;

import com.cvte.csb.sys.common.MyBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @author zy
 * @date 2020-05-06 18:12
 **/
@Data
@Accessors(chain = true)
public class WipMcTaskUpdateDTO extends MyBaseEntity {

    private String optUser;

    private List<Record> updateList;


    @Data
    @Accessors(chain = true)
    public static class Record extends MyBaseEntity {
        /**
         * 配料任务编号
         */
        @ApiModelProperty(value = "配料任务编号")
        private String mcTaskNo;

        /**
         * 工厂
         */
        @ApiModelProperty(value = "工厂")
        private String factoryId;
        /**
         * 客户
         */
        @ApiModelProperty(value = "客户")
        private String client;
        /**
         * 齐套日期
         */
        @ApiModelProperty(value = "齐套日期")
        private Date mtrReadyTime;
        /**
         * 备注
         */
        @ApiModelProperty(value = "备注")
        private String remark;
    }


}
