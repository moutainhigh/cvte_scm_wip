package com.cvte.scm.wip.domain.thirdpart.thirdpart.dto.query;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author zy
 * @date 2020-05-11 16:25
 **/
@Data
@Accessors(chain = true)
public class EbsInoutStockQuery {

    private List<String> ticketNoList;
}
