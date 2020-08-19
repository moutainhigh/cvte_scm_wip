package com.cvte.scm.wip.domain.common.repository;

import java.util.List;

/**
 * @author zy
 * @date 2020-05-21 16:45
 **/
public interface WipBaseRepository <E> {

    boolean isExist(String id);

    E selectOne(E entity);

    E selectById(Object id);

    E selectByIdVerifyExist(String id);

    List<E> selectListByIds(String... ids);

    List<E> selectListByIds(List<E> objects);

    List<E> selectList(E entity);

    List<E> selectListAll();

    Long selectCount(E entity);

    List<E> selectByExample(Object example);

    int insert(E entity);

    int insertSelective(E entity);

    int delete(E entity);

    int deleteById(Object id);

    int updateById(E entity);

    int updateSelectiveById(E entity);

    int updateByJudgeVersionSelective(E entity);

    void deleteListByIds(String... ids);

    void deleteListByIds(List<E> objects);


    void insertList(List<E> entityList);
    
    void updateList(List<E> entityList);

    /**
     * 主要是为了满足部分场景下需要将字段更新为null
     *
     * @param entityList
     * @return void
     **/
    void updateListForce(List<E> entityList);

}
