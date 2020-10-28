package com.dr.framework.sys.service;

import com.dr.framework.common.entity.StatusEntity;
import com.dr.framework.common.service.CacheAbleService;
import com.dr.framework.core.security.entity.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 系统权限
 *
 * @author dr
 */
@Service
public class PermissionService extends CacheAbleService<Permission> {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long insert(Permission entity) {
        if (StringUtils.isEmpty(entity.getStatus())) {
            entity.setStatus(StatusEntity.STATUS_ENABLE);
        }
        return super.insert(entity);
    }

    @Override
    protected String getCacheName() {
        return "core.security.permission";
    }
}
