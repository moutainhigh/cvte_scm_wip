package com.cvte.scm.wip.infrastructure.rtc.repository;

import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqItemVO;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcLineQueryVO;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.rtc.mapper.dataobject.WipMtrRtcLDO;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcLineRepository;
import org.springframework.stereotype.Service;
import com.cvte.scm.wip.infrastructure.rtc.mapper.WipMtrRtcLMapper;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.List;

/**
 * 服务实现类
 *
 * @author author
 * @since 2020-09-08
 */
@Service
public class WipMtrRtcLineRepositoryImpl
        extends WipBaseRepositoryImpl<WipMtrRtcLMapper, WipMtrRtcLDO, WipMtrRtcLineEntity>
        implements WipMtrRtcLineRepository {

    @Override
    public List<WipMtrRtcLineEntity> selectByHeaderId(String headerId) {
        Example example = new Example(WipMtrRtcLDO.class);
        example.createCriteria().andEqualTo("headerId", headerId);
        List<WipMtrRtcLDO> lineDOList = mapper.selectByExample(example);
        return this.batchBuildEntity(lineDOList);
    }

    @Override
    public BigDecimal sumQtyExceptCurrent(WipMtrRtcLineQueryVO wipMtrRtcLineQueryVO) {
        return mapper.sumQtyExceptCurrent(wipMtrRtcLineQueryVO);
    }

    @Override
    public List<WipReqItemVO> batchSumUnPostQtyExceptCurrent(WipMtrRtcLineQueryVO queryVO) {
        return mapper.batchSumUnPostQtyExceptCurrent(queryVO);
    }
}
