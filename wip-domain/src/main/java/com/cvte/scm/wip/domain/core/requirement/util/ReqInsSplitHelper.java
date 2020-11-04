package com.cvte.scm.wip.domain.core.requirement.util;

import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.common.enums.error.ReqInsErrEnum;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsDetailEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipLotVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.InsOperationTypeEnum;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/25 14:23
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public class ReqInsSplitHelper {

    /**
     * 将批次数量分配到投料行上, 单位用量可为负, 为负时扣减数量
     * @since 2020/6/13 10:54 上午
     * @author xueyuting
     * @param insDetail 更改指令
     * @param reqLineList 投料行
     * @param wipLotMap 工单批次
     * @param updateUnitQty 单位用量
     */
    public static List<WipReqLineEntity> allocateQty(ReqInsDetailEntity insDetail, List<WipReqLineEntity> reqLineList, Map<String, WipLotVO> wipLotMap, BigDecimal updateUnitQty) {
        List<WipReqLineEntity> resultList = new ArrayList<>();

        // 用于分配给小批次的总数量
        BigDecimal remainLotQty;
        if (Objects.nonNull(insDetail.getItemQty())) {
            // 用量不为空时取用量
            remainLotQty = insDetail.getItemQty();
            if (updateUnitQty.compareTo(BigDecimal.ZERO) < 0) {
                remainLotQty = remainLotQty.negate();
            }
        } else {
            List<String> lineLotNumberList = reqLineList.stream().map(WipReqLineEntity::getLotNumber).collect(Collectors.toList());
            Iterator<Map.Entry<String, WipLotVO>> wipLotIterator = wipLotMap.entrySet().iterator();
            while (wipLotIterator.hasNext()) {
                // 移除不在本次更改范围的小批次
                Map.Entry<String, WipLotVO> wipLotEntry = wipLotIterator.next();
                String lotNumber = wipLotEntry.getKey();
                if (!lineLotNumberList.contains(lotNumber)) {
                    wipLotIterator.remove();
                }
            }
            if (CollectionUtils.isEmpty(wipLotMap)) {
                throw new ServerException(ReqInsErrEnum.TARGET_LOT_INVALID.getCode(), ReqInsErrEnum.TARGET_LOT_INVALID.getDesc());
            }

            // 用量为空时取 小批次数量之和(工单数量) * 单位用量
            remainLotQty = wipLotMap.values().stream().map(WipLotVO::getLotQuantity)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .multiply(updateUnitQty)
                    .setScale(1, RoundingMode.FLOOR)
                    .setScale(0, RoundingMode.CEILING);
        }

        // 重要, 由于批次分配的剩余数量会累加到最后一次分配上, 所以必须保证批次的排序是从小到大
        // 如小批次的比例原本为1:1:1，但由于投料数量多次修改的结果变成了15, 10, 10, 此时要再减少35的数量, 没有排序的话, 就是分配12, 10, 13(按1:1:1计算, 13=10+3), 会有3个数量没有正确分配
        // 而从小到大排序后, 分配为10(剩余2), 10(剩余2), 15(35-10-10)
        // 这个是在原有算法上改动最小的一种方案, 显然已过于复杂, TODO 改进分配算法
        reqLineList.sort(Comparator.comparing(WipReqLineEntity::getReqQty));

        Map<String, List<WipReqLineEntity>> lotGroupLineMap = reqLineList.stream().collect(Collectors.groupingBy(WipReqLineEntity::getLotNumber, LinkedHashMap::new, toList()));
        Iterator<Map.Entry<String, List<WipReqLineEntity>>> lotGroupLineMapIterator = lotGroupLineMap.entrySet().iterator();
        while (lotGroupLineMapIterator.hasNext()) {
            Map.Entry<String, List<WipReqLineEntity>> lotGroupLineEntry = lotGroupLineMapIterator.next();
            String lotNumber = lotGroupLineEntry.getKey();
            WipLotVO wipLot = wipLotMap.get(lotNumber);
            if (Objects.isNull(wipLot)) {
                // 小批次不存在, 报错
                throw new ServerException(ReqInsErrEnum.TARGET_LOT_INVALID.getCode(), ReqInsErrEnum.TARGET_LOT_INVALID.getDesc());
            }
            // 向上取整的原因: 分配数量可以是负数, 如果向下取整, 可能出现前几个小批次刚好没扣完, 而分配到最后一个数量又过剩, 不合理
            // 如: 3个小批次, 用量各为334, 分配总量1001, -333.37向下取整为-333, 最后一个-335, 结果是1,1,0;而向上取整的结果是0,0,1
            BigDecimal updateQty = updateUnitQty.multiply(wipLot.getLotQuantity()).setScale(1, RoundingMode.DOWN).setScale(0, RoundingMode.UP);
            if (!lotGroupLineMapIterator.hasNext()) {
                updateQty = remainLotQty;
            }
            // 工单数量扣减已分配到该小批次上的数量
            remainLotQty = remainLotQty.subtract(updateQty);

            List<WipReqLineEntity> lotGroupLineList = lotGroupLineEntry.getValue();
            // 因为原子服务update的回写EBS不会被处理, 所以要同时生成新增/删除类型的参数用于回写EBS的行
            List<WipReqLineEntity> reduceOrIncreaseList = new ArrayList<>();

            // 分配策略: 将分配数量均匀加/减到批次对应的投料行上, 若出现小数, 向上取整
            BigDecimal allocateQty = updateQty.divide(new BigDecimal(lotGroupLineList.size()), 0, RoundingMode.UP);
            Iterator<WipReqLineEntity> lotGroupLineIterator = lotGroupLineList.iterator();
            while (lotGroupLineIterator.hasNext()) {
                WipReqLineEntity lotGroupLine = lotGroupLineIterator.next();
                // 计算更新后的需求数量
                if (!lotGroupLineIterator.hasNext()) {
                    // 把剩余的数量都分配到最后一个行上
                    allocateQty = updateQty;
                }
                long resultQty = lotGroupLine.getReqQty() + allocateQty.longValue();

                BigDecimal realAllocateQty = allocateQty;
                if (resultQty < 0) {
                    // 可能减少到0, 此时实际分配数量 = 需求数量
                    if (allocateQty.compareTo(BigDecimal.ZERO) >= 0) {
                        realAllocateQty = new BigDecimal(lotGroupLine.getReqQty());
                    } else {
                        realAllocateQty = new BigDecimal(-lotGroupLine.getReqQty());
                    }
                    resultQty = 0;
                }
                lotGroupLine.setReqQty(resultQty);

                // 单位用量 = 更新后需求数量 / 工单批次数量, 非最后一个, 向接近0的方向取整
                RoundingMode roundingMode = RoundingMode.UP;
                if (!lotGroupLineIterator.hasNext()) {
                    // 最后一行 向远离0的方向取整
                    roundingMode = RoundingMode.DOWN;
                }
                lotGroupLine.setUnitQty(new BigDecimal(resultQty).divide(wipLot.getLotQuantity(), 6, roundingMode).doubleValue());

                // 若行变更类型为空(以防新增类型重复处理), 则再生成一个变更类型=指令的行, 用于回写EBS, 原因是EBS接口表不处理update类型的数据
                if (StringUtils.isBlank(lotGroupLine.getChangeType())) {
                    WipReqLineEntity reduceOrIncreaseLine = new WipReqLineEntity();
                    BeanUtils.copyProperties(lotGroupLine, reduceOrIncreaseLine);
                    reduceOrIncreaseLine.setReqQty(realAllocateQty.abs().longValue())
                            .setUnitQty(realAllocateQty.abs().divide(wipLot.getLotQuantity(), 6, roundingMode).doubleValue());
                    if (InsOperationTypeEnum.REDUCE.getCode().equals(insDetail.getOperationType())) {
                        reduceOrIncreaseLine.setChangeType(ChangedTypeEnum.REDUCE.getCode());
                    } else {
                        reduceOrIncreaseLine.setChangeType(ChangedTypeEnum.INCREASE.getCode());
                    }
                    reduceOrIncreaseList.add(reduceOrIncreaseLine);
                }

                // 小批次数量扣减已分配到位号的数量
                updateQty = updateQty.subtract(realAllocateQty);
            }
            // 如果当前小批次的分配数量有剩余, 还要加回到总分配数量上
            remainLotQty = remainLotQty.add(updateQty);
            resultList.addAll(lotGroupLineList);
            resultList.addAll(reduceOrIncreaseList);
        }
        for (WipReqLineEntity line : resultList) {
            if (StringUtils.isBlank(line.getChangeType())) {
                // 变更类型为"更新"
                line.setChangeType(ChangedTypeEnum.UPDATE.getCode());
            }
        }
        return resultList;
    }

}
