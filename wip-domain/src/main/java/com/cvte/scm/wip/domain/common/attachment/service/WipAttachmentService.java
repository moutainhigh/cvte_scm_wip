package com.cvte.scm.wip.domain.common.attachment.service;

import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.base.enums.YesOrNoEnum;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.toolkit.CollectionUtils;
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
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

    public AttachmentVO getAttachmentView(AttachmentQuery attachmentQuery) {

        List<AttachmentVO> attachmentVOS = listAttachmentView(attachmentQuery);

        AttachmentVO attachmentVO;
        if (CollectionUtils.isEmpty(attachmentVOS)) {
            attachmentVO = null;
        } else if (attachmentVOS.size() == 1) {
            attachmentVO = attachmentVOS.get(0);
        } else {
            log.error("[WipAttachmentService][getAttachmentView] select one, but found {}", attachmentVOS.size());
            throw new ParamsIncorrectException("附件信息有误，请联系管理员维护");
        }
        return attachmentVO;
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
