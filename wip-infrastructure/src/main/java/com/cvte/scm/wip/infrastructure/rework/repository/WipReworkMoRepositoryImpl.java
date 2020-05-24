package com.cvte.scm.wip.infrastructure.rework.repository;

import com.cvte.csb.base.enums.OrderTypeEnum;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.core.rework.entity.WipReworkMoEntity;
import com.cvte.scm.wip.domain.core.rework.repository.WipReworkMoRepository;
import com.cvte.scm.wip.domain.core.rework.valueobject.WipRwkMoVO;
import com.cvte.scm.wip.domain.core.rework.valueobject.enums.WipMoReworkLotStatusEnum;
import com.cvte.scm.wip.infrastructure.rework.mapper.WipRwkMoMapper;
import com.cvte.scm.wip.infrastructure.rework.mapper.dataobject.WipRwkMoDO;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    public WipReworkMoRepositoryImpl(WipRwkMoMapper wipRwkMoMapper) {
        this.wipRwkMoMapper = wipRwkMoMapper;
    }

    @Override
    public List<WipReworkMoEntity> selectByEntity(WipReworkMoEntity entity) {
        WipRwkMoDO moDO = WipRwkMoDO.buildDO(entity);
        List<WipRwkMoDO> moEntityList = wipRwkMoMapper.select(moDO);
        return WipRwkMoDO.batchBuildEntity(moEntityList);
    }

    @Override
    public List<WipReworkMoEntity> selectByParam(WipRwkMoVO moParam) {
        Example example = createMoLotExample(moParam);
        List<WipRwkMoDO> doList = wipRwkMoMapper.selectByExample(example);
        return WipRwkMoDO.batchBuildEntity(doList);
    }

    private Example createMoLotExample(WipRwkMoVO rwkMoDTO) {
        Example example = new Example(WipRwkMoDO.class);
        if (StringUtils.isBlank(rwkMoDTO.getLotStatus())) {
            throw new ParamsIncorrectException("批次状态不可为空");
        }
        WipMoReworkLotStatusEnum lotStatusEnum = WipMoReworkLotStatusEnum.getMode(rwkMoDTO.getLotStatus());
        if (Objects.isNull(lotStatusEnum)) {
            throw new ParamsIncorrectException(String.format("不支持的批次状态【%s】", rwkMoDTO.getLotStatus()));
        }
        // lot_status = 1 or lot_status = "库存"
        Example.Criteria criteria = example.createCriteria();
        List<String> lotStatusList = new ArrayList<>();
        lotStatusList.add(lotStatusEnum.getCode());
        lotStatusList.add(lotStatusEnum.getDesc());
        criteria.andIn("lotStatus", lotStatusList);
        if (StringUtils.isNotBlank(rwkMoDTO.getProductNo())) {
            List<String> productArr = Arrays.asList(rwkMoDTO.getProductNo().split(","));
            if (productArr.size() > 1) {
                criteria.andIn("productNo", productArr);
            } else {
                criteria.andLike("productNo", "%" + rwkMoDTO.getProductNo() + "%");
            }
        }
        if (StringUtils.isNotBlank(rwkMoDTO.getConsumerName())) {
            criteria.andLike("consumerName", "%" + rwkMoDTO.getConsumerName() + "%");
        }
        if (StringUtils.isNotBlank(rwkMoDTO.getCustItemNum())) {
            criteria.andLike("custItemNum", "%" + rwkMoDTO.getCustItemNum() + "%");
        }
        if (StringUtils.isNotBlank(rwkMoDTO.getSourceLotNo())) {
            criteria.andLike("sourceLotNo", "%" + rwkMoDTO.getSourceLotNo() + "%");
        }
        if (StringUtils.isNotBlank(rwkMoDTO.getFactoryId())) {
            criteria.andEqualTo("factoryId", Integer.parseInt(rwkMoDTO.getFactoryId()));
        }
        if (StringUtils.isNotBlank(rwkMoDTO.getShipOrgId())) {
            criteria.andEqualTo("organizationId", rwkMoDTO.getShipOrgId());
        }

        if (Objects.isNull(rwkMoDTO.getPageNum())) {
            rwkMoDTO.setPageNum(1);
        }
        if (Objects.isNull(rwkMoDTO.getPageSize())) {
            rwkMoDTO.setPageSize(30);
        }
        if (rwkMoDTO.isNeedPage()) {
            PageHelper.startPage(rwkMoDTO.getPageNum(), rwkMoDTO.getPageSize());
            if (StringUtils.isNotBlank(rwkMoDTO.getOrderBy())) {
                PageHelper.orderBy(rwkMoDTO.getOrderBy() + " " + (Objects.isNull(rwkMoDTO.getOrder()) ? OrderTypeEnum.DESC : rwkMoDTO.getOrder()).toString());
            }
        }
        return example;
    }

}
