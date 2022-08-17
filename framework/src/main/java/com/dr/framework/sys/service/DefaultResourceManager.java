package com.dr.framework.sys.service;

import com.dr.framework.common.entity.TreeNode;
import com.dr.framework.common.service.CommonService;
import com.dr.framework.common.service.DataBaseService;
import com.dr.framework.core.security.bo.PermissionMatcher;
import com.dr.framework.core.security.bo.PermissionResource;
import com.dr.framework.core.security.bo.PermissionResourcePart;
import com.dr.framework.core.security.bo.ResourceProviderInfo;
import com.dr.framework.core.security.entity.Permission;
import com.dr.framework.core.security.entity.SubSystem;
import com.dr.framework.core.security.entity.SysMenu;
import com.dr.framework.core.security.event.PermissionResourceChangeEvent;
import com.dr.framework.core.security.event.SecurityEvent;
import com.dr.framework.core.security.service.ResourceManager;
import com.dr.framework.core.security.service.ResourceProvider;
import com.dr.framework.core.security.service.SecurityManager;
import com.dr.framework.core.util.Constants;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理所有的资源
 *
 * @author dr
 */
@Service
public class DefaultResourceManager implements ResourceManager, InitDataService.DataInit, InitializingBean, ApplicationContextAware {
    private Map<String, ResourceProvider> resourceProviderMap = Collections.synchronizedMap(new HashMap<>());
    /**
     * 缓存计算出来的资源，只需计算一次即可
     * key是 人员Id+资源类型+资源分组
     * <p>
     * 资源变更需要清空缓存
     * <p>
     * 角色权限变更需要清空缓存
     */
    Cache personResourceCache;
    /**
     * 资源类型和缓存集合map
     */
    Map<String, Set<String>> typeKeyMap = Collections.synchronizedMap(new HashMap<>());

    @Autowired
    protected SecurityManager securityManager;
    protected ApplicationContext applicationContext;

    @Override
    public List<ResourceProviderInfo> getResourceProviders() {
        return resourceProviderMap.values().stream().map(ResourceProviderInfo::new).collect(Collectors.toList());
    }

    @Override
    public List<? extends PermissionResource> getResourceGroup(String resourceType) {
        if (resourceProviderMap.containsKey(resourceType)) {
            return resourceProviderMap.get(resourceType).getGroupResource();
        }
        return Collections.emptyList();
    }

    @Override
    public List<PermissionResourcePart> getResourceParts(String resourceType) {
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
    public List<? extends PermissionResource> getResources(String resourceType, String groupId) {
        ResourceProvider resourceProvider = resourceProviderMap.get(resourceType);
        if (resourceProvider != null) {
            return resourceProvider.getResources(groupId);
        }
        return Collections.emptyList();
    }

    @Override
    public List<TreeNode<? extends PermissionResource>> getResourcesTree(String resourceType, String groupId) {
        return listToTree(getResources(resourceType, groupId), groupId);
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
    public List<? extends PermissionResource> getPersonResources(String userId, String resourceType, String groupId) {
        //先从缓存读取
        String cacheKey = String.join("-", userId, resourceType, groupId);
        //缓存key
        typeKeyMap.computeIfAbsent(resourceType, k -> Collections.synchronizedSet(new HashSet<>()))
                .add(cacheKey);
        //缓存没有则在计算
        return personResourceCache.get(cacheKey, () -> {
            List<? extends PermissionResource> resources = doGetResources(userId, resourceType, groupId);
            if (resources.isEmpty()) {
                resources = resourceProviderMap.get(resourceType).getResourcesWhenEmpty(userId, groupId);
            }
            return resources;
        });
    }

    @Override
    public List<TreeNode<? extends PermissionResource>> getPersonResourceTree(String userId, String resourceType, String groupId) {
        return listToTree(getPersonResources(userId, resourceType, groupId), groupId);
    }


    List<TreeNode<? extends PermissionResource>> listToTree(List<? extends PermissionResource> treeNodes, String groupId) {
        if (treeNodes == null || treeNodes.isEmpty()) {
            return Collections.emptyList();
        }
        //特殊处理系统编码，系统默认id为default，加载菜单的时候会出问题
        if (treeNodes.get(0) instanceof SubSystem && Constants.DEFAULT.equals(groupId)) {
            return CommonService.listToTree(
                            treeNodes,
                            "$pid_DEFAULT",
                            PermissionResource::getId,
                            o -> "$pid_DEFAULT",
                            PermissionResource::getOrder,
                            PermissionResource::getName,
                            null,
                            false)
                    .stream().map(
                            t -> (TreeNode<PermissionResource>) t
                    ).collect(Collectors.toList());
        } else {
            return CommonService.listToTree(
                            treeNodes,
                            groupId,
                            PermissionResource::getId,
                            PermissionResource::getParentId,
                            PermissionResource::getOrder,
                            PermissionResource::getName,
                            null,
                            false)
                    .stream().map(
                            t -> (TreeNode<PermissionResource>) t
                    ).collect(Collectors.toList());
        }
    }

    /**
     * 真正执行获取用户资源操作
     *
     * @param personId
     * @param type
     * @param groupId
     * @return
     */
    private List<? extends PermissionResource> doGetResources(String personId, String type, String groupId) {
        //先获取特定类型所有的资源
        List<? extends PermissionResource> resources = getResources(type, groupId);
        if (resources.isEmpty()) {
            return resources;
        }
        //在获取用户所有的权限资源
        List<PermissionMatcher> rolePermissions = securityManager.userPermissions(personId, type, groupId);
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
    private boolean hasPermission(PermissionResource resource, List<PermissionMatcher> rolePermissions) {
        for (PermissionMatcher holder : rolePermissions) {
            if (holder.match(resource.getId())) {
                return true;
            }
            if (!(resource instanceof SysMenu)) {
                if (holder.match(resource.getCode())) {
                    return true;
                }
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

    /**
     * 监听权限资源变化，删除缓存
     *
     * @param event
     */
    @EventListener
    public synchronized void listenResourceChange(SecurityEvent event) {
        if (event instanceof PermissionResourceChangeEvent) {
            String type = (String) event.getSource();
            if (StringUtils.hasText(type) && typeKeyMap.containsKey(type)) {
                Set<String> cacheKeySet = typeKeyMap.get(type);
                cacheKeySet.forEach(k -> personResourceCache.evictIfPresent(k));
                typeKeyMap.remove(type);
            }
        } else {
            clearCache();
        }
    }

    /**
     * 清空缓存
     */
    private synchronized void clearCache() {
        personResourceCache.clear();
        typeKeyMap.clear();
    }

    @Autowired
    protected PermissionService permissionService;

    /**
     * 添加所有资源的所有权限，并将权限赋给超级管理员
     *
     * @param dataBaseService
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initData(DataBaseService dataBaseService) {
        resourceProviderMap.values().forEach(
                p -> {
                    String permissionId = p.getType();
                    if (!permissionService.exists(permissionId)) {
                        //添加菜单超级管理员权限
                        Permission permission = new Permission();
                        permission.setId(permissionId);
                        permission.setCode(SecurityManager.defaultMatcher);
                        permission.setSys(true);
                        permission.setGroupId(SecurityManager.defaultMatcher);
                        permission.setType(p.getType());
                        permission.setName(p.getName() + "所有权限");
                        permissionService.insert(permission);
                        //将权限赋给超级管理员角色
                        securityManager.addPermissionToRole(RoleService.adminRoleId, permission.getId());
                    }
                }
        );
    }

    @Override
    public int order() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
