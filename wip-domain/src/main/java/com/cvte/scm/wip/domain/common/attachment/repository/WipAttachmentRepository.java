package com.cvte.scm.wip.domain.common.attachment.repository;

import com.cvte.scm.wip.domain.common.attachment.dto.AttachmentQuery;
import com.cvte.scm.wip.domain.common.attachment.dto.AttachmentVO;
import com.cvte.scm.wip.domain.common.attachment.entity.WipAttachmentEntity;
import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;

import java.util.List;

/**
 * @author zy
 * @date 2020-05-25 10:19
 **/
public interface WipAttachmentRepository extends WipBaseRepository<WipAttachmentEntity> {

    List<AttachmentVO> listAttachmentView(AttachmentQuery attachmentQuery);
}
