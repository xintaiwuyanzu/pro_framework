package com.dr.framework.sys.service;

import com.dr.framework.core.security.bo.PermissionHolder;
import com.dr.framework.core.security.bo.PermissionResource;
import com.dr.framework.core.security.bo.PermissionResourcePart;
import com.dr.framework.core.security.bo.ResourceProviderInfo;
import com.dr.framework.core.security.service.ResourceManager;
import com.dr.framework.core.security.service.ResourceProvider;
import com.dr.framework.core.security.service.SecurityManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理所有的资源
 *
 * @author dr
 */
public class DefaultResourceManager extends ApplicationObjectSupport implements ResourceManager, InitializingBean {
    private Map<String, ResourceProvider> resourceProviderMap = Collections.synchronizedMap(new HashMap<>());
    Cache personResourceCache;
    @Autowired
    protected SecurityManager securityManager;

    @Override
    public Collection<ResourceProviderInfo> getResourceProviders() {
        return resourceProviderMap.values().stream().map(ResourceProviderInfo::new).collect(Collectors.toList());
    }

    @Override
    public Collection<PermissionResource> getResourceGroup(String resourceType) {
        if (resourceProviderMap.containsKey(resourceType)) {
            return resourceProviderMap.get(resourceType).getGroupResource();
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<PermissionResourcePart> getResourceParts(String resourceType) {
        if (resourceProviderMap.containsKey(resourceType)) {
            return resourceProviderMap.get(resourceType).getParts();
        }
        return Collections.emptyList();
    }

    /**
     * 获取资源列表
     *
     * @param resourceType 资源类型
     * @param groupId      资源分组Id
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<PermissionResource> getResources(String resourceType, String groupId) {
        ResourceProvider resourceProvider = resourceProviderMap.get(resourceType);
        if (resourceProvider != null) {
            return resourceProvider.getResources(groupId);
        }
        return Collections.emptyList();
    }

    /**
     * 获取指定用户的资源列表
     *
     * @param userId
     * @param resourceType
     * @param groupId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<PermissionResource> getPersonResources(String userId, String resourceType, String groupId) {
        //先从缓存读取
        String cacheKey = String.join("-", userId, resourceType, groupId);
        //缓存没有则在计算
        return personResourceCache.get(cacheKey, () -> doGetResources(userId, resourceType, groupId));
    }

    /**
     * 真正执行获取用户资源操作
     *
     * @param personId
     * @param type
     * @param groupId
     * @return
     */
    private Collection<PermissionResource> doGetResources(String personId, String type, String groupId) {
        //先获取特定类型所有的资源
        Collection<PermissionResource> resources = getResources(type, groupId);
        if (resources.isEmpty()) {
            return resources;
        }
        //在获取用户所有的权限资源
        List<PermissionHolder> rolePermissions = securityManager.userPermissions(personId, type, groupId);
        if (rolePermissions.isEmpty()) {
            return Collections.emptyList();
        }
        return resources.stream().filter(p -> hasPermission(p, rolePermissions)).collect(Collectors.toList());
    }

    /**
     * 判断是否有指定的权限
     *
     * @param resource
     * @param rolePermissions
     * @return
     */
    private boolean hasPermission(PermissionResource resource, List<PermissionHolder> rolePermissions) {
        for (PermissionHolder holder : rolePermissions) {
            if (holder.match(resource.getCode())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void afterPropertiesSet() {
        getApplicationContext().getBeansOfType(ResourceProvider.class)
                .values()
                .forEach(r -> resourceProviderMap.put(r.getType(), r));
        CacheManager cacheManager = getApplicationContext().getBean(CacheManager.class);
        personResourceCache = cacheManager.getCache("core.security.personResource");
    }
}
