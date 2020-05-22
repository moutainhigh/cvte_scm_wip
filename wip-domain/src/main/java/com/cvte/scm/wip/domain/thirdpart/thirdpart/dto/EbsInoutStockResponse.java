package com.cvte.scm.wip.domain.thirdpart.thirdpart.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zy
 * @date 2020-05-12 11:30
 **/
@Data
public class EbsInoutStockResponse {

    private StockHeader returnInfo;

    private List<StockLine> rtLines;


    @Data
    public static class StockHeader {

        private String headerId;

        private String ticketNo;

    }


    @Data
    public static class StockLine {

        private String lineId;

        private String lineNo;

        private String interfaceOrigSource;

        private String interfaceOrigSourceId;

        @ApiModelProperty("本地定义的辅助性字段")
        private String mcTaskLineId;
    }

}
