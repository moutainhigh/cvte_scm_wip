package com.cvte.scm.wip.domain.core.requirement.service;

import com.alibaba.excel.context.AnalysisContext;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.listener.ExcelListener;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.common.utils.ExcelUtils;
import com.cvte.scm.wip.common.utils.ValidateUtils;
import com.cvte.scm.wip.domain.core.requirement.dto.WipItemWkpPostImportDTO;
import com.cvte.scm.wip.domain.core.requirement.dto.query.QueryWipItemWkpPosVO;
import com.cvte.scm.wip.domain.core.requirement.entity.WipItemWkpPosEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipItemWkpPosRepository;
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
    private ModelMapper modelMapper;

    /**
     * 根据导入excel删除原有数据
     * <p>
     *     删除规则：根据"产品规格 + 物料编码"匹配，匹配到则删除
     * </p>
     *
     * @param wipItemWkpPostImportDTOS
     * @return void
     **/
    public void deleteByWipItemWkpPostImportDTO(List<WipItemWkpPostImportDTO> wipItemWkpPostImportDTOS) {

        Map<String, Set<String>> importMap = new HashMap<>();
        for (WipItemWkpPostImportDTO wipItemWkpPostImportDTO : wipItemWkpPostImportDTOS) {
            String itemCode = wipItemWkpPostImportDTO.getItemCode();
            if (!importMap.containsKey(itemCode)) {
                importMap.put(itemCode, new HashSet<>());
            }
            importMap.get(itemCode).add(wipItemWkpPostImportDTO.getProductModel());
        }

        List<String> itemCodes = wipItemWkpPostImportDTOS.stream().map(WipItemWkpPostImportDTO::getItemCode).collect(Collectors.toList());
        List<WipItemWkpPosEntity> wipItemWkpPosEntities =
                repository.listWipItemWkpPosEntity(new QueryWipItemWkpPosVO().setItemCodes(itemCodes));
        List<String> deleteList = new ArrayList<>();
        for (WipItemWkpPosEntity wipItemWkpPosEntity : wipItemWkpPosEntities) {
            String itemCode = wipItemWkpPosEntity.getItemCode();
            if (importMap.containsKey(itemCode)
                    && importMap.get(itemCode).contains(wipItemWkpPosEntity.getProductModel())) {
                deleteList.add(wipItemWkpPosEntity.getId());
            }
        }

        if (CollectionUtils.isNotEmpty(deleteList)) {
            repository.deleteListByIds(deleteList.toArray(new String[0]));
        }
    }

    public void batchSave(List<WipItemWkpPosEntity> wipItemWkpPosEntities) {
        repository.insertList(wipItemWkpPosEntities);
    }

    /**
     * 批量导入
     *
     * @param wipItemWkpPostImportDTOS
     * @return void
     **/
    public void batchSaveWipItemWkpPostImportDTO(List<WipItemWkpPostImportDTO> wipItemWkpPostImportDTOS) {
        if (CollectionUtils.isEmpty(wipItemWkpPostImportDTOS)) {
            return;
        }

        List<WipItemWkpPosEntity> wipItemWkpPosEntities =
                modelMapper.map(wipItemWkpPostImportDTOS, new TypeToken<List<WipItemWkpPosEntity>>(){}.getType());
        wipItemWkpPosEntities.forEach(el -> {
            el.setId(UUIDUtils.getUUID());
            EntityUtils.writeCurUserStdCrtInfoToEntity(el);
        });
        repository.insertList(wipItemWkpPosEntities);
    }


    /**
     * 校验导入数据
     *
     * @param wipItemWkpPostImportDTOS
     * @return void
     **/
    public void validateWipItemWkpPostImportDTO(List<WipItemWkpPostImportDTO> wipItemWkpPostImportDTOS) {
        StringBuilder errMsgs = new StringBuilder();
        List<String> itemCodes = new ArrayList<>();
        wipItemWkpPostImportDTOS.forEach(el -> {
            String errMsg = ValidateUtils.validate(el);
            if (StringUtils.isNotBlank(errMsg)) {
                errMsgs.append(String.format("【第%d行：%s】", el.getRowNum(), errMsg));
            }

            itemCodes.add(el.getItemCode());
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
}
