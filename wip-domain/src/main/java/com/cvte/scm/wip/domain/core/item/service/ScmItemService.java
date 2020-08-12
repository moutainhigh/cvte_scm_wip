package com.cvte.scm.wip.domain.core.item.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.core.item.entity.ScmItemEntity;
import com.cvte.scm.wip.domain.core.item.repository.ScmItemRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * @author : jf
 * Date    : 2019.02.14
 * Time    : 11:03
 * Email   ：jiangfeng7128@cvte.com
 */
@Service
public class ScmItemService {

    private static final UnaryOperator<String> getOrEmpty = s -> s != null ? s : "";

    private ScmItemRepository scmItemRepository;

    public ScmItemService(ScmItemRepository scmItemRepository) {
        this.scmItemRepository = scmItemRepository;
    }

    public String getItemId(String itemNo) {
        return getOrEmpty.apply(scmItemRepository.getItemId(itemNo));
    }

    public String getItemNo(String itemId) {
        return getOrEmpty.apply(scmItemRepository.getItemNo(itemId));
    }

    public boolean hasInvalidItemNo(String[] itemNos) {
        if (ArrayUtil.isEmpty(itemNos)) {
            return true;
        }
        Set<String> validItemNos = scmItemRepository.getValidItemNos(itemNos);
        return validItemNos.isEmpty() || validItemNos.size() < itemNos.length;
    }

    public Map<String, String> getItemMap(String organizationId, Iterable<String> itemNos) {
        if (StringUtils.isEmpty(organizationId) || CollUtil.isEmpty(itemNos)) {
            return Collections.emptyMap();
        }
        List<ScmItemEntity> itemEntityList = scmItemRepository.selectByItemNos(organizationId, itemNos);
        return itemEntityList.stream().collect(Collectors.toMap(ScmItemEntity::getItemNo, ScmItemEntity::getItemId));
    }

}