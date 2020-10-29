package com.dr.framework.common.service;

import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.common.page.Page;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 带有缓存能力的service
 * cache的key是主键，拦截增删改查，动态调整缓存
 *
 * @param <T>
 * @author dr
 */
public abstract class CacheAbleService<T extends IdEntity> extends DefaultBaseService<T> {
    @Autowired
    protected CacheManager cacheManager;
    /**
     * 缓存对象
     */
    protected Cache cache;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long updateById(T entity) {
        long result = super.updateById(entity);
        if (result > 0) {
            //更新删除缓存
            cache.evictIfPresent(entity.getId());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long updateBySqlQuery(SqlQuery<T> sqlQuery) {
        /**
         * 先查询出来会修改几条数据
         */
        List<String> ids = selectIdListByQuery(sqlQuery);
        if (ids.isEmpty()) {
            return 0;
        }
        long result = super.updateBySqlQuery(sqlQuery);
        ids.stream().forEach(id -> cache.evictIfPresent(id));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public T selectOne(SqlQuery<T> sqlQuery) {
        List<String> ids = selectIdListByQuery(sqlQuery);
        Assert.isTrue(ids.size() < 2, "查询到多条数据！");
        if (ids.isEmpty()) {
            return null;
        } else {
            return selectById(ids.get(0));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long delete(SqlQuery<T> sqlQuery) {
        List<String> ids = selectIdListByQuery(sqlQuery);
        if (ids.isEmpty()) {
            return 0;
        } else {
            long result = super.delete(sqlQuery);
            ids.forEach(id -> cache.evictIfPresent(id));
            return result;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public List<T> selectList(SqlQuery<T> sqlQuery) {
        return selectIdListByQuery(sqlQuery)
                .stream().map(this::selectById)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public Page<T> selectPage(SqlQuery<T> sqlQuery, int pageIndex, int pageSize) {
        Page<T> page = selectIdPageByQuery(sqlQuery, pageIndex, pageSize);
        List<T> data = page.getData();
        if (data != null && !data.isEmpty()) {
            page.setData(data.stream().map(t -> selectById(t.getId())).collect(Collectors.toList()));
        }
        return page;
    }


    protected SqlQuery<T> makeOnlyId(SqlQuery<T> sqlQuery) {
        getEntityRelation().getColumns()
                .forEach(c -> {
                    if (!c.getName().equalsIgnoreCase(IdEntity.ID_COLUMN_NAME)) {
                        sqlQuery.exclude(c);
                    }
                });
        return sqlQuery;
    }

    protected List<String> selectIdListByQuery(SqlQuery<T> sqlQuery) {
        return commonMapper.selectByQuery(makeOnlyId(sqlQuery)).stream().map(IdEntity::getId).collect(Collectors.toList());
    }

    protected Page<T> selectIdPageByQuery(SqlQuery<T> sqlQuery, int pageIndex, int pageSize) {
        return super.selectPage(makeOnlyId(sqlQuery), pageIndex, pageSize);
    }

    @Override
    public T selectById(String id) {
        return cache.get(id, () -> super.selectById(id));
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        cache = cacheManager.getCache(getCacheName());
    }

    /**
     * 获取缓存的主键
     *
     * @return
     */
    abstract protected String getCacheName();
}
