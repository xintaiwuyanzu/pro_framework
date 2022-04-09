package com.dr.framework.sys.service;

import com.dr.framework.common.entity.StatusEntity;
import com.dr.framework.common.service.CacheAbleService;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.security.entity.Permission;
import com.dr.framework.core.security.event.SecurityEvent;
import com.dr.framework.core.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * 系统权限
 *
 * @author dr
 */
@Service
public class PermissionService extends CacheAbleService<Permission> {
    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long insert(Permission entity) {
        if (ObjectUtils.isEmpty(entity.getStatus())) {
            entity.setStatus(StatusEntity.STATUS_ENABLE);
        }
        if (!StringUtils.hasText(entity.getGroupId())) {
            entity.setGroupId(Constants.DEFAULT);
        }
        eventPublisher.publishEvent(new SecurityEvent<>(entity));
        return super.insert(entity);
    }

    @Override
    public long updateById(Permission entity) {
        eventPublisher.publishEvent(new SecurityEvent<>(entity));
        return super.updateById(entity);
    }

    @Override
    public long updateBySqlQuery(SqlQuery<Permission> sqlQuery) {
        eventPublisher.publishEvent(new SecurityEvent<>(""));
        return super.updateBySqlQuery(sqlQuery);
    }

    @Override
    public long deleteById(String... ids) {
        eventPublisher.publishEvent(new SecurityEvent<>(""));
        return super.deleteById(ids);
    }

    @Override
    public long delete(SqlQuery<Permission> sqlQuery) {
        eventPublisher.publishEvent(new SecurityEvent<>(""));
        return super.delete(sqlQuery);
    }

    @Override
    protected String getCacheName() {
        return "core.security.permission";
    }
}
