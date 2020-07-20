package com.dr.framework.sys.service;


import com.dr.framework.common.dao.CommonMapper;
import com.dr.framework.common.entity.BaseEntity;
import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.common.entity.StatusEntity;
import com.dr.framework.common.entity.TreeNode;
import com.dr.framework.common.page.Page;
import com.dr.framework.common.service.CommonService;
import com.dr.framework.common.service.DataBaseService;
import com.dr.framework.common.service.DefaultDataBaseService;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.organise.query.PersonQuery;
import com.dr.framework.core.organise.service.OrganisePersonService;
import com.dr.framework.core.orm.module.EntityRelation;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.security.entity.*;
import com.dr.framework.core.security.query.PermissionQuery;
import com.dr.framework.core.security.query.RoleQuery;
import com.dr.framework.core.security.query.SubSysQuery;
import com.dr.framework.core.security.query.SysMenuQuery;
import com.dr.framework.core.security.service.SecurityManager;
import com.dr.framework.sys.entity.PermissionExistVo;
import com.dr.framework.util.Constants;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 默认权限相关的service
 * TODO 加缓存
 * TODO 权限，人员组关联
 * TODO 继承权限
 * TODO 互斥权限
 * TODO 日志
 *
 * @author dr
 */
@Service
public class DefaultSecurityManager
        extends RelationHelper
        implements SecurityManager, InitializingBean, InitDataService.DataInit {
    @Autowired
    CommonMapper commonMapper;
    @Autowired
    CommonService commonService;
    @Autowired
    DefaultDataBaseService defaultDataBaseService;
    @Autowired
    OrganisePersonService organisePersonService;

    EntityRelation roleRelation;
    EntityRelation roleGroupRelation;
    EntityRelation rolePermissionRelation;
    EntityRelation rolePersonRelation;


    EntityRelation permissionRelation;
    EntityRelation permissionRelationRelation;

    EntityRelation sysMenuRelation;
    EntityRelation subSysRelation;


    EntityRelation personRelation;

    @Override
    public List<Role> userRoles(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return Collections.EMPTY_LIST;
        }
        return commonMapper.selectByQuery(
                SqlQuery.from(roleRelation)
                        .in(
                                roleRelation.getColumn(IdEntity.ID_COLUMN_NAME),
                                SqlQuery.from(rolePersonRelation, false)
                                        .column(rolePersonRelation.getColumn("roleId"))
                                        .equal(rolePersonRelation.getColumn("personId"), userId)
                        )
        ).stream()
                .map(o -> (Role) o)
                .collect(Collectors.toList());
    }

    @Override
    public List<Person> roleUsers(String roleId) {
        if (StringUtils.isEmpty(roleId)) {
            return Collections.emptyList();
        }
        return commonMapper.selectByQuery(
                SqlQuery.from(personRelation)
                        .in(
                                personRelation.getColumn(IdEntity.ID_COLUMN_NAME),
                                SqlQuery.from(rolePersonRelation, false)
                                        .column(rolePersonRelation.getColumn("personId"))
                                        .equal(rolePersonRelation.getColumn("roleId"), roleId)
                        )
        ).stream()
                .map(o -> (Person) o)
                .collect(Collectors.toList());
    }

    @Override
    public List<Permission> userPermissions(String userId) {
        List<Role> roles = userRoles(userId);
        SqlQuery<Permission> query = SqlQuery.from(permissionRelation);

        SqlQuery subQuery = SqlQuery.from(rolePermissionRelation, false)
                .column(rolePermissionRelation.getColumn("permission_id"))
                .in(rolePermissionRelation.getColumn("roleId"),
                        roles.stream().map(BaseEntity::getId).collect(Collectors.toList()));

        query.in(permissionRelation.getColumn(IdEntity.ID_COLUMN_NAME), subQuery);
        //逻辑权限
        List<Permission> permissions = commonMapper.selectByQuery(query);
        //菜单权限
        SqlQuery<SysMenu> sysMenuSqlQuery = SqlQuery.from(sysMenuRelation);
        sysMenuSqlQuery.in(sysMenuRelation.getColumn(IdEntity.ID_COLUMN_NAME), subQuery);

        List<SysMenu> sysMenus = commonMapper.selectByQuery(sysMenuSqlQuery);
        permissions.addAll(sysMenus.stream()
                .map(s -> {
                    Permission p = new Permission();
                    p.setName(s.getName());
                    p.setId(s.getId());
                    p.setCode(s.getUrl());
                    return p;
                })
                .collect(Collectors.toList()));


        return permissions;
    }

    @Override
    public List<Person> permissionUsers(String permissionId) {
        //TODO 这个逻辑太复杂，
        return null;
    }

    @Override
    public boolean hasRole(String userid, String... roleCodes) {
        List<String> roles1 = userRoles(userid)
                .stream()
                .map(r -> r.getCode())
                .collect(Collectors.toList());
        for (String r : roleCodes) {
            if (!roles1.contains(r)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean hasPermission(String userid, String... permissionCodes) {
        List<String> pCodes = userPermissions(userid)
                .stream()
                .map(r -> r.getCode())
                .collect(Collectors.toList());
        for (String p : permissionCodes) {
            if (!StringUtils.isEmpty(p) && !pCodes.contains(p)) {
                return false;
            }
        }
        return true;
    }

    Person checkUser(String userId) {
        Person person = organisePersonService.getPerson(new PersonQuery.Builder().idEqual(userId).build());
        Assert.notNull(person, "未查询到指定的用户");
        return person;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addRoleToUser(String userId, String... role) {
        checkUser(userId);
        //查出来该用户已经有的角色
        List<String> roles = userRoles(userId).stream().map(BaseEntity::getId).collect(Collectors.toList());
        //TODO 过滤没有的角色id
        for (String r : role) {
            if (!roles.contains(r)) {
                RolePerson rolePerson = new RolePerson();
                rolePerson.setPersonId(userId);
                rolePerson.setRoleId(r);
                commonMapper.insert(rolePerson);
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long removeUserRole(String userId, String... roleIds) {
        checkUser(userId);

        SqlQuery sqlQuery = SqlQuery.from(rolePersonRelation)
                .equal(rolePersonRelation.getColumn("personId"), userId)
                .in(rolePersonRelation.getColumn("roleId"), roleIds);
        //删除用户角色关联
        return commonMapper.deleteByQuery(sqlQuery);
    }

    /**
     * 这里的逻辑取个巧
     * 给每一个用户建立一个指定类型的角色，该条角色只被该用户拥有。
     * 这样给用户加权限就是往这条特定的角色上添加权限。
     * 从而其他相关的角色逻辑还是能够正常执行
     * <p>
     * TODO 删除用户的时候该用户的唯一角色也得删除掉
     *
     * @param userId
     * @param permission
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPermissionToUser(String userId, String... permission) {
        Person person = checkUser(userId);
        //1、查询或者创建该用户唯一的角色，这里直接根据用户id作为角色的code
        List<Role> roles = selectRoleList(new RoleQuery.Builder().codeLike(userId).build());
        Role role;
        Assert.isTrue(roles.size() < 2, "查询到多条用户角色，数据错误！");
        if (roles.isEmpty()) {
            //还没添加角色则给该用户添加角色
            role = new Role();
            role.setCode(userId);
            role.setDescription(person.getUserName() + "专有角色");
            role.setName(person.getUserName());
            role.setType(Role.USER_DEFAULT_ROLE);
            addRole(role);
            addRoleToUser(userId, role.getId());
        } else {
            role = roles.get(0);
        }
        return addPermissionToRole(role.getId(), permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long removeUserPermission(String userId, String... permissionIds) {
        checkUser(userId);
        return removeRolePermission(userId, permissionIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPermissionToRole(String roleId, String... permission) {
        List<Role> roles = selectRoleList(new RoleQuery.Builder().idEqual(roleId).build());
        Assert.isTrue(roles.size() == 1, "未查询到指定的角色");
        SqlQuery<Permission> permissionSqlQuery = SqlQuery.from(permissionRelation, false);
        permissionSqlQuery.column(permissionRelation.getColumn(IdEntity.ID_COLUMN_NAME))
                .in(permissionRelation.getColumn(IdEntity.ID_COLUMN_NAME),
                        SqlQuery.from(rolePermissionRelation, false)
                                .column(rolePermissionRelation.getColumn("permission_id"))
                                .equal(rolePermissionRelation.getColumn("roleId"), roleId)
                );
        List<String> oldPermission = commonMapper.selectByQuery(permissionSqlQuery)
                .stream()
                .map(BaseEntity::getId)
                .collect(Collectors.toList());
        //添加新的权限
        for (String p : permission) {
            if (!oldPermission.contains(p)) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setPermissionId(p);
                rolePermission.setRoleId(roleId);
                commonMapper.insert(rolePermission);
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long removeRolePermission(String roleId, String... permissionIds) {
        return commonMapper.deleteByQuery(
                SqlQuery.from(rolePermissionRelation)
                        .equal(rolePermissionRelation.getColumn("roleId"), roleId)
                        .in(rolePermissionRelation.getColumn("permission_id"), permissionIds)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addMenuToUser(String userId, String... menuIds) {
        return addPermissionToUser(userId, menuIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long removeUserMenu(String userId, String... menuIds) {
        return removeUserPermission(userId, menuIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addMenuToRole(String roleId, String... menuIds) {
        return addPermissionToRole(roleId, menuIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long removeRoleMenu(String roleId, String... menuIds) {
        return removeRolePermission(roleId, menuIds);
    }

    /**
     * ==============================================================================================
     * 下面全是添加数据的方法
     * ==============================================================================================
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role addRole(Role role) {
        return addSecurity(role);
    }

    @Override
    public long updateRole(Role role) {
        //先查出来数据
        Role old = commonMapper.selectById(Role.class, role.getId());
        Assert.notNull(old, "未查询到指定的数据");
        if (!StringUtils.isEmpty(role.getCode())) {
            Assert.isTrue(role.getCode().equals(old.getCode()), "角色编码不能修改");
        }
        //在更新数据
        commonMapper.updateIgnoreNullByQuery(SqlQuery.from(roleRelation)
                .set(roleRelation.getColumn("name"), role.getName())
                .set(roleRelation.getColumn("description"), role.getDescription())
                .set(roleRelation.getColumn("security_type"), role.getType())
                .set(roleRelation.getColumn("isSys"), role.isSys())
                .set(roleRelation.getColumn("order_info"), role.getOrder())
                .set(roleRelation.getColumn(StatusEntity.STATUS_COLUMN_KEY), role.getStatus())
                .equal(roleRelation.getColumn(Role.ID_COLUMN_NAME), old.getId())
        );
        return 0;
    }

    @Override
    public long deleteRole(String... roleCode) {
        Assert.isTrue(roleCode.length > 0, "角色编码不能为空！");
        List<String> roleIds = commonMapper.selectByQuery(
                SqlQuery.from(roleRelation)
                        .in(roleRelation.getColumn("security_code"), roleCode)
        ).stream()
                .map(o -> ((Role) o).getId())
                .collect(Collectors.toList());
        int count = 0;
        //删除角色
        count += commonMapper.deleteByQuery(SqlQuery.from(roleRelation).
                in(roleRelation.getColumn(IdEntity.ID_COLUMN_NAME), roleIds)
        );
        //删除角色权限关联
        count += commonMapper.deleteByQuery(SqlQuery.from(rolePermissionRelation).
                in(rolePermissionRelation.getColumn("roleId"), roleIds)
        );
        //删除角色人员关联
        count += commonMapper.deleteByQuery(SqlQuery.from(rolePersonRelation).
                in(rolePersonRelation.getColumn("roleId"), roleCode)
        );
        //删除角色人员组关联
        count += commonMapper.deleteByQuery(SqlQuery.from(roleGroupRelation).
                in(roleGroupRelation.getColumn("roleId"), roleCode)
        );
        return count;
    }

    @Override
    public List<Role> selectRoleList(RoleQuery query) {
        return commonMapper.selectByQuery(roleQueryToSqlQuery(query));
    }

    @Override
    public Page<Role> selectRolePage(RoleQuery query, int start, int end) {
        return commonMapper.selectPageByQuery(roleQueryToSqlQuery(query), start, end);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Permission addPermission(Permission permission) {
        return addSecurity(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long updatePermission(Permission permission) {
        //先查出来数据
        Permission old = commonMapper.selectById(Permission.class, permission.getId());
        Assert.notNull(old, "未查询到指定的数据");
        if (!StringUtils.isEmpty(permission.getCode())) {
            Assert.isTrue(permission.getCode().equals(old.getCode()), "角色编码不能修改");
        }
        //在更新数据
        commonMapper.updateIgnoreNullByQuery(SqlQuery.from(permissionRelation)
                .set(permissionRelation.getColumn("name"), permission.getName())
                .set(permissionRelation.getColumn("description"), permission.getDescription())
                .set(permissionRelation.getColumn("security_type"), permission.getType())
                .set(permissionRelation.getColumn("isSys"), permission.isSys())
                .set(permissionRelation.getColumn("order_info"), permission.getOrder())
                .set(permissionRelation.getColumn(StatusEntity.STATUS_COLUMN_KEY), permission.getStatus())
                .equal(permissionRelation.getColumn(Permission.ID_COLUMN_NAME), old.getId())
        );
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long deletePermission(String... permissionCode) {
        Assert.isTrue(permissionCode.length > 0, "权限编码不能为空！");
        List<String> pids = commonMapper.selectByQuery(SqlQuery.from(permissionRelation)
                .in(permissionRelation.getColumn("security_code"), permissionCode)
        ).stream()
                .map(o -> ((Permission) o).getId())
                .collect(Collectors.toList());
        int count = 0;
        //删除权限
        count += commonMapper.deleteByQuery(SqlQuery.from(permissionRelation)
                .in(permissionRelation.getColumn(IdEntity.ID_COLUMN_NAME), pids)
        );
        //删除权限角色关联
        count += commonMapper.deleteByQuery(SqlQuery.from(rolePermissionRelation).
                in(rolePermissionRelation.getColumn("permission_id"), pids)
        );
        //删除权限关联表
        count += commonMapper.deleteByQuery(SqlQuery.from(permissionRelationRelation).
                in(permissionRelationRelation.getColumn("permission_id"), pids)
        );
        return count;
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<Permission> selectPermissionList(PermissionQuery query) {
        return commonMapper.selectByQuery(permissionQueryToSqlQuery(query));
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Page<Permission> selectPermissionPage(PermissionQuery query, int start, int end) {
        return commonMapper.selectPageByQuery(permissionQueryToSqlQuery(query), start, end);
    }

    @Transactional(rollbackFor = Exception.class)
    <T extends IdEntity> T addSecurity(T role) {
        EntityRelation relation;
        String code;
        if (role instanceof Role) {
            relation = roleRelation;
            code = ((Role) role).getCode();
            if (!StringUtils.isEmpty(((Role) role).getStatus())) {
                ((Role) role).setStatus(StatusEntity.STATUS_ENABLE);
            }
        } else {
            relation = permissionRelation;
            code = ((Permission) role).getCode();
            if (!StringUtils.isEmpty(((Permission) role).getStatus())) {
                ((Permission) role).setStatus(StatusEntity.STATUS_ENABLE);
            }
        }
        //判断指定的编码是否存在
        Assert.isTrue(!StringUtils.isEmpty(code), "权限编码不能为空");
        Assert.isTrue(!commonMapper.existsByQuery(SqlQuery.from(relation)
                .equal(relation.getColumn("security_code"), code)
        ), "已存在指定的权限编码");
        commonService.insertIfNotExist(role);
        return role;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysMenu addMenu(SysMenu sysMenu) {
        if (StringUtils.isEmpty(sysMenu.getStatus())) {
            sysMenu.setStatus(StatusEntity.STATUS_ENABLE_STR);
        }
        if (StringUtils.isEmpty(sysMenu.getSysId())) {
            sysMenu.setSysId(SubSystem.DEFAULT_SYSTEM_ID);
        }
        commonService.insertIfNotExist(sysMenu);
        return sysMenu;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long updateMenu(SysMenu sysMenu) {
        return commonService.update(sysMenu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long deleteMenu(String... ids) {
        Assert.isTrue(ids.length > 0, "菜单id不能为空！");
        int count = 0;
        //删除菜单
        commonMapper.deleteByQuery(SqlQuery.from(sysMenuRelation).
                in(sysMenuRelation.getColumn(IdEntity.ID_COLUMN_NAME), ids)
        );
        //删除权限角色关联
        count += commonMapper.deleteByQuery(SqlQuery.from(rolePermissionRelation).
                in(rolePermissionRelation.getColumn("permission_id"), ids)
        );
        //删除权限关联表
        count += commonMapper.deleteByQuery(SqlQuery.from(permissionRelationRelation).
                in(permissionRelationRelation.getColumn("permission_id"), ids)
        );
        return count;
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<SysMenu> selectMenuList(SysMenuQuery query) {
        return commonService.selectList(sysMenuQueryToSqlQuery(query));
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Page<SysMenu> selectMenuPage(SysMenuQuery query, int start, int end) {
        return commonService.selectPage(sysMenuQueryToSqlQuery(query), start, end);
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<TreeNode> menuTree(String sysId, String personId, boolean all) {
        SysMenuQuery query = new SysMenuQuery.Builder().sysIdEqual(sysId).build();
        List<SysMenu> sysMenus = selectMenuList(query);
        if (!all) {
            sysMenus = sysMenus
                    .stream()
                    .filter(s -> hasPermission(personId, s.getUrl()))
                    .collect(Collectors.toList());
        }
        return CommonService.listToTree(sysMenus, sysId, SysMenu::getName, SysMenu::getLeaf, !all);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubSystem addSubSys(SubSystem subSystem) {
        if (StringUtils.isEmpty(subSystem.getStatus())) {
            subSystem.setStatus(StatusEntity.STATUS_ENABLE_STR);
        }
        commonService.insertIfNotExist(subSystem);
        return subSystem;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long updateSubSys(SubSystem subSystem) {
        return commonService.update(subSystem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long deleteSubSys(String id) {
        Assert.isTrue(!StringUtils.isEmpty(id), "子系统id不能为空！");
        int count = 0;
        //删除子系统
        count += commonMapper.deleteById(SubSystem.class, id);
        //删除子系统对应的菜单
        List<SysMenu> sysMenus = selectMenuList(new SysMenuQuery.Builder().sysIdEqual(id).build());
        deleteMenu(sysMenus.stream()
                .map(BaseEntity::getId)
                .collect(Collectors.toList())
                .toArray(new String[sysMenus.size()])
        );
        return count;
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<SubSystem> selectSubSysList(SubSysQuery query) {
        return commonMapper.selectByQuery(subSysQueryToSqlQuery(query));
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Page<SubSystem> selectSubSysPage(SubSysQuery query, int start, int end) {
        return commonMapper.selectPageByQuery(subSysQueryToSqlQuery(query), start, end);
    }

    /**
     * 获取角色列表，根据用户ID标注哪些角色已授权给该用户
     *
     * @param userId
     * @return
     */
    @Override
    public Map<String, Object> getRoleList(String userId) {
        List<Role> roles = selectRoleList(new RoleQuery.Builder().build());
        List<Role> roleList = userRoles(userId);
        List<PermissionExistVo> list = new ArrayList<>();
        for (Role role : roleList) {
            list.add(addRole(role, true));
        }
        for (Role role : roles) {
            boolean flag = true;
            for (Role r : roleList) {
                if (r.getId().equals(role.getId())) {
                    flag = false;
                }
            }
            if (flag) {
                list.add(addRole(role, false));
            }
        }
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("id", "99999");
        valueMap.put("name", "所有角色");
        valueMap.put("children", list);
        valueMap.put("disabled", true);
        return valueMap;
    }

    private PermissionExistVo addRole(Role role, Boolean exist) {
        PermissionExistVo permissionExistVo = new PermissionExistVo();
        permissionExistVo.setId(role.getId());
        permissionExistVo.setName(role.getName());
        permissionExistVo.setDescription(role.getDescription());
        permissionExistVo.setExist(exist);
        permissionExistVo.setType("role");
        return permissionExistVo;
    }

    /**
     * 获取权限列表和菜单列表，根据角色ID标注哪些权限已授权给该角色
     *
     * @param sysMenus
     * @return
     */
    @Override
    public List<TreeNode> getPermissionMenuList(Person person, SysMenu sysMenus) {
        List<PermissionExistVo> list = new ArrayList<>();
        List<TreeNode> aDefault = null;
        if ("menu".equals(sysMenus.getName())) {
            aDefault = menuTree("default", person.getId(), true);
            List<SysMenu> sysMenusNotInRole = selectMenuList(new SysMenuQuery.Builder().build());
            List<SysMenu> sysMenusInRole = selectMenuList(new SysMenuQuery.Builder().roleIdIn(sysMenus.getId()).build());
            for (SysMenu sysMenu : sysMenusInRole) {
                list.add(addSysMenu(sysMenu, true));
            }
            for (TreeNode treeNode : aDefault) {
                addTreeNode(sysMenusInRole, treeNode);
            }
            for (SysMenu sysMenu : sysMenusNotInRole) {
                boolean flag = true;
                for (SysMenu menu : sysMenusInRole) {
                    if (menu.getId().equals(sysMenu.getId())) {
                        flag = false;
                    }
                }
                if (flag) {
                    list.add(addSysMenu(sysMenu, false));
                }
            }

        }
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("id", "99999");
        valueMap.put("name", "所有权限");
        valueMap.put("children", aDefault);
        valueMap.put("disabled", true);
        return aDefault;
    }

    @Override
    public List<TreeNode> getPermissionMenuListOne(Person person, String roleId, String type) {
        List<PermissionExistVo> list = new ArrayList<>();
        List<TreeNode> aDefault = null;
        if ("menu".equals(type)) {
            aDefault = menuTree("defaultone", person.getId(), true);
            List<SysMenu> sysMenusNotInRole = selectMenuList(new SysMenuQuery.Builder().build());
            List<SysMenu> sysMenusInRole = selectMenuList(new SysMenuQuery.Builder().roleIdIn(roleId).build());
            for (SysMenu sysMenu : sysMenusInRole) {
                list.add(addSysMenu(sysMenu, true));
            }
            for (TreeNode treeNode : aDefault) {
                addTreeNode(sysMenusInRole, treeNode);
            }
            for (SysMenu sysMenu : sysMenusNotInRole) {
                boolean flag = true;
                for (SysMenu menu : sysMenusInRole) {
                    if (menu.getId().equals(sysMenu.getId())) {
                        flag = false;
                    }
                }
                if (flag) {
                    list.add(addSysMenu(sysMenu, false));
                }
            }
        }
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("id", "99999");
        valueMap.put("name", "所有权限");
        valueMap.put("children", aDefault);
        valueMap.put("disabled", true);
        return aDefault;
    }

    private PermissionExistVo addSysMenu(SysMenu sysMenu, Boolean exist) {
        PermissionExistVo permissionExistVo = new PermissionExistVo();
        permissionExistVo.setExist(exist);
        permissionExistVo.setId(sysMenu.getId());
        permissionExistVo.setName(sysMenu.getName());
        permissionExistVo.setDescription(sysMenu.getDescription());
        permissionExistVo.setType("sysMenu");
        return permissionExistVo;
    }

    private void addTreeNode(List<SysMenu> sysMenusInRole, TreeNode treeNode) {
        List<TreeNode> nodeChildren = treeNode.getChildren();
        if (nodeChildren != null) {
            boolean flag = true;
            for (SysMenu sysMenu : sysMenusInRole) {
                if (sysMenu.getId().equals(treeNode.getId())) {
                    flag = false;
                }
            }
            if (flag) {
                SysMenu data = (SysMenu) treeNode.getData();
                treeNode.setData(addSysMenu(data, false));
            } else {
                SysMenu data = (SysMenu) treeNode.getData();
                treeNode.setData(addSysMenu(data, true));
            }
            for (TreeNode nodeChild : nodeChildren) {
                addTreeNode(sysMenusInRole, nodeChild);
            }
        } else {
            boolean flag = true;
            for (SysMenu sysMenu : sysMenusInRole) {
                if (sysMenu.getId().equals(treeNode.getId())) {
                    flag = false;
                }
            }
            if (flag) {
                SysMenu data = (SysMenu) treeNode.getData();
                treeNode.setData(addSysMenu(data, false));
            } else {
                SysMenu data = (SysMenu) treeNode.getData();
                treeNode.setData(addSysMenu(data, true));
            }
        }
    }

    protected SqlQuery<Role> roleQueryToSqlQuery(RoleQuery query) {
        SqlQuery<Role> sqlQuery = SqlQuery.from(roleRelation);
        checkBuildInQuery(roleRelation, sqlQuery, IdEntity.ID_COLUMN_NAME, query.getIds());
        checkBuildLikeQuery(roleRelation, sqlQuery, "security_code", query.getCodeLike());
        sqlQuery.orderBy(roleRelation.getColumn(Role.ORDER_COLUMN_NAME));
        return sqlQuery;
    }

    protected SqlQuery<Permission> permissionQueryToSqlQuery(PermissionQuery query) {
        SqlQuery<Permission> sqlQuery = SqlQuery.from(permissionRelation);
        sqlQuery.orderBy(permissionRelation.getColumn(Permission.ORDER_COLUMN_NAME));
        return sqlQuery;
    }

    protected SqlQuery<SubSystem> subSysQueryToSqlQuery(SubSysQuery query) {
        SqlQuery<SubSystem> sqlQuery = SqlQuery.from(subSysRelation);
        checkBuildInQuery(subSysRelation, sqlQuery, SubSystem.ID_COLUMN_NAME, query.getIds());
        sqlQuery.orderBy(subSysRelation.getColumn(SubSystem.ORDER_COLUMN_NAME));
        return sqlQuery;
    }

    protected SqlQuery<SysMenu> sysMenuQueryToSqlQuery(SysMenuQuery query) {
        SqlQuery<SysMenu> sysMenuSqlQuery = SqlQuery.from(sysMenuRelation);
        checkBuildInQuery(sysMenuRelation, sysMenuSqlQuery, SysMenu.ID_COLUMN_NAME, query.getIds());
        if (!StringUtils.isEmpty(query.getPersonId())) {
            sysMenuSqlQuery.in(sysMenuRelation.getColumn(SysMenu.ID_COLUMN_NAME),
                    SqlQuery.from(rolePermissionRelation, false)
                            .column(rolePermissionRelation.getColumn("permission_id"))
                            .in(rolePermissionRelation.getColumn("roleId"),
                                    SqlQuery.from(rolePersonRelation, false)
                                            .column(rolePersonRelation.getColumn("roleId"))
                                            .equal(rolePersonRelation.getColumn("personId"), query.getPersonId())
                            )
            );
        }
        if (query.getRoleIdIn() != null && !query.getRoleIdIn().isEmpty()) {
            sysMenuSqlQuery.in(sysMenuRelation.getColumn(SysMenu.ID_COLUMN_NAME),
                    SqlQuery.from(rolePermissionRelation, false)
                            .column(rolePermissionRelation.getColumn("permission_id"))
                            .in(rolePermissionRelation.getColumn("roleId"), query.getRoleIdIn()
                            )
            );
        }
        sysMenuSqlQuery.orderBy(sysMenuRelation.getColumn(SysMenu.ORDER_COLUMN_NAME));
        return sysMenuSqlQuery;
    }

    @Override
    public void afterPropertiesSet() {
        roleRelation = defaultDataBaseService.getTableInfo(Role.class);
        roleGroupRelation = defaultDataBaseService.getTableInfo(RoleGroup.class);
        rolePermissionRelation = defaultDataBaseService.getTableInfo(RolePermission.class);
        rolePersonRelation = defaultDataBaseService.getTableInfo(RolePerson.class);


        permissionRelation = defaultDataBaseService.getTableInfo(Permission.class);
        permissionRelationRelation = defaultDataBaseService.getTableInfo(PermissionRelation.class);

        subSysRelation = defaultDataBaseService.getTableInfo(SubSystem.class);
        sysMenuRelation = defaultDataBaseService.getTableInfo(SysMenu.class);

        personRelation = defaultDataBaseService.getTableInfo(Person.class);
    }

    @Override
    public void initData(DataBaseService dataBaseService) {
        if (dataBaseService.tableExist("SYS_menu", Constants.SYS_MODULE_NAME)) {
            String rootMenuId = SubSystem.DEFAULT_SYSTEM_ID + "main";
            if (!commonMapper.exists(SysMenu.class, rootMenuId)) {
                SysMenu parent = new SysMenu();
                parent.setName("系统管理");
                parent.setParentId(SubSystem.DEFAULT_SYSTEM_ID);
                parent.setLeaf(false);
                parent.setIcon("grid");
                parent.setOrder(1);
                parent.setId(rootMenuId);
                addMenu(parent);
                SysMenu sysMenu = new SysMenu();
                sysMenu.setId("sysMenu");
                sysMenu.setName("菜单管理");
                sysMenu.setParentId(rootMenuId);
                sysMenu.setLeaf(true);
                sysMenu.setOrder(0);
                sysMenu.setIcon("align-justify");
                sysMenu.setUrl("/main/sysMenu");
                addMenu(sysMenu);

                SysMenu organise = new SysMenu();
                organise.setParentId(rootMenuId);
                organise.setLeaf(true);
                organise.setName("机构管理");
                organise.setUrl("/main/organise");
                organise.setOrder(1);
                addMenu(organise);

                SysMenu person = new SysMenu();
                person.setParentId(rootMenuId);
                person.setLeaf(true);
                person.setName("人员管理");
                person.setUrl("/main/person");
                person.setOrder(2);
                addMenu(person);

                addMenuToUser("admin", parent.getId(), sysMenu.getId(), organise.getId(), person.getId());
            }
        }
    }

    @Override
    public int order() {
        return 1;
    }
}
