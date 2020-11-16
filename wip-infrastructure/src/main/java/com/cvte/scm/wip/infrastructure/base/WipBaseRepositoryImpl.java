package com.cvte.scm.wip.infrastructure.base;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.core.exception.client.params.SourceNotFoundException;
import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.scm.wip.domain.common.base.BaseModel;
import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zy
 * @date 2020-05-21 16:02
 **/
@Slf4j
public abstract class WipBaseRepositoryImpl<M extends CommonMapper<T>, T extends BaseModel, E extends BaseModel>
        extends RepositoryTypeReference<T, E>
        implements WipBaseRepository<E> {

    @Autowired
    @Qualifier("pgSqlSessionFactory")
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    protected M mapper;

    @Autowired
    protected ModelMapper modelMapper;

    protected Class<E> getEntityClass() {
        return (Class<E>) getDomainEntityRawType();
    }

    protected Class<T> getDomainClass() {
        return (Class<T>) getDataObjectRawType();
    }

    protected List<T> batchBuildDO(List<E> domainEntityList) {
        return domainEntityList.stream().map(el -> buildDO(el)).collect(Collectors.toList());
    }

    protected T buildDO(E domainEntity) {
        return modelMapper.map(domainEntity, getDomainClass());
    }

    protected E buildEntity(T dataObject) {
        if (Objects.isNull(dataObject)) {
            return null;
        }
        return modelMapper.map(dataObject, getEntityClass());
    }

    protected List<E> batchBuildEntity(List<T> domainEntityList) {
        return domainEntityList.stream().map(el -> buildEntity(el)).collect(Collectors.toList());
    }

    public boolean isExist(String id) {
        E t = this.selectById(id);
        return ObjectUtils.isNotNull(t);
    }

    public E selectOne(E domainEntity) {
        return buildEntity(this.mapper.selectOne(buildDO(domainEntity)));
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

    public List<E> selectList(E domainEntity) {
        return batchBuildEntity(this.mapper.select(buildDO(domainEntity)));
    }

    public List<E> selectListAll() {
        return batchBuildEntity(this.mapper.selectAll());
    }

    public Long selectCount(E domainEntity) {
        return new Long((long)this.mapper.selectCount(buildDO(domainEntity)));
    }

    public List<E> selectByExample(Object example) {
        return batchBuildEntity(this.mapper.selectByExample(example));
    }

    public int insert(E domainEntity) {
        return this.mapper.insert(buildDO(domainEntity));
    }

    public int insertSelective(E domainEntity) {
        return this.mapper.insertSelective(buildDO(domainEntity));
    }

    public int delete(E domainEntity) {
        return this.mapper.delete(buildDO(domainEntity));
    }

    public int deleteById(Object id) {
        return this.mapper.deleteByPrimaryKey(id);
    }

    public int updateById(E domainEntity) {
        return this.mapper.updateByPrimaryKey(buildDO(domainEntity));
    }

    public int updateSelectiveById(E domainEntity) {
        return this.mapper.updateByPrimaryKeySelective(buildDO(domainEntity));
    }

    public int updateByJudgeVersionSelective(E domainEntity) {
        int result = this.mapper.updateByPrimaryKeySelective(buildDO(domainEntity));
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
     * @param domainEntityList
     * @return void
     **/
    @Transactional(
            rollbackFor = {RuntimeException.class}
    )
    public void insertList(List<E> domainEntityList) {
        SqlSession batchSqlSession = null;
        List<T> doList = batchBuildDO(domainEntityList);

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
    public void updateList(List<E> domainEntityList) {
        SqlSession batchSqlSession = null;
        List<T> doList = batchBuildDO(domainEntityList);
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


    @Transactional(
            rollbackFor = {RuntimeException.class}
    )
    public void updateListForce(List<E> domainEntityList) {
        SqlSession batchSqlSession = null;
        List<T> doList = batchBuildDO(domainEntityList);
        try {
            batchSqlSession = this.sqlSessionFactory.openSession(ExecutorType.BATCH, false);
            BaseMapper baseMapper = (BaseMapper)batchSqlSession.getMapper(this.getMapperClazz());

            for(int index = 0; index < doList.size(); ++index) {
                baseMapper.updateByPrimaryKey(doList.get(index));
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

    @Transactional(
            rollbackFor = {RuntimeException.class}
    )
    public void deleteList(List<E> domainEntityList) {
        SqlSession batchSqlSession = null;
        List<T> doList = batchBuildDO(domainEntityList);
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
