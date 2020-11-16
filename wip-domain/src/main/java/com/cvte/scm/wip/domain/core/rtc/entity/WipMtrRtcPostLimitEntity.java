package com.cvte.scm.wip.domain.core.rtc.entity;

import com.cvte.scm.wip.domain.common.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * ${table.comment}
 *
 * @author author
 * @since 2020-10-23
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class WipMtrRtcPostLimitEntity extends BaseModel {

    private String factoryId;

    private String factoryNo;

    private Integer limitDay;

    private Integer limitHour;

    private String status;

    private Date crtTime;

    private String crtUser;

    private String crtHost;

    private Date updTime;

    private String updUser;

    private String updHost;

}
