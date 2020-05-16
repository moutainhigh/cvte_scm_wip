package com.cvte.scm.wip.domain.common.view.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/10 16:49
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
public class PageResultEntity implements Serializable {

    private Long total;
    private Integer pageSize;
    private Integer pageNum;
    private Integer pages;
    private Object list;

    public PageResultEntity(Integer pageSize, Integer pageNum, Long total) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.pages = (int) (total + (long) pageSize - 1L) / pageSize;
    }

    public PageResultEntity() {
    }

}