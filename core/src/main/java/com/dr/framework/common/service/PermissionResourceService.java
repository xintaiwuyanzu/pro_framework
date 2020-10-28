package com.dr.framework.common.service;

import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.security.bo.PermissionResource;
import com.dr.framework.core.security.event.PermissionResourceChangeEvent;
import com.dr.framework.core.security.service.ResourceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

/**
 * 拦截增删改查，发布资源变更消息
 *
 * @param <T>
 * @author dr
 */
public abstract class PermissionResourceService<T extends PermissionResource> extends CacheAbleService<T> implements ResourceProvider {
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    long mapPublish(long result) {
        if (result > 0) {
            applicationEventPublisher.publishEvent(new PermissionResourceChangeEvent(getType()));
        }
        return result;
    }

    @Override
    public long updateById(T entity) {
        return mapPublish(super.updateById(entity));
    }

    @Override
    public long insert(T entity) {
        return mapPublish(super.insert(entity));
    }

    @Override
    public long updateBySqlQuery(SqlQuery<T> sqlQuery) {
        return mapPublish(super.updateBySqlQuery(sqlQuery));
    }

    @Override
    public long delete(SqlQuery<T> sqlQuery) {
        return mapPublish(super.delete(sqlQuery));
    }

    @Override
    public PermissionResource getResource(String resourceId) {
        return selectById(resourceId);
    }
}
