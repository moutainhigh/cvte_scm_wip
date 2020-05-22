package com.cvte.scm.wip.spi.changebill;

import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.core.changebill.service.SourceChangeBillService;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillBuildVO;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillDetailBuildVO;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillQueryVO;
import com.cvte.scm.wip.spi.changebill.DTO.SourceChangeBillDTO;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 11:20
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class SourceChangeBillServiceImpl implements SourceChangeBillService {

    @Override
    public List<ChangeBillBuildVO> querySourceChangeBill(ChangeBillQueryVO queryVO) {
        List<SourceChangeBillDTO> changeBillDTOList = requestEbsChangeBill(queryVO);
        return parseDtoToVo(changeBillDTOList);
    }

    private List<SourceChangeBillDTO> requestEbsChangeBill(ChangeBillQueryVO queryVO) {
        return null;
    }

    @VisibleForTesting
    List<ChangeBillBuildVO> parseDtoToVo(List<SourceChangeBillDTO> sourceBillList) {
        List<ChangeBillBuildVO> billBuildVOList = new ArrayList<>();

        Map<String, List<SourceChangeBillDTO>> sourceBillMap = sourceBillList.stream()
                .collect(Collectors.groupingBy(bill -> bill.getBillId() + bill.getOrganizationId()));
        for (Map.Entry<String, List<SourceChangeBillDTO>> entry : sourceBillMap.entrySet()) {
            List<SourceChangeBillDTO> sourceChangeBillDTOList = entry.getValue();

            // 随机取一个, 生成更改单头构造对象
            SourceChangeBillDTO randomOne = sourceChangeBillDTOList.get(0);
            ChangeBillBuildVO billBuildVO = SourceChangeBillDTO.buildHeaderVO(randomOne);

            // 生成所有更改单行构造对象
            List<ChangeBillDetailBuildVO> billDetailBuildVOList = new ArrayList<>();
            for (SourceChangeBillDTO sourceChangeBillDTO : sourceChangeBillDTOList) {
                ChangeBillDetailBuildVO detailBuildVO = SourceChangeBillDTO.buildDetailVO(sourceChangeBillDTO);
                // EBS位号非结构化, 需要拆分
                List<ChangeBillDetailBuildVO> detailListSplitByPos = splitBillDetailByPos(detailBuildVO, ",");
                billDetailBuildVOList.addAll(detailListSplitByPos);
            }

            billBuildVO.setDetailVOList(billDetailBuildVOList);
            billBuildVOList.add(billBuildVO);
        }
        return billBuildVOList;
    }

    @VisibleForTesting
    List<ChangeBillDetailBuildVO> splitBillDetailByPos(ChangeBillDetailBuildVO vo, String splitter) {
        List<ChangeBillDetailBuildVO> resultList = new ArrayList<>();
        String posNo = vo.getPosNo();
        if (StringUtils.isBlank(posNo) || !posNo.contains(splitter)) {
            resultList.add(vo);
            return resultList;
        }

        String[] posNoArr = posNo.split(splitter);
        BigDecimal itemQty = vo.getItemQty();
        if (Objects.isNull(itemQty)) {
            itemQty = BigDecimal.ZERO;
        }
        final BigDecimal originQty = itemQty;
        // 拆分后向上取整, 按顺序分配到位号上
        BigDecimal splitQty = itemQty.divide(new BigDecimal(posNoArr.length), 0, RoundingMode.CEILING);
        for (String splitPosNo : posNoArr) {
            BigDecimal allocateQty = itemQty.min(splitQty);
            BigDecimal allocateUnitQty = allocateQty.divide(originQty, 6, RoundingMode.DOWN);

            ChangeBillDetailBuildVO splitDetailBuildVo = new ChangeBillDetailBuildVO();
            BeanUtils.copyProperties(vo, splitDetailBuildVo);
            splitDetailBuildVo.setPosNo(splitPosNo.trim())
                    .setItemQty(allocateQty)
                    .setItemUnitQty(allocateUnitQty);

            resultList.add(splitDetailBuildVo);
            itemQty = itemQty.subtract(allocateQty);
        }
        return resultList;
    }

}
