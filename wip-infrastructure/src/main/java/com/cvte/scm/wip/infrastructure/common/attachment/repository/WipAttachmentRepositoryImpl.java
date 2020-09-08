package com.cvte.scm.wip.infrastructure.common.attachment.repository;

import com.cvte.csb.base.enums.YesOrNoEnum;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.scm.wip.domain.common.attachment.dto.AttachmentQuery;
import com.cvte.scm.wip.domain.common.attachment.dto.AttachmentVO;
import com.cvte.scm.wip.domain.common.attachment.entity.WipAttachmentEntity;
import com.cvte.scm.wip.domain.common.attachment.repository.WipAttachmentRepository;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.common.attachment.mapper.WipAttachmentMapper;
import com.cvte.scm.wip.infrastructure.common.attachment.mapper.dataobject.WipAttachmentDO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zy
 * @date 2020-05-25 10:25
 **/
@Repository
public class WipAttachmentRepositoryImpl
    extends WipBaseRepositoryImpl<WipAttachmentMapper, WipAttachmentDO, WipAttachmentEntity>
    implements WipAttachmentRepository {

    public List<AttachmentVO> listAttachmentView(AttachmentQuery attachmentQuery) {

        Example example = new Example(WipAttachmentDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("referenceId", attachmentQuery.getReferenceId())
                .andEqualTo("referenceType", attachmentQuery.getReferenceType())
                .andEqualTo("id", attachmentQuery.getId())
                .andEqualTo("isDel", YesOrNoEnum.NO.getValue());

        if (CollectionUtils.isNotEmpty(attachmentQuery.getCrtUsers())) {
            criteria.andIn("crtUser", attachmentQuery.getCrtUsers());
        }

        example.orderBy("crtTime");
        List<WipAttachmentDO> attachmentList = mapper.selectByExample(example);

        List<AttachmentVO> attachmentViewList = attachmentList.stream()
                .map(att -> modelMapper.map(att, AttachmentVO.class))
                .collect(Collectors.toList());

        return attachmentViewList;

    }
}
