package com.cvte.scm.wip.domain.core.ckd.service;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.core.exception.client.params.SourceNotFoundException;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.utils.CurrentContextUtils;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.common.base.WipBaseService;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcTaskLineQuery;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcTaskLineVersionQuery;
import com.cvte.scm.wip.domain.core.ckd.dto.view.WipMcTaskCompareView;
import com.cvte.scm.wip.domain.core.ckd.dto.view.WipMcTaskLineVersionView;
import com.cvte.scm.wip.domain.core.ckd.dto.view.WipMcTaskLineView;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcTaskLineEntity;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcTaskLineVersionEntity;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcTaskVersionEntity;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskLineStatusEnum;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskVersionChangTypeEnum;
import com.cvte.scm.wip.domain.core.ckd.repository.WipMcTaskVersionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 服务实现类
 *
 * @author zy
 * @since 2020-04-28
 */
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipMcTaskVersionService extends WipBaseService<WipMcTaskVersionEntity, WipMcTaskVersionRepository> {

    @Autowired
    private WipMcTaskLineService wipMcTaskLineService;

    @Autowired
    private WipMcTaskLineVersionService wipMcTaskLineVersionService;

    @Autowired
    private WipMcTaskValidateService wipMcTaskValidateService;

    @Autowired
    private ModelMapper modelMapper;


    public void batchSync(List<String> taskIds) {

        for (String taskId : taskIds) {
            sync(taskId);
        }
    }

    public void sync(String taskId) {
        sync(taskId, true);
    }

    public void sync(String taskId, boolean isAuth) {
        sync(taskId, isAuth, null);
    }

    public void sync(String taskId, List<WipMcTaskLineView> wipMcTaskLineViews) {
        sync(taskId, true, wipMcTaskLineViews);
    }

    public void sync(String taskId, boolean isAuth, List<WipMcTaskLineView> wipMcTaskLineViews) {

        if (StringUtils.isBlank(taskId)) {
            throw new ParamsIncorrectException("配料任务id不能为空");
        }

        WipMcTaskVersionEntity wipMcTaskVersion = repository.getLastVersion(taskId);
        if (ObjectUtils.isNull(wipMcTaskVersion)) {
            throw new SourceNotFoundException("版本信息不存在");
        }

        // 校验版本是否可更新
        if (isAuth) {
            wipMcTaskValidateService.validateUpdVersion(taskId);
        }

        List<WipMcTaskLineVersionView> wipMcTaskLineVersionViews = wipMcTaskLineVersionService
                .listWipMcTaskLineVersionView(new WipMcTaskLineVersionQuery().setVersionId(wipMcTaskVersion.getVersionId()));

        Map<String, WipMcTaskLineVersionView> wipMcTaskLineVersionViewMap = wipMcTaskLineVersionViews.stream()
                .collect(Collectors.toMap(WipMcTaskLineVersionView::getSoLineId, Function.identity()));


        // 版本任务清单
        wipMcTaskLineViews = ObjectUtils.isNotNull(wipMcTaskLineViews) ? wipMcTaskLineViews :
                wipMcTaskLineService.listWipMcTaskLineView(new WipMcTaskLineQuery().setTaskIds(Arrays.asList(taskId)));

        List<WipMcTaskLineVersionEntity> lineVersionInsertList = new ArrayList<>();
        List<WipMcTaskLineVersionEntity> lineVersionUpdateList = new ArrayList<>();
        wipMcTaskLineViews.forEach(el -> {

            WipMcTaskLineVersionEntity wipMcTaskLineVersion = modelMapper.map(el, WipMcTaskLineVersionEntity.class);
            wipMcTaskLineVersion.setId(UUIDUtils.getUUID())
                    .setVersionId(wipMcTaskVersion.getVersionId())
                    .setSoLineNo(el.getSourceLineNo())
                    .setSoLineId(el.getSourceLineId())
                    .setQty(el.getMcQty());

            WipMcTaskLineVersionView wipMcTaskLineVersionView = wipMcTaskLineVersionViewMap.get(wipMcTaskLineVersion.getSoLineId());
            if (ObjectUtils.isNull(wipMcTaskLineVersionView)) {
                EntityUtils.writeStdCrtInfoToEntity(wipMcTaskLineVersion, CurrentContextUtils.getOrDefaultUserId("SCM-WIP"));
                lineVersionInsertList.add(wipMcTaskLineVersion);
            } else {
                wipMcTaskLineVersion.setId(wipMcTaskLineVersionView.getId());
                EntityUtils.writeStdUpdInfoToEntity(wipMcTaskLineVersionView, CurrentContextUtils.getOrDefaultUserId("SCM-WIP"));
                lineVersionUpdateList.add(wipMcTaskLineVersion);

                wipMcTaskLineVersionViewMap.remove(wipMcTaskLineVersion.getSoLineId());
            }
        });

        // 更新删除数据
        List<String> lineVersionDeleteIdList = new ArrayList<>();
        for (WipMcTaskLineVersionView deleteView : wipMcTaskLineVersionViewMap.values()) {
            lineVersionDeleteIdList.add(deleteView.getId());
        }


        wipMcTaskVersion.setVersionDate(new Date());
        EntityUtils.writeStdUpdInfoToEntity(wipMcTaskVersion, CurrentContextUtils.getOrDefaultUserId("SCM-WIP"));

        repository.updateSelectiveById(wipMcTaskVersion);
        wipMcTaskLineVersionService.insertList(lineVersionInsertList);
        wipMcTaskLineVersionService.updateList(lineVersionUpdateList);

        if (CollectionUtils.isNotEmpty(lineVersionDeleteIdList)) {
            wipMcTaskLineVersionService.deleteListByIds(lineVersionDeleteIdList.toArray(new String[0]));
        }
    }


    public void add(String taskId, List<WipMcTaskLineEntity> wipMcTaskLineList) {

        if (StringUtils.isBlank(taskId)) {
            throw new ParamsIncorrectException("配料任务id不能为空");
        }

        // 版本信息
        WipMcTaskVersionEntity wipMcTaskVersion = new WipMcTaskVersionEntity();
        wipMcTaskVersion.setVersionId(UUIDUtils.getUUID())
                .setVersionNo(getCurVersionNo(taskId))
                .setMcTaskId(taskId)
                .setVersionDate(new Date());
        EntityUtils.writeStdCrtInfoToEntity(wipMcTaskVersion, CurrentContextUtils.getOrDefaultUserId("SCM-WIP"));

        // 版本任务清单
        wipMcTaskLineList = ObjectUtils.isNotNull(wipMcTaskLineList)
                ? wipMcTaskLineList : wipMcTaskLineService.selectList(new WipMcTaskLineEntity().setMcTaskId(taskId));

        List<WipMcTaskLineVersionEntity> wipMcTaskLineVersions = new ArrayList<>();
        wipMcTaskLineList.forEach(el -> {

            WipMcTaskLineVersionEntity wipMcTaskLineVersion = modelMapper.map(el, WipMcTaskLineVersionEntity.class);
            wipMcTaskLineVersion.setId(UUIDUtils.getUUID())
                    .setVersionId(wipMcTaskVersion.getVersionId())
                    .setSoLineNo(el.getSourceLineNo())
                    .setSoLineId(el.getSourceLineId())
                    .setQty(el.getMcQty());
            EntityUtils.writeStdCrtInfoToEntity(wipMcTaskLineVersion, CurrentContextUtils.getOrDefaultUserId("SCM-WIP"));
            wipMcTaskLineVersions.add(wipMcTaskLineVersion);
        });

        repository.insertSelective(wipMcTaskVersion);
        wipMcTaskLineVersionService.insertList(wipMcTaskLineVersions);
    }

    public void add(String taskId) {
        add(taskId, null);
    }

    public List<WipMcTaskCompareView> compareTaskVersion(String compareVersionId, String compareToVersionId) {

        if (StringUtils.isBlank(compareVersionId, compareToVersionId)) {
            throw new ParamsRequiredException("请选择比较版本");
        }


        List<WipMcTaskLineVersionView> compareList = wipMcTaskLineVersionService.listWipMcTaskLineVersionView(
                new WipMcTaskLineVersionQuery().setVersionId(compareVersionId).setLineStatus(McTaskLineStatusEnum.NORMAL.getCode())
        );
        List<WipMcTaskLineVersionView> compareToList = wipMcTaskLineVersionService.listWipMcTaskLineVersionView(
                new WipMcTaskLineVersionQuery().setVersionId(compareToVersionId).setLineStatus(McTaskLineStatusEnum.NORMAL.getCode())
        );

        compareList = calTotalQtyByItemId(compareList);
        compareToList = calTotalQtyByItemId(compareToList);


        Map<String, WipMcTaskLineVersionView> compareToMap =
                compareToList.stream().collect(Collectors.toMap(WipMcTaskLineVersionView::getItemId, Function.identity()));

        List<WipMcTaskCompareView> wipMcTaskCompareViews = new ArrayList<>();
        for (WipMcTaskLineVersionView compareItem : compareList) {

            // 先设入默认值
            WipMcTaskCompareView wipMcTaskCompareView = new WipMcTaskCompareView();
            wipMcTaskCompareView.setItemId(compareItem.getItemId())
                    .setItemCode(compareItem.getItemCode())
                    .setQty(compareItem.getQty())
                    .setChangTypeCode(McTaskVersionChangTypeEnum.INSERT.getCode())
                    .setChangTypeName(McTaskVersionChangTypeEnum.INSERT.getValue());

            // 如果被比较对象也含有该订单行，比较数量
            if (compareToMap.containsKey(compareItem.getItemId())) {

                WipMcTaskLineVersionView compareToItem = compareToMap.get(compareItem.getItemId());
                Integer changeQty = compareItem.getQty() - compareToItem.getQty();

                if (changeQty == 0) {
                    // 两个版本都有，数量又没变化的，不返回
                    compareToMap.remove(compareItem.getItemId());
                    continue;
                }

                // 设入具体变更值
                McTaskVersionChangTypeEnum mcTaskVersionChangTypeEnum = changeQty > 0 ?
                        McTaskVersionChangTypeEnum.INCREMENT : McTaskVersionChangTypeEnum.DECREMENT;
                wipMcTaskCompareView.setChangTypeCode(mcTaskVersionChangTypeEnum.getCode())
                        .setChangTypeName(mcTaskVersionChangTypeEnum.getValue())
                        .setChangQty(Math.abs(changeQty));

                // 移除比较过的数据
                compareToMap.remove(compareItem.getItemId());
            }

            wipMcTaskCompareViews.add(wipMcTaskCompareView);

        }

        // 处理比较版本里没有，被比较版本里有的
        for (WipMcTaskLineVersionView compareToItem : compareToMap.values()) {

            WipMcTaskCompareView wipMcTaskCompareView = new WipMcTaskCompareView();
            wipMcTaskCompareView.setItemId(compareToItem.getItemId())
                    .setItemCode(compareToItem.getItemCode())
                    .setQty(compareToItem.getQty())
                    .setChangTypeCode(McTaskVersionChangTypeEnum.DELETE.getCode())
                    .setChangTypeName(McTaskVersionChangTypeEnum.DELETE.getValue());
            wipMcTaskCompareViews.add(wipMcTaskCompareView);
        }


        return wipMcTaskCompareViews;
    }



    public List<WipMcTaskVersionEntity> listWipMcTaskVersion(String taskId) {
        return repository.listWipMcTaskVersion(taskId);
    }

    private String getCurVersionNo(String taskId) {

        WipMcTaskVersionEntity wipMcTaskVersion = repository.getLastVersion(taskId);

        return ObjectUtils.isNull(wipMcTaskVersion) ? "0" : String.valueOf(Long.valueOf(wipMcTaskVersion.getVersionNo()) + 1);
    }

    /**
     * 合并相同物料，并返回合并后的列表, 处理后的列表字段仅itemId,ItemCode,qty有效
     *
     * @param wipMcTaskLineVersionViews
     * @return java.util.List<com.cvte.scm.wip.ckd.dto.view.WipMcTaskLineVersionView>
     **/
    private List<WipMcTaskLineVersionView> calTotalQtyByItemId(List<WipMcTaskLineVersionView> wipMcTaskLineVersionViews) {
        if (CollectionUtils.isEmpty(wipMcTaskLineVersionViews)) {
            return new ArrayList<>();
        }

        List<WipMcTaskLineVersionView> calTotalList = new ArrayList<>();
        Map<String, WipMcTaskLineVersionView> itemIdAndViewMap = new HashMap<>();
        for (WipMcTaskLineVersionView view : wipMcTaskLineVersionViews) {
            if (!itemIdAndViewMap.containsKey(view.getItemId())) {
                itemIdAndViewMap.put(view.getItemId(), view);
                calTotalList.add(view);
                continue;
            }

            WipMcTaskLineVersionView totalView = itemIdAndViewMap.get(view.getItemId());
            totalView.setQty(totalView.getQty() + view.getQty());
        }

        return calTotalList;
    }
}
