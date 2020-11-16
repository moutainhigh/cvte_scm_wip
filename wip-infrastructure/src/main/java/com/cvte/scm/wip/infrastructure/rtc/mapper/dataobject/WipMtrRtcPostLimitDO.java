package com.cvte.scm.wip.infrastructure.rtc.mapper.dataobject;

import com.cvte.scm.wip.domain.common.base.BaseModel;

import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * ${table.comment}
 *
 * @author author
 * @since 2020-10-23
 */
@Table(name = "wip_mtr_rtc_post_limit")
@Data
@EqualsAndHashCode(callSuper = false)
public class WipMtrRtcPostLimitDO extends BaseModel {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "factory_id")
    private String factoryId;

    @Column(name = "factory_no")
    private String factoryNo;

    @Column(name = "limit_day")
    private Double limitDay;

    @Column(name = "limit_hour")
    private Double limitHour;

    private String status;

    private String rmk01;

    private String rmk02;

    private String rmk03;

    private String rmk04;

    private String rmk05;

    @Column(name = "crt_time")
    private Date crtTime;

    @Column(name = "crt_user")
    private String crtUser;

    @Column(name = "crt_host")
    private String crtHost;

    @Column(name = "upd_time")
    private Date updTime;

    @Column(name = "upd_user")
    private String updUser;

    @Column(name = "upd_host")
    private String updHost;

}
