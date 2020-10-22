package com.cvte.scm.wip.infrastructure.rtc.repository;

import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity;
import com.cvte.scm.wip.domain.core.rtc.valueobject.enums.WipMtrRtcHeaderStatusEnum;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.rtc.mapper.dataobject.WipMtrRtcHDO;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcHeaderRepository;
import org.springframework.stereotype.Service;
import com.cvte.scm.wip.infrastructure.rtc.mapper.WipMtrRtcHMapper;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务实现类
 *
 * @author author
 * @since 2020-09-08
 */
@Service
public class WipMtrRtcHeaderRepositoryImpl
        extends WipBaseRepositoryImpl<WipMtrRtcHMapper, WipMtrRtcHDO, WipMtrRtcHeaderEntity>
        implements WipMtrRtcHeaderRepository {

    @Override
    public List<WipMtrRtcHeaderEntity> selectUnPost(WipMtrRtcHeaderEntity rtcHeader) {
        Example example = new Example(WipMtrRtcHDO.class);
        example.createCriteria()
                .andEqualTo("organizationId", rtcHeader.getOrganizationId())
                .andEqualTo("factoryId", rtcHeader.getFactoryId())
                .andEqualTo("wkpNo", rtcHeader.getWkpNo())
                .andEqualTo("moId", rtcHeader.getMoId())
                .andEqualTo("billType", rtcHeader.getBillType())
                .andIn("billStatus", WipMtrRtcHeaderStatusEnum.getUnCompleteStatus().stream().map(WipMtrRtcHeaderStatusEnum::getCode).collect(Collectors.toList()));
        return batchBuildEntity(this.mapper.selectByExample(example));
    }

}
