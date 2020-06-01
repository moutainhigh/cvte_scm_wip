package com.cvte.scm.wip.domain.core.requirement.service;

import com.alibaba.excel.context.AnalysisContext;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.constants.CommonDateConstant;
import com.cvte.scm.wip.common.constants.CommonUserConstant;
import com.cvte.scm.wip.common.listener.ExcelListener;
import com.cvte.scm.wip.common.utils.*;
import com.cvte.scm.wip.domain.core.requirement.dto.WipItemWkpPostImportDTO;
import com.cvte.scm.wip.domain.core.requirement.entity.WipItemWkpPosEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipItemWkpPosRepository;
import com.cvte.scm.wip.domain.core.requirement.valueobject.QueryWipItemWkpPosVO;
import com.cvte.scm.wip.domain.core.scm.dto.query.SysOrgOrganizationVQuery;
import com.cvte.scm.wip.domain.core.scm.service.ScmViewCommonService;
import com.cvte.scm.wip.domain.core.scm.vo.SysOrgOrganizationVO;
import jodd.util.StringUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zy
 * @date 2020-05-22 14:40
 **/
@Service
public class WipItemWkpPosService {

    @Autowired
    private WipItemWkpPosRepository repository;

    @Autowired
    private ScmViewCommonService scmViewCommonService;

    @Autowired
    private ModelMapper modelMapper;


    /**
     *
     *
     * @param queryWipItemWkpPosVO
     * @return java.util.List<com.cvte.scm.wip.domain.core.requirement.entity.WipItemWkpPosEntity>
     **/
    public List<WipItemWkpPosEntity> listWipItemWkpPosEntity(QueryWipItemWkpPosVO queryWipItemWkpPosVO) {
        return repository.listWipItemWkpPosEntity(queryWipItemWkpPosVO);
    }

    /**
     * 批量保存
     *
     * 根据"产品规格 + 物料编码 + 组织"匹配当前生效数据，如果数据已存在，则失效旧数据
     *
     * @param wipItemWkpPosEntities
     * @return void
     **/
    public void batchSave(List<WipItemWkpPosEntity> wipItemWkpPosEntities) {
        if (CollectionUtils.isEmpty(wipItemWkpPosEntities)) {
            return;
        }

        Date curDate = new Date();

        QueryWipItemWkpPosVO queryWipItemWkpPosVO = new QueryWipItemWkpPosVO()
                .setQueryDate(curDate)
                .setItemCodes(LambdaUtils.mapToList(wipItemWkpPosEntities, WipItemWkpPosEntity::getItemCode))
                .setProductModels(LambdaUtils.mapToList(wipItemWkpPosEntities, WipItemWkpPosEntity::getProductModel))
                .setOrganizationIds(LambdaUtils.mapToList(wipItemWkpPosEntities, WipItemWkpPosEntity::getOrganizationId));
        List<WipItemWkpPosEntity> existEntity = this.listWipItemWkpPosEntity(queryWipItemWkpPosVO);

        Set<String> postImportKeySet = wipItemWkpPosEntities.stream().map(WipItemWkpPosEntity::generateUniqueKey).collect(Collectors.toSet());

        List<WipItemWkpPosEntity> invalidList = new ArrayList<>();
        for (WipItemWkpPosEntity wipItemWkpPosEntity : existEntity) {
            if (postImportKeySet.contains(wipItemWkpPosEntity.generateUniqueKey())) {
                WipItemWkpPosEntity invalidEntity = new WipItemWkpPosEntity()
                        .setId(wipItemWkpPosEntity.getId())
                        .setEndDate(curDate);
                EntityUtils.writeStdUpdInfoToEntity(invalidEntity, CurrentContextUtils.getOrDefaultUserId(CommonUserConstant.SCM_WIP));
                invalidList.add(invalidEntity);
            }
        }

        wipItemWkpPosEntities.forEach(el -> {
            el.setId(UUIDUtils.getUUID()).setBeginDate(curDate).setEndDate(CommonDateConstant.END_DATE);
            EntityUtils.writeCurUserStdCrtInfoToEntity(el);
        });

        repository.updateList(invalidList);
        repository.insertList(wipItemWkpPosEntities);
    }

