package com.cvte.scm.wip.infrastructure.base;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.core.exception.client.params.SourceNotFoundException;
import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

/**
 * @author zy
 * @date 2020-05-21 16:02
 **/
public abstract class WipBaseRepositoryImpl<M extends CommonMapper<T>, T, E>
        implements WipBaseRepository<E> {

    @Autowired
    @Qualifier("pgSqlSessionFactory")
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    protected M mapper;


    protected abstract List<T> batchBuildDO(List<E> entityList);

    protected abstract T buildDO(E entity);

    protected abstract E buildEntity(T domain);

    protected abstract List<E> batchBuildEntity(List<T> entityList);

    public boolean isExist(String id) {
        E t = this.selectById(id);
        return ObjectUtils.isNotNull(t);
    }

    public E selectOne(E entity) {
        return buildEntity(this.mapper.selectOne(buildDO(entity)));
    }

    public E selectById(Object id) {
        return buildEntity(this.mapper.selectByPrimaryKey(id));
    }

    public E selectByIdVerifyExist(String id) {
        E t = this.selectById(id);
        if (ObjectUtils.isNull(t)) {
            throw new SourceNotFoundException(String.format("%s not exist", id));
        } else {
            return t;
        }
    }

    public List<E> selectListByIds(String... ids) {
        return batchBuildEntity(this.mapper.selectByIds(ids));
    }

    public List<E> selectListByIds(List<E> objects) {
        return batchBuildEntity(this.mapper.selectListByIds(batchBuildDO(objects)));
    }

    public List<E> selectList(E entity) {
        return batchBuildEntity(this.mapper.select(buildDO(entity)));
    }

    public List<E> selectListAll() {
        return batchBuildEntity(this.mapper.selectAll());
    }

    public Long selectCount(E entity) {
        return new Long((long)this.mapper.selectCount(buildDO(entity)));
    }

    public List<E> selectByExample(Object example) {
        return batchBuildEntity(this.mapper.selectByExample(example));
    }

    public int insert(E entity) {
        return this.mapper.insert(buildDO(entity));
    }

    public int insertSelective(E entity) {
        return this.mapper.insertSelective(buildDO(entity));
    }

    public int delete(E entity) {
        return this.mapper.delete(buildDO(entity));
    }

    public int deleteById(Object id) {
        return this.mapper.deleteByPrimaryKey(id);
    }

    public int updateById(E entity) {
        return this.mapper.updateByPrimaryKey(buildDO(entity));
    }

    public int updateSelectiveById(E entity) {
        return this.mapper.updateByPrimaryKeySelective(buildDO(entity));
    }

    public int updateByJudgeVersionSelective(E entity) {
        int result = this.mapper.updateByPrimaryKeySelective(buildDO(entity));
        if (result != 1) {
            throw new ParamsIncorrectException("当前数据已被其他用户编辑过，请刷新后再编辑提交");
        } else {
            return result;
        }
    }

    @Transactional(
            rollbackFor = {RuntimeException.class}
    )
    public void deleteListByIds(String... ids) {
        this.mapper.deleteByIds(ids);
    }

    @Transactional(
            rollbackFor = {RuntimeException.class}
    )
    public void deleteListByIds(List<E> objects) {
        this.mapper.deleteListByIds(batchBuildDO(objects));
    }

    /**
     *
     * <p>
     * 需注意，实体主键生成器不是NoKeyGenerator(如：@GeneratedValue(generator = "JDBC"))的话，批量插入会出错，
     * 具体问题可看 org.apache.ibatis.executor.BatchExecutor.doFlushStatements
     * </p>
     * <p>
     * 如需要添加GeneratedValue注解，可以指定generator为UUID
     * </p>
     *
     * @param entityList
     * @return void
     **/
    @Transactional(
            rollbackFor = {RuntimeException.class}
    )
    public void insertList(List<E> entityList) {
        SqlSession batchSqlSession = null;
        List<T> doList = batchBuildDO(entityList);

        try {
            batchSqlSession = this.sqlSessionFactory.openSession(ExecutorType.BATCH, false);
            BaseMapper baseMapper = (BaseMapper)batchSqlSession.getMapper(this.getMapperClazz());

            for(int index = 0; index < doList.size(); ++index) {
                baseMapper.insertSelective(doList.get(index));
                if (index != 0 && index % 500 == 0) {
                    batchSqlSession.commit();
                }
            }

            batchSqlSession.commit();
            batchSqlSession.clearCache();
        } catch (Exception var8) {
            var8.printStackTrace();
            throw new RuntimeException("csb-jdbc insert batch error", var8);
        } finally {
            if (batchSqlSession != null) {
                batchSqlSession.close();
            }

        }
    }

    @Transactional(
            rollbackFor = {RuntimeException.class}
    )
    public void updateList(List<E> entityList) {
        SqlSession batchSqlSession = null;
        List<T> doList = batchBuildDO(entityList);
        try {
            batchSqlSession = this.sqlSessionFactory.openSession(ExecutorType.BATCH, false);
            BaseMapper baseMapper = (BaseMapper)batchSqlSession.getMapper(this.getMapperClazz());

            for(int index = 0; index < doList.size(); ++index) {
                baseMapper.updateByPrimaryKeySelective(doList.get(index));
                if (index != 0 && index % 500 == 0) {
                    batchSqlSession.commit();
                }
            }

            batchSqlSession.commit();
            batchSqlSession.clearCache();
        } catch (Exception var8) {
            var8.printStackTrace();
            throw new RuntimeException("csb-jdbc update batch error", var8);
        } finally {
            if (batchSqlSession != null) {
                batchSqlSession.close();
            }

        }
    }


    private Class getMapperClazz() throws Exception {
        Class<?>[] interfaces = this.mapper.getClass().getInterfaces();
        Class clazz = null;

        for(int i = 0; i < interfaces.length; ++i) {
            if (BaseMapper.class.isAssignableFrom(interfaces[i])) {
                clazz = interfaces[i];
            }
        }

        if (clazz == null) {
            throw new Exception("mapper not implements interfaces tk.mybatis.mapper.common.BaseMapper");
        } else {
            return clazz;
        }
    }


}
