package com.dr.framework.common.service;

import com.dr.framework.common.dao.CommonMapper;
import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.common.page.Page;
import com.dr.framework.core.orm.module.EntityRelation;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * baseservice默认实现
 *
 * @author dr
 */
public class DefaultBaseService<T extends IdEntity> implements BaseService<T>, InitializingBean, ApplicationContextAware {
    @Autowired
    protected DefaultDataBaseService defaultDataBaseService;
    @Autowired
    protected CommonMapper commonMapper;
    @Autowired
    private CommonService commonService;
    ApplicationContext applicationContext;

    protected EntityRelation entityRelation;

    private Class<T> entityClass;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long insert(T entity) {
        return commonService.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long updateById(T entity) {
        return commonService.update(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long updateBySqlQuery(SqlQuery<T> sqlQuery) {
        return commonMapper.updateByQuery(sqlQuery);
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public T selectOne(SqlQuery<T> sqlQuery) {
        return commonService.selectOne(sqlQuery);
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public T selectById(String id) {
        Assert.isTrue(!StringUtils.isEmpty(id), "主键不能为空！");
        SqlQuery<T> sqlQuery = SqlQuery.from(getEntityRelation());
        sqlQuery.equal(getEntityRelation().getColumn(IdEntity.ID_COLUMN_NAME), id);
        return selectOne(sqlQuery);
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<T> selectList(SqlQuery<T> sqlQuery) {
        return commonService.selectList(sqlQuery);
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Page<T> selectPage(SqlQuery<T> sqlQuery, int pageIndex, int pageSize) {
        return commonService.selectPage(sqlQuery, pageIndex, pageSize);
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public long count(SqlQuery<T> sqlQuery) {
        return commonMapper.countByQuery(sqlQuery);
    }

    @Override
    public boolean exists(String id) {
        return commonService.exists(entityRelation.getEntityClass(), id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long delete(SqlQuery<T> sqlQuery) {
        return commonService.delete(sqlQuery);
    }

    @Override
    public long deleteById(String... ids) {
        Assert.isTrue(ids.length > 0, "主键不能为空！");
        SqlQuery<T> sqlQuery = SqlQuery.from(getEntityRelation());
        sqlQuery.in(getEntityRelation().getColumn(IdEntity.ID_COLUMN_NAME), ids);
        return delete(sqlQuery);
    }

    /**
     * 获取泛型的实际类型
     *
     * @return
     */
    public synchronized Class<T> getEntityClass() {
        if (entityClass == null) {
            Type sc = getClass().getGenericSuperclass();
            if (ParameterizedType.class.isAssignableFrom(sc.getClass())) {
                Type[] types = ((ParameterizedType) sc).getActualTypeArguments();
                entityClass = (Class<T>) types[0];
            }
        }
        return entityClass;
    }

    public EntityRelation getEntityRelation() {
        return entityRelation;
    }

    public CommonService getCommonService() {
        return commonService;
    }

    @Override
    public void afterPropertiesSet() {
        entityRelation = defaultDataBaseService.getTableInfo(getEntityClass());
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