    /**
     * 批量导入
     *
     * 根据"产品规格 + 物料编码 + 组织"匹配当前生效数据，如果数据已存在，则失效旧数据
     *
     * @param wipItemWkpPostImportDTOS
     * @return void
     **/
    public void batchSaveWipItemWkpPostImportDTO(List<WipItemWkpPostImportDTO> wipItemWkpPostImportDTOS) {
        if (CollectionUtils.isEmpty(wipItemWkpPostImportDTOS)) {
            return;
        }
        batchSave(modelMapper.map(wipItemWkpPostImportDTOS, new TypeToken<List<WipItemWkpPosEntity>>(){}.getType()));
    }

    /**
     * 校验导入数据
     *
     * @param wipItemWkpPostImportDTOS
     * @return void
     **/
    public void validateAndInitWipItemWkpPostImportDTO(List<WipItemWkpPostImportDTO> wipItemWkpPostImportDTOS) {

        Map<String, String> orgCodeAndIdMap = getOrgCodeAndIdMapByImportDTOS(wipItemWkpPostImportDTOS);


        StringBuilder errMsgs = new StringBuilder();
        Set<String> uniqueKeySet = new HashSet<>();
        wipItemWkpPostImportDTOS.forEach(el -> {

            StringBuilder errMsg = new StringBuilder(ValidateUtils.validate(el));
            if (StringUtil.isNotBlank(el.getOrganizationCode()) && !orgCodeAndIdMap.containsKey(el.getOrganizationCode())) {
                errMsg.append(String.format("组织%s未找到;", el.getOrganizationCode()));
            }

            el.setOrganizationId(orgCodeAndIdMap.get(el.getOrganizationCode()));
            String uniqueKey = el.generateUniqueKey();

            if (uniqueKeySet.contains(uniqueKey)) {
                errMsg.append("不可同时导入重复数据;");
            }
            if (errMsg.length() > 0) {
                errMsgs.append(String.format("【第%d行：%s】", el.getRowNum(), errMsg));
            }

            uniqueKeySet.add(uniqueKey);
        });

        if (errMsgs.length() > 0) {
            throw new ParamsIncorrectException("导入数据有误，请检查：" + errMsgs.toString());
        }

    }



    public List<WipItemWkpPostImportDTO> parseExcel(MultipartFile file) {
        ExcelListener<WipItemWkpPostImportDTO> listener = new ExcelListener<WipItemWkpPostImportDTO>() {
            @Override
            public void invoke(WipItemWkpPostImportDTO wipItemWkpPostImportDTO, AnalysisContext analysisContext) {
                // 记录Excel数据行数，便于记录错误信息。
                wipItemWkpPostImportDTO.setRowNum(analysisContext.readRowHolder().getRowIndex());
                super.invoke(wipItemWkpPostImportDTO, analysisContext);
            }
        };
        try {
            return ExcelUtils.readExcel(file.getInputStream(), WipItemWkpPostImportDTO.class, listener);
        } catch (IOException e) {
            throw new ParamsIncorrectException("excel解析错误，请联系管理员");
        }
    }

    private Map<String, String> getOrgCodeAndIdMapByImportDTOS(List<WipItemWkpPostImportDTO> wipItemWkpPostImportDTOS) {
        List<String> organizationCodes = wipItemWkpPostImportDTOS.stream()
                .map(WipItemWkpPostImportDTO::getOrganizationCode)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        List<SysOrgOrganizationVO> sysOrgOrganizationVOS = scmViewCommonService.listSysOrgOrganizationVO(
                new SysOrgOrganizationVQuery().setEbsOrganizationCodes(organizationCodes));
        return sysOrgOrganizationVOS.stream()
                .collect(Collectors.toMap(SysOrgOrganizationVO::getEbsOrganizationCode, SysOrgOrganizationVO::getEbsOrganizationId));
    }


}
