package com.cvte.scm.wip.domain.common.base;

import com.cvte.csb.core.exception.client.params.SourceNotFoundException;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 继承该类的service对应的 repository 需要相应继承WipBaseRepository
 *
 * @author zy
 * @date 2020-05-21 20:24
 **/
public abstract class WipBaseService<M, T extends WipBaseRepository<M>> {

    @Autowired
    protected T repository;

    public boolean isExist(String id) {
        return repository.isExist(id);
    }

    public M selectOne(M entity) {
        return repository.selectOne(entity);
    }

    public M selectById(Object id) {
        return repository.selectById(id);
    }

    public M selectByIdVerifyExist(String id) {
        M t = this.selectById(id);
        if (ObjectUtils.isNull(t)) {
            throw new SourceNotFoundException(String.format("%s not exist", id));
        } else {
            return t;
        }
    }

    public List<M> selectListByIds(String... ids) {
        return repository.selectListByIds(ids);
    }

    public List<M> selectListByIds(List<M> objects) {
        return repository.selectListByIds(objects);
    }

    public List<M> selectList(M entity) {
        return repository.selectList(entity);
    }

    public List<M> selectListAll() {
        return repository.selectListAll();
    }

    public Long selectCount(M entity) {
        return repository.selectCount(entity);
    }

    public int insert(M entity) {
        return repository.insert(entity);
    }

    public int insertSelective(M entity) {
        return repository.insertSelective(entity);
    }

    public int delete(M entity) {
        return repository.delete(entity);
    }

    public int deleteById(Object id) {
        return repository.deleteById(id);
    }

    public int updateById(M entity) {
        return repository.updateById(entity);
    }

    public int updateSelectiveById(M entity) {
        return repository.updateSelectiveById(entity);
    }

    public int updateByJudgeVersionSelective(M entity) {
        return repository.updateByJudgeVersionSelective(entity);
    }

    @Transactional(
            rollbackFor = {RuntimeException.class}
    )
    public void deleteListByIds(String... ids) {
        repository.deleteListByIds(ids);
    }

    @Transactional(
            rollbackFor = {RuntimeException.class}
    )
    public void deleteListByIds(List<M> objects) {
        repository.deleteListByIds(objects);
    }


    @Transactional(
            rollbackFor = {RuntimeException.class}
    )
    public void insertList(List<M> entityList) {
        repository.insertList(entityList);
    }

    @Transactional(
            rollbackFor = {RuntimeException.class}
    )
    public void updateList(List<M> entityList) {
        repository.updateList(entityList);
    }


}
