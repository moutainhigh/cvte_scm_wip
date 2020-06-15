package com.cvte.scm.wip.domain.core.requirement.service;

import com.alibaba.fastjson.JSON;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.common.view.entity.PageResultEntity;
import com.cvte.scm.wip.domain.common.view.service.ViewService;
import com.cvte.scm.wip.domain.common.view.vo.SysViewPageParamVO;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLineRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsInfoVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqLineVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.BillStatusEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.cvte.csb.toolkit.StringUtils.isNotEmpty;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 17:11
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class WipReqLinePageService {

    private static final int MAX_PAGE_SIZE = 100000;

    private ViewService viewService;
    private WipReqLineRepository wipReqLineRepository;

    public WipReqLinePageService(ViewService viewService, WipReqLineRepository wipReqLineRepository) {
        this.viewService = viewService;
        this.wipReqLineRepository = wipReqLineRepository;
    }

    public PageResultEntity tree(SysViewPageParamVO sysViewPageParam) {
        int originPageSize = sysViewPageParam.getPageSize();
        int originPageNum = sysViewPageParam.getPageNum();
        // 由于前端查询、排序的参数可能是子节点的属性，所以视图查询的并不是父节点
        // 而是header_id对应下的所有行, 然后会根据 批次_物料_工序 抽取出虚拟的父节点，再重新分页
        sysViewPageParam.setPageSize(MAX_PAGE_SIZE);
        sysViewPageParam.setPageNum(0);
        // 调用bsm接口获取配置的视图数据
        PageResultEntity feignPageResult = viewService.getViewPageDataByViewPageParam(sysViewPageParam);
        List<WipReqLineVO> allChildrenLines = JSON.parseArray(JSON.toJSONString(feignPageResult.getList()), WipReqLineVO.class);

        // 使用LinkedHashMap，保证不会有影响原有查询结果的排序
        Map<String, List<WipReqLineVO>> groupedLinesMap = allChildrenLines.stream().collect(Collectors.groupingBy(this::generateGroupKey, LinkedHashMap::new, toList()));
        // 重新计算分页参数
        int parentTotalCount = groupedLinesMap.entrySet().size();
        int startIndex = originPageNum * originPageSize;
        int endIndex = Math.min((startIndex + originPageSize), parentTotalCount) - 1;

        List<WipReqLineVO> parentLines = new ArrayList<>();
        int loopCount = 0;
        for (Map.Entry<String, List<WipReqLineVO>> groupedLinesEntry : groupedLinesMap.entrySet()) {
            if (loopCount >= startIndex && loopCount <= endIndex) {
                WipReqLineVO parentLine = new WipReqLineVO();
                List<WipReqLineVO> currentLinesChildren = groupedLinesEntry.getValue();
                if (currentLinesChildren.size() <= 1) {
                    // 只有一个位号，则不需要父节点
                    BeanUtils.copyProperties(currentLinesChildren.get(0), parentLine);
                } else {
                    // posNo = child posNo1,child posNo2,... ; qty = sum(child qty)
                    String parentPosNo = currentLinesChildren.stream().filter(childLine -> isNotEmpty(childLine.getPosNo())).map(WipReqLineVO::getPosNo).collect(Collectors.joining(","));
                    int parentQty = currentLinesChildren.stream().map(WipReqLineVO::getIssuedQty).filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
                    int reqQty = currentLinesChildren.stream().map(WipReqLineVO::getReqQty).filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
                    double unitQty = currentLinesChildren.stream().map(WipReqLineVO::getUnitQty).filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum();
                    String lineStatus = currentLinesChildren.stream().filter(line -> line != null && line.getLineStatus() != null).map(o -> Integer.valueOf(o.getLineStatus())).min(Integer::compareTo).orElse(0).toString();
                    // 取最新更新的行初始化父节点的一些信息，若更新时间为空则默认取第一个
                    List<WipReqLineVO> filterLines = currentLinesChildren.stream().filter(line -> null != line.getUpdDate()).collect(toList());
                    WipReqLineVO parentMainInfo;
                    if (filterLines.isEmpty()) {
                        parentMainInfo = currentLinesChildren.get(0);
                    } else {
                        parentMainInfo = filterLines.stream().max(Comparator.comparing(WipReqLineVO::getUpdDate)).get();
                    }
                    BeanUtils.copyProperties(parentMainInfo, parentLine);
                    // 其lineId = lotNumber_itemNo_wkpNo
                    parentLine.setLineId(groupedLinesEntry.getKey())
                            .setChildren(currentLinesChildren)
                            .setPosNo(parentPosNo)
                            .setIssuedQty(parentQty)
                            .setReqQty(reqQty)
                            .setUnitQty(unitQty)
                            .setLineStatus(lineStatus);
                }
                parentLines.add(parentLine);
            }
            loopCount++;
        }
        PageResultEntity finalPageResult = new PageResultEntity();
        finalPageResult.setList(parentLines);
        finalPageResult.setTotal((long) parentTotalCount);
        finalPageResult.setPageSize(originPageSize);
        finalPageResult.setPageNum(originPageNum);
        finalPageResult.setPages(calculatePageNum(finalPageResult.getTotal(), finalPageResult.getPageSize()).intValue());
        return finalPageResult;
    }

    /**
     * 未发料数量汇总
     */
    public Integer sumUnissuedQty(String headerId) {
        if (StringUtils.isBlank(headerId)) {
            throw new ParamsIncorrectException("【未发料数量汇总】投料单头ID不可为空!");
        }
        WipReqLineEntity queryLine = new WipReqLineEntity().setHeaderId(headerId).setSourceCode(null);
        List<WipReqLineEntity> reqLines = wipReqLineRepository.selectList(queryLine);
        reqLines.removeIf(line -> BillStatusEnum.CLOSED.getCode().equals(line.getLineStatus()) || BillStatusEnum.CANCELLED.getCode().equals(line.getLineStatus()));
        int sumReqQty = reqLines.stream().map(WipReqLineEntity::getReqQty).filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
        int sumIssuedQty = reqLines.stream().map(WipReqLineEntity::getIssuedQty).filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
        return sumReqQty - sumIssuedQty;
    }

    private String generateGroupKey(WipReqLineVO wipReqLineVO) {
        return wipReqLineVO.getLotNumber() + "_" + wipReqLineVO.getItemNo() + "_" + wipReqLineVO.getWkpNo();
    }

    private Long calculatePageNum(Long total, Integer pageSize) {
        return (total / pageSize + (total % pageSize > 0 ? 1 : 0));
    }

    public PageResultEntity reqInsInfo(SysViewPageParamVO pageParam) {
        int originPageSize = pageParam.getPageSize();
        int originPageNum = pageParam.getPageNum();
        pageParam.setPageSize(MAX_PAGE_SIZE);
        pageParam.setPageNum(0);

        PageResultEntity feignPageResult = viewService.getViewPageDataByViewPageParam(pageParam);
        List<ReqInsInfoVO> insDetailList = JSON.parseArray(JSON.toJSONString(feignPageResult.getList()), ReqInsInfoVO.class);

        Map<String, List<ReqInsInfoVO>> insDetailMap = insDetailList.stream().collect(groupingBy(ReqInsInfoVO::getBillNo, LinkedHashMap::new, toList()));

        int billTotalCount = insDetailMap.size();
        int startIndex = originPageNum * originPageSize;
        int endIndex = Math.min((startIndex + originPageSize), billTotalCount) - 1;

        List<ReqInsInfoVO> resultInsInfoList = new ArrayList<>();
        int loopCount = 0;
        for (Map.Entry<String, List<ReqInsInfoVO>> insDetailEntry : insDetailMap.entrySet()) {
            if (loopCount >= startIndex && loopCount <= endIndex) {
                List<ReqInsInfoVO> groupInsInfoList = insDetailEntry.getValue();
                groupInsInfoList.get(0).setHeadFlag(1);
                resultInsInfoList.addAll(groupInsInfoList);
            }
            loopCount++;
        }

        feignPageResult.setList(resultInsInfoList);
        feignPageResult.setTotal((long)billTotalCount);
        feignPageResult.setPageSize(originPageSize);
        feignPageResult.setPageNum(originPageNum);
        feignPageResult.setPages(calculatePageNum(feignPageResult.getTotal(), feignPageResult.getPageSize()).intValue());
        return feignPageResult;
    }

}
