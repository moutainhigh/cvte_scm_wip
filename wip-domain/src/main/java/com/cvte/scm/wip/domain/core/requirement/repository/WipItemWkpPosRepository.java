package com.cvte.scm.wip.domain.core.requirement.repository;

import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.QueryWipItemWkpPosVO;
import com.cvte.scm.wip.domain.core.requirement.entity.WipItemWkpPosEntity;

import java.util.List;

public interface WipItemWkpPosRepository extends WipBaseRepository<WipItemWkpPosEntity> {

    List<WipItemWkpPosEntity> listWipItemWkpPosEntity(QueryWipItemWkpPosVO query);
}
