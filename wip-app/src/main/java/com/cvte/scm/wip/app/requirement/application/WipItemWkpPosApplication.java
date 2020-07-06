package com.cvte.scm.wip.app.requirement.application;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.scm.wip.domain.core.requirement.dto.WipItemWkpPostImportDTO;
import com.cvte.scm.wip.domain.core.requirement.service.WipItemWkpPosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author zy
 * @date 2020-05-25 17:10
 **/
@Component
@Transactional(transactionManager = "pgTransactionManager")
public class WipItemWkpPosApplication {

    @Autowired
    private WipItemWkpPosService wipItemWkpPosService;

    /**
     * 导入excel
     *
     * @param file
     * @return void
     **/
    public void importExcel(MultipartFile file) {

        List<WipItemWkpPostImportDTO> wipItemWkpPostImportDTOS = wipItemWkpPosService.parseExcel(file);

        if (CollectionUtils.isEmpty(wipItemWkpPostImportDTOS)) {
            throw new ParamsIncorrectException("导入数据不能为空");
        }
        batchSave(wipItemWkpPostImportDTOS);
    }

    public void saveImport(List<WipItemWkpPostImportDTO> wipItemWkpPostImportDTOS) {
        if (CollectionUtils.isEmpty(wipItemWkpPostImportDTOS)) {
            return;
        }

        for (int i = 0; i < wipItemWkpPostImportDTOS.size(); i++) {
            wipItemWkpPostImportDTOS.get(i).setRowNum(i + 1);
        }

        batchSave(wipItemWkpPostImportDTOS);

    }

    private void batchSave(List<WipItemWkpPostImportDTO> wipItemWkpPostImportDTOS) {

        wipItemWkpPosService.validateAndInitWipItemWkpPostImportDTO(wipItemWkpPostImportDTOS);

        wipItemWkpPosService.batchSaveWipItemWkpPostImportDTO(wipItemWkpPostImportDTOS);
    }



}
