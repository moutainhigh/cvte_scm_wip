package com.cvte.scm.wip.domain.core.requirement.service;

import com.alibaba.fastjson.JSON;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.excel.converter.dto.ExportExcelDTO;
import com.cvte.scm.wip.common.excel.converter.dto.FieldDefinitionDTO;
import com.cvte.scm.wip.common.excel.converter.easyexcel.NumberConverter;
import com.cvte.scm.wip.common.excel.converter.enums.FieldDefinitionTypeEnum;
import com.cvte.scm.wip.common.excel.converter.enums.ValueMergeTypeEnum;
import com.cvte.scm.wip.common.utils.ExcelExportUtils;
import com.cvte.scm.wip.domain.common.view.entity.PageResultEntity;
import com.cvte.scm.wip.domain.common.view.service.ViewService;
import com.cvte.scm.wip.domain.common.view.vo.DatabaseQueryVO;
import com.cvte.scm.wip.domain.common.view.vo.SysViewPageParamVO;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLineRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsInfoVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqLineVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.BillStatusEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.cvte.csb.toolkit.StringUtils.isNotEmpty;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static com.cvte.scm.wip.domain.core.requirement.valueobject.enums.EntireReportFields.*;

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

    public void entireExport(SysViewPageParamVO sysViewPageParam, HttpServletResponse httpServletResponse) {
        List<FieldDefinitionDTO> fieldDefinitionDTOList = Arrays.asList(
                new FieldDefinitionDTO().setType(FieldDefinitionTypeEnum.UNIQUE.getCode()).setOriginFiled("id").setField("id"),
                new FieldDefinitionDTO().setWidth(4000).setType(FieldDefinitionTypeEnum.COMMON.getCode()).setOriginFiled(ITEM_NO.getField()).setField(ITEM_NO.getField()),
                new FieldDefinitionDTO().setWidth(2000).setType(FieldDefinitionTypeEnum.COMMON.getCode()).setOriginFiled(WKP_NO.getField()).setField(WKP_NO.getField()),
                new FieldDefinitionDTO().setWidth(4000).setType(FieldDefinitionTypeEnum.COMMON.getCode()).setOriginFiled(ITEM_DESC.getField()).setField(ITEM_DESC.getField()),
                new FieldDefinitionDTO().setWidth(4000).setType(FieldDefinitionTypeEnum.COMMON.getCode()).setOriginFiled(CRAFT_ATTR.getField()).setField(CRAFT_ATTR.getField()),
                new FieldDefinitionDTO().setWidth(4000).setType(FieldDefinitionTypeEnum.COMMON.getCode()).setOriginFiled(CRAFT_DESC.getField()).setField(CRAFT_DESC.getField()),
                new FieldDefinitionDTO().setWidth(4000).setType(FieldDefinitionTypeEnum.COMMON.getCode()).setOriginFiled(CRAFT_REQ.getField()).setField(CRAFT_REQ.getField()),
                new FieldDefinitionDTO().setWidth(4000).setType(FieldDefinitionTypeEnum.COMMON.getCode()).setOriginFiled(INV_QTY.getField()).setField(INV_QTY.getField()),
                new FieldDefinitionDTO().setWidth(4000).setType(FieldDefinitionTypeEnum.COMMON.getCode()).setOriginFiled(ITEM_CLASS.getField()).setField(ITEM_CLASS.getField()),
                new FieldDefinitionDTO().setWidth(4000).setType(FieldDefinitionTypeEnum.COMMON.getCode()).setOriginFiled(REPLACE_GROUP.getField()).setField(REPLACE_GROUP.getField()),
                new FieldDefinitionDTO().setWidth(5000).setType(FieldDefinitionTypeEnum.COMMON.getCode()).setOriginFiled(IS_FACTORY_PUR.getField()).setField(IS_FACTORY_PUR.getField()),
                new FieldDefinitionDTO().setWidth(4000).setType(FieldDefinitionTypeEnum.COMMON.getCode()).setOriginFiled(REQ_QTY.getField()).setField(REQ_QTY.getField()).setValueMergeType(ValueMergeTypeEnum.SUM),
                new FieldDefinitionDTO().setWidth(10000).setType(FieldDefinitionTypeEnum.ROW_TO_COLUMN.getCode()).setOriginFiled("source_lot_no").setValueField(REQ_QTY.getField())
        );

        final List<String> originHead = Arrays.asList("id", ITEM_NO.getField(), WKP_NO.getField(), ITEM_DESC.getField(), CRAFT_ATTR.getField(), CRAFT_DESC.getField(), CRAFT_REQ.getField(), INV_QTY.getField(), ITEM_CLASS.getField(), REPLACE_GROUP.getField(), IS_FACTORY_PUR.getField(), REQ_QTY.getField(), "source_lot_no");
        final List<String> entireHead = Arrays.asList(ITEM_NO.getField(), WKP_NO.getField(), ITEM_DESC.getField(), CRAFT_ATTR.getField(), CRAFT_DESC.getField(), CRAFT_REQ.getField(), INV_QTY.getField(), ITEM_CLASS.getField(), REPLACE_GROUP.getField(), IS_FACTORY_PUR.getField(), REQ_QTY.getField(), "source_lot_no");
        final Map<String, String> headTextMap = new HashMap<>();
        headTextMap.put(ITEM_NO.getField(), ITEM_NO.getDesc());
        headTextMap.put(WKP_NO.getField(), WKP_NO.getDesc());
        headTextMap.put(ITEM_DESC.getField(), ITEM_DESC.getDesc());
        headTextMap.put(CRAFT_ATTR.getField(), CRAFT_ATTR.getDesc());
        headTextMap.put(CRAFT_DESC.getField(), CRAFT_DESC.getDesc());
        headTextMap.put(CRAFT_REQ.getField(), CRAFT_REQ.getDesc());
        headTextMap.put(ITEM_CLASS.getField(), ITEM_CLASS.getDesc());
        headTextMap.put(REPLACE_GROUP.getField(), REPLACE_GROUP.getDesc());
        headTextMap.put(INV_QTY.getField(), INV_QTY.getDesc());
        headTextMap.put(IS_FACTORY_PUR.getField(), IS_FACTORY_PUR.getDesc());
        headTextMap.put(REQ_QTY.getField(), REQ_QTY.getDesc());

        String sql = viewService.getViewPageSQLByViewPageParam(sysViewPageParam);
        DatabaseQueryVO databaseQuery = new DatabaseQueryVO();
        String databaseId = "pgdb_scm-wip";
        databaseQuery.setId(databaseId);
        databaseQuery.setSql(sql);
        List<Map<String, Object>> pageData = viewService.executeQuery(databaseQuery);

        if (ListUtil.empty(pageData)) {
            throw new ParamsIncorrectException("导出数据查询为空!");
        }
        ExportExcelDTO exportExcelDTO = new ExportExcelDTO();
        exportExcelDTO.setOriginData(pageData)
                .setOriginHeads(originHead)
                .setHeadTextMap(headTextMap)
                .setFileName("test")
                .setFieldDefinitions(fieldDefinitionDTOList)
                .setExportHeads(entireHead)
                .setConverters(Collections.singletonList(new NumberConverter()));
        try {
            ExcelExportUtils.exportAfterConvert(exportExcelDTO, httpServletResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
