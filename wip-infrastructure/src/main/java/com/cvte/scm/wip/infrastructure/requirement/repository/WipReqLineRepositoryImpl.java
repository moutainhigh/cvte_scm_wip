package com.cvte.scm.wip.infrastructure.requirement.repository;

import com.cvte.csb.core.exception.ServerException;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.enums.error.ReqInsErrEnum;
import com.cvte.scm.wip.common.utils.ClassUtils;
import com.cvte.scm.wip.common.utils.CodeableEnumUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqLineRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqLineKeyQueryVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.BillStatusEnum;
import com.cvte.scm.wip.infrastructure.requirement.mapper.WipReqLinesMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.WipReqManualLimitMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqLineDO;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqManualLimitDO;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.cvte.csb.toolkit.StringUtils.isNotEmpty;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 16:29
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class WipReqLineRepositoryImpl implements WipReqLineRepository {

    /**
     * 一个位标识，用于获取枚举 {@link BillStatusEnum} 中 "草稿、已确定、已备料和已领料" 的 code 值列表。
     */
    private static final int DRAFT_CONFIRMED_PREPARED_ISSUED = 15;

    private WipReqLinesMapper wipReqLinesMapper;
    private WipReqManualLimitMapper wipReqManualLimitMapper;
    private NamedParameterJdbcTemplate batchTemplate;

    public WipReqLineRepositoryImpl(WipReqLinesMapper wipReqLinesMapper, WipReqManualLimitMapper wipReqManualLimitMapper, @Qualifier("pgParameterJdbcTemplate") NamedParameterJdbcTemplate batchTemplate) {
        this.wipReqLinesMapper = wipReqLinesMapper;
        this.wipReqManualLimitMapper = wipReqManualLimitMapper;
        this.batchTemplate = batchTemplate;
    }

    @Override
    public WipReqLineEntity selectById(String id) {
        WipReqLineDO lineDO = wipReqLinesMapper.selectByPrimaryKey(id);
        return WipReqLineDO.buildEntity(lineDO);
    }

    @Override
    public List<WipReqLineEntity> selectList(WipReqLineEntity queryEntity) {
        WipReqLineDO queryDO = WipReqLineDO.buildDO(queryEntity);
        queryDO.setSourceCode(null);
        List<WipReqLineDO> lineDOList = wipReqLinesMapper.select(queryDO);
        if (ListUtil.empty(lineDOList)) {
            throw new ParamsIncorrectException("投料行数据不存在");
        }

        List<WipReqLineEntity> lineEntityList = new ArrayList<>();
        for (WipReqLineDO lineDO : lineDOList) {
            WipReqLineEntity lineEntity = WipReqLineDO.buildEntity(lineDO);
            lineEntityList.add(lineEntity);
        }
        return lineEntityList;
    }

    @Override
    public List<WipReqLineEntity> selectByExample(Example example) {
        List<WipReqLineDO> lineDOList = wipReqLinesMapper.selectByExample(example);

        List<WipReqLineEntity> lineEntityList = new ArrayList<>();
        for (WipReqLineDO lineDO : lineDOList) {
            WipReqLineEntity lineEntity = WipReqLineDO.buildEntity(lineDO);
            lineEntityList.add(lineEntity);
        }
        return lineEntityList;
    }

    @Override
    public List<WipReqLineEntity> selectValidByKey(WipReqLineKeyQueryVO keyQueryVO, List<String> statusList) {
        if (StringUtils.isNotBlank(keyQueryVO.getLineId())) {
            WipReqLineDO queryDO = new WipReqLineDO().setLineId(keyQueryVO.getLineId());
            List<WipReqLineDO> lineDOList = wipReqLinesMapper.select(queryDO);
            lineDOList.removeIf(line -> BillStatusEnum.valid(line.getLineStatus()));
            return WipReqLineDO.batchBuildEntity(lineDOList);
        }
        Example example = createCustomExample(keyQueryVO, statusList);
        return selectByExample(example);
    }

    @Override
    public void insertSelective(WipReqLineEntity lineEntity) {
        WipReqLineDO lineDO = WipReqLineDO.buildDO(lineEntity);
        wipReqLinesMapper.insertSelective(lineDO);
    }

    @Override
    public void updateSelectiveById(WipReqLineEntity lineEntity) {
        WipReqLineDO lineDO = WipReqLineDO.buildDO(lineEntity);
        wipReqLinesMapper.updateByPrimaryKeySelective(lineDO);
    }

    @Override
    public void writeIncrementalData(List<String> wipEntityIdList, List<Integer> organizationIdList) {
        wipReqLinesMapper.writeIncrementalData(wipEntityIdList, organizationIdList);
    }

    @Override
    public void writeLackLines(List<String> wipEntityIdList, List<Integer> organizationIdList) {
        batchTemplate.getJdbcOperations().execute("refresh materialized view wip.wip_req_lines_lack_mv");
        wipReqLinesMapper.writeLackLines(wipEntityIdList, organizationIdList);
    }

    private Example createCustomExample(WipReqLineKeyQueryVO keyQueryVO, List<String> statusList) {
        if (StringUtils.isBlank(keyQueryVO.getHeaderId())) {
            throw new ServerException(ReqInsErrEnum.KEY_NULL.getCode(), ReqInsErrEnum.KEY_NULL.getDesc() + "目标投料单头或组织不可为空");
        }
        Example example = new Example(WipReqLineDO.class);
        Example.Criteria criteria = example.createCriteria();
        BiConsumer<String, String> equalOrNull = (p, v) -> criteria.andEqualTo(p, isNotEmpty(v) ? v : null);
        equalOrNull.accept("headerId", keyQueryVO.getHeaderId());
        equalOrNull.accept("organizationId", keyQueryVO.getOrganizationId());
        equalOrNull.accept("lotNumber", keyQueryVO.getLotNumber());
        equalOrNull.accept("wkpNo", keyQueryVO.getWkpNo());
        equalOrNull.accept("itemId", keyQueryVO.getItemId());
        equalOrNull.accept("itemNo", keyQueryVO.getItemNo());
        equalOrNull.accept("posNo", keyQueryVO.getPosNo());
        if (ListUtil.notEmpty(statusList)) {
            criteria.andIn("lineStatus", statusList);
        }
        return example;
    }

    @Override
    public Example createCustomExample(WipReqLineEntity wipReqLine) {
        Example example = new Example(WipReqLineDO.class);
        Example.Criteria criteria = example.createCriteria();
        BiConsumer<String, String> equalOrNull = (p, v) -> criteria.andEqualTo(p, isNotEmpty(v) ? v : null);
        equalOrNull.accept("headerId", wipReqLine.getHeaderId());
        equalOrNull.accept("organizationId", wipReqLine.getOrganizationId());
        equalOrNull.accept("lotNumber", wipReqLine.getLotNumber());
        equalOrNull.accept("wkpNo", wipReqLine.getWkpNo());
        equalOrNull.accept("itemId", wipReqLine.getItemId());
        equalOrNull.accept("itemNo", wipReqLine.getItemNo());
        if (!criteria.isValid()) {
            return null;
        } else if (isNotEmpty(wipReqLine.getPosNo())) {
            criteria.andEqualTo("posNo", wipReqLine.getPosNo());
        } else {
            criteria.andCondition("(pos_no is null or pos_no = '')");
        }
        criteria.andIn("lineStatus", CodeableEnumUtils.getCodesByOrdinalFlag(DRAFT_CONFIRMED_PREPARED_ISSUED, BillStatusEnum.class));
        // 默认example拼接条件时，如果属性值为空，则忽略这条件。如果所有条件都失效，则直接查询"where 1 = 1" 😓。
        return example;
    }

    @Override
    public Example createExample() {
        return new Example(WipReqLineDO.class);
    }

    @Override
    @SneakyThrows
    public List<WipReqLineEntity> selectByColumnAndStatus(WipReqLineEntity lineEntity, int status) {
        WipReqLineDO lineDO = WipReqLineDO.buildDO(lineEntity);
        Example example = new Example(WipReqLineDO.class);
        Example.Criteria criteria = example.createCriteria();
        for (Field field : WipReqLineEntityFieldSingleton.INSTANCE) {
            Object value = field.get(lineDO);
            if (value instanceof String) {
                String[] elements = ((String) value).split(",");
                if (elements.length > 1) {
                    criteria.andIn(field.getName(), Arrays.asList(elements));
                    continue;
                }
            }
            criteria.andEqualTo(field.getName(), value);
        }
        if (!criteria.isValid()) {
            throw new ParamsIncorrectException("投料单行查询条件为空；");
        }
        criteria.andIn("lineStatus", CodeableEnumUtils.getCodesByOrdinalFlag(status, BillStatusEnum.class));
        List<WipReqLineDO> lineDOList = wipReqLinesMapper.selectByExample(example);
        return WipReqLineDO.batchBuildEntity(lineDOList);
    }

    @Override
    public List<String> selectOutRangeItemList(String changeType, String organization, List<String> itemNoList, List<String> outRangeItemNoList) {
        Example example = new Example(WipReqManualLimitDO.class);
        Example.Criteria criteria = example.createCriteria().andEqualTo("organizationId", organization);
        criteria.andEqualTo("changeType", changeType);
        List<WipReqManualLimitDO> wipReqManualLimitDOList = wipReqManualLimitMapper.selectByExample(example);
        List<String> limitItemClassList = wipReqManualLimitDOList.stream().map(WipReqManualLimitDO::getItemClass).collect(Collectors.toList());
        filterOutRangeItemNoList(itemNoList, limitItemClassList, outRangeItemNoList);
        return limitItemClassList;
    }

    private void filterOutRangeItemNoList(List<String> itemNoList, List<String> limitItemClassList, List<String> outRangeItemNoList) {
        for (String itemNo : itemNoList) {
            boolean outRangeFlag = true;
            for (String limitItemClass : limitItemClassList) {
                if (itemNo.startsWith(limitItemClass)) {
                    outRangeFlag = false;
                    break;
                }
            }
            if (outRangeFlag) {
                outRangeItemNoList.add(itemNo);
            }
        }
    }

    /**
     * 投料单行字段单例类，提升字段解析效率。
     */
    private static class WipReqLineEntityFieldSingleton {
        private static final List<Field> INSTANCE = new ArrayList<>(WipReqLineDO.class.getDeclaredFields().length);

        static {
            for (Field field : WipReqLineDO.class.getDeclaredFields()) {
                if (!ClassUtils.isAnnotated(field, Transient.class) && ClassUtils.isAnnotated(field, Column.class)) {
                    field.setAccessible(true);
                    INSTANCE.add(field);
                }
            }
        }
    }

}
