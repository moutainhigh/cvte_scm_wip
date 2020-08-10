package com.cvte.scm.wip.infrastructure.rework.repository;

import com.cvte.csb.base.enums.OrderTypeEnum;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.core.rework.entity.WipReworkMoEntity;
import com.cvte.scm.wip.domain.core.rework.repository.WipReworkMoRepository;
import com.cvte.scm.wip.domain.core.rework.valueobject.WipRwkMoVO;
import com.cvte.scm.wip.domain.core.rework.valueobject.enums.ReworkTypeEnum;
import com.cvte.scm.wip.domain.core.rework.valueobject.enums.WipMoReworkLotStatusEnum;
import com.cvte.scm.wip.infrastructure.rework.mapper.WipRwkItemMapper;
import com.cvte.scm.wip.infrastructure.rework.mapper.WipRwkMoMapper;
import com.cvte.scm.wip.infrastructure.rework.mapper.dataobject.WipRwkItemDO;
import com.cvte.scm.wip.infrastructure.rework.mapper.dataobject.WipRwkMoDO;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/24 15:43
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class WipReworkMoRepositoryImpl implements WipReworkMoRepository {

    private WipRwkMoMapper wipRwkMoMapper;
    private WipRwkItemMapper wipRwkItemMapper;

    public WipReworkMoRepositoryImpl(WipRwkMoMapper wipRwkMoMapper, WipRwkItemMapper wipRwkItemMapper) {
        this.wipRwkMoMapper = wipRwkMoMapper;
        this.wipRwkItemMapper = wipRwkItemMapper;
    }

    @Override
    public List<WipReworkMoEntity> selectByEntity(WipReworkMoEntity entity) {
        WipRwkMoDO moDO = WipRwkMoDO.buildDO(entity);
        List<WipRwkMoDO> moEntityList = wipRwkMoMapper.select(moDO);
        return WipRwkMoDO.batchBuildEntity(moEntityList);
    }

    @Override
    public List<WipReworkMoEntity> selectByParam(WipRwkMoVO moParam) {
        List<WipReworkMoEntity> reworkMoList;
        if (ReworkTypeEnum.isItemRework(moParam.getReworkType())) {
            Example example = createItemExample(moParam);
            List<WipRwkItemDO> doList = wipRwkItemMapper.selectByExample(example);
            reworkMoList = WipRwkItemDO.batchBuildEntity(doList);
        } else {
            Example example = createMoLotExample(moParam);
            List<WipRwkMoDO> doList = wipRwkMoMapper.selectByExample(example);
            reworkMoList = WipRwkMoDO.batchBuildEntity(doList);
        }
        for (WipReworkMoEntity wipReworkMoEntity : reworkMoList) {
            if (null == wipReworkMoEntity.getSourceLotNo()) {
                wipReworkMoEntity.setSourceLotNo("");
            }
            if (null == wipReworkMoEntity.getLotStatus()) {
                wipReworkMoEntity.setLotStatus(WipMoReworkLotStatusEnum.IN_STOCK.getDesc());
            }
        }
        return reworkMoList;
    }

    private Example createMoLotExample(WipRwkMoVO rwkMoVO) {
        Example example = new Example(WipRwkMoDO.class);
        if (StringUtils.isBlank(rwkMoVO.getLotStatus())) {
            throw new ParamsIncorrectException("批次状态不可为空");
        }
        WipMoReworkLotStatusEnum lotStatusEnum = WipMoReworkLotStatusEnum.getMode(rwkMoVO.getLotStatus());
        if (Objects.isNull(lotStatusEnum)) {
            throw new ParamsIncorrectException(String.format("不支持的批次状态【%s】", rwkMoVO.getLotStatus()));
        }
        // lot_status = 1 or lot_status = "库存"
        Example.Criteria criteria = example.createCriteria();
        List<String> lotStatusList = new ArrayList<>();
        lotStatusList.add(lotStatusEnum.getCode());
        lotStatusList.add(lotStatusEnum.getDesc());
        criteria.andIn("lotStatus", lotStatusList);
        if (StringUtils.isNotBlank(rwkMoVO.getProductNo())) {
            List<String> productArr = Arrays.asList(rwkMoVO.getProductNo().split(","));
            if (productArr.size() > 1) {
                criteria.andIn("productNo", productArr);
            } else {
                criteria.andLike("productNo", "%" + rwkMoVO.getProductNo() + "%");
            }
        }
        if (StringUtils.isNotBlank(rwkMoVO.getConsumerName())) {
            criteria.andLike("consumerName", "%" + rwkMoVO.getConsumerName() + "%");
        }
        if (StringUtils.isNotBlank(rwkMoVO.getCustItemNum())) {
            criteria.andLike("custItemNum", "%" + rwkMoVO.getCustItemNum() + "%");
        }
        if (StringUtils.isNotBlank(rwkMoVO.getSourceLotNo())) {
            criteria.andLike("sourceLotNo", "%" + rwkMoVO.getSourceLotNo() + "%");
        }
        createRwkCriteria(rwkMoVO, criteria);
        return example;
    }

    private Example createItemExample(WipRwkMoVO rwkMoVO) {
        Example example = new Example(WipRwkItemDO.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(rwkMoVO.getProductNo())) {
            List<String> productArr = Arrays.asList(rwkMoVO.getProductNo().split(","));
            if (productArr.size() > 1) {
                criteria.andIn("itemNo", productArr);
            } else {
                criteria.andLike("itemNo", "%" + rwkMoVO.getProductNo() + "%");
            }
            createRwkCriteria(rwkMoVO, criteria);
        }
        return example;
    }

    private void createRwkCriteria(WipRwkMoVO rwkMoVO, Example.Criteria criteria) {
        if (StringUtils.isNotBlank(rwkMoVO.getFactoryId())) {
            criteria.andEqualTo("factoryId", Integer.parseInt(rwkMoVO.getFactoryId()));
        }
        if (StringUtils.isNotBlank(rwkMoVO.getShipOrgId())) {
            criteria.andEqualTo("organizationId", rwkMoVO.getShipOrgId());
        }

        if (Objects.isNull(rwkMoVO.getPageNum())) {
            rwkMoVO.setPageNum(1);
        }
        if (Objects.isNull(rwkMoVO.getPageSize())) {
            rwkMoVO.setPageSize(30);
        }
        if (rwkMoVO.isNeedPage()) {
            PageHelper.startPage(rwkMoVO.getPageNum(), rwkMoVO.getPageSize());
            if (StringUtils.isNotBlank(rwkMoVO.getOrderBy())) {
                PageHelper.orderBy(rwkMoVO.getOrderBy() + " " + (Objects.isNull(rwkMoVO.getOrder()) ? OrderTypeEnum.DESC : rwkMoVO.getOrder()).toString());
            }
        }
    }

}
