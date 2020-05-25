package com.cvte.scm.wip.domain.common.attachment.service;

import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.base.enums.YesOrNoEnum;
import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.utils.CurrentContextUtils;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.common.attachment.dto.AttachmentDTO;
import com.cvte.scm.wip.domain.common.attachment.dto.AttachmentQuery;
import com.cvte.scm.wip.domain.common.attachment.dto.AttachmentVO;
import com.cvte.scm.wip.domain.common.attachment.entity.WipAttachmentEntity;
import com.cvte.scm.wip.domain.common.attachment.repository.WipAttachmentRepository;
import com.cvte.scm.wip.domain.common.sys.user.entity.SysUserEntity;
import com.cvte.scm.wip.domain.common.sys.user.repository.SysUserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务实现类
 *
 * @author zy
 * @since 2020-04-29
 */
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipAttachmentService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private WipAttachmentRepository repository;

    @Autowired
    private SysUserRepository sysUserRepository;


    public void saveBatch(List<AttachmentDTO> attachmentSaveDTOList) {

        String curUserName = getCurrentSysUserName();

        List<WipAttachmentEntity> attachmentList = attachmentSaveDTOList.stream().map(el -> {
            el.validate();
            WipAttachmentEntity attachment = modelMapper.map(el, WipAttachmentEntity.class)
                    .setId(UUIDUtils.getUUID())
                    .setCrtName(curUserName)
                    .setUpdName(curUserName)
                    .setIsDel(YesOrNoEnum.NO.getValue());
            EntityUtils.writeStdCrtInfoToEntity(attachment, CurrentContextUtils.getOrEmptyOperatingUser().getId());
            return attachment;
        }).collect(Collectors.toList());

        repository.insertList(attachmentList);
    }

    public void removeById(String id) {

        if (StringUtils.isBlank(id)) {
            throw new ParamsRequiredException("必填参数不可为空");
        }

        String curUserName = getCurrentSysUserName();

        WipAttachmentEntity wipAttachment = new WipAttachmentEntity();
        wipAttachment.setIsDel(YesOrNoEnum.YES.getValue()).setId(id).setUpdName(curUserName);
        EntityUtils.writeStdUpdInfoToEntity(wipAttachment, CurrentContextUtils.getOrEmptyOperatingUser().getId());

        repository.updateSelectiveById(wipAttachment);

    }


    public List<AttachmentVO> listAttachmentView(AttachmentQuery attachmentQuery) {
        return repository.listAttachmentView(attachmentQuery);
    }


    public String getCurrentSysUserName() {

        if (ObjectUtils.isNull(CurrentContext.getCurrentOperatingUser())
            || StringUtils.isBlank(CurrentContext.getCurrentOperatingUser().getId())) {
            return null;
        }

        SysUserEntity sysUser = sysUserRepository.selectByPrimaryKey(CurrentContext.getCurrentOperatingUser().getId());

        if (ObjectUtils.isNull(sysUser)) {
            return "iacUser";
        }
        return sysUser.getName();
    }


}
