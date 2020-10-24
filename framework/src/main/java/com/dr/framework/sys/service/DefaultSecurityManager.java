package com.dr.framework.sys.service;


import com.dr.framework.common.dao.CommonMapper;
import com.dr.framework.common.entity.BaseEntity;
import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.common.service.CommonService;
import com.dr.framework.common.service.DefaultDataBaseService;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.organise.query.PersonQuery;
import com.dr.framework.core.organise.service.OrganisePersonService;
import com.dr.framework.core.orm.module.EntityRelation;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.security.bo.PermissionHolder;
import com.dr.framework.core.security.entity.Permission;
import com.dr.framework.core.security.entity.Role;
import com.dr.framework.core.security.query.RoleQuery;
import com.dr.framework.core.security.service.SecurityManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 默认权限相关的service
 * TODO 加缓存
 * TODO 权限，人员组关联
 * TODO 继承权限 rbac1
 * TODO 互斥权限 rbac2
 * TODO 日志
 *
 * @author dr
 */
@Service
public class DefaultSecurityManager extends ApplicationObjectSupport implements RelationHelper, SecurityManager, InitializingBean {
    @Autowired
    CommonMapper commonMapper;
    @Autowired
    CommonService commonService;
    @Autowired
    DefaultDataBaseService defaultDataBaseService;
    @Autowired
    RoleService roleService;

    EntityRelation roleRelation;
    EntityRelation permissionRelation;
    EntityRelation personRelation;

    @Override
    public List<Role> userRoles(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return Collections.EMPTY_LIST;
        }
        //角色人员子查询
        SqlQuery<EntityRolePerson> rolePersonSubQuery = SqlQuery.from(EntityRolePerson.class, false)
                .column(EntityRolePersonInfo.ROLEID)
                .equal(EntityRolePersonInfo.PERSONID, userId);
        //角色查询
        SqlQuery<Role> sqlQuery = SqlQuery.from(Role.class)
                .in(roleRelation.getColumn(IdEntity.ID_COLUMN_NAME), rolePersonSubQuery);
        return roleService.selectList(sqlQuery);
    }

    @Override
    public List<PermissionHolder> userPermissions(String userId, String permissionType, String permissionGroup) {
        checkUser(userId);
        SqlQuery<EntityRolePerson> rolePersonSqlQuery = SqlQuery.from(EntityRolePerson.class, false)
                .column(EntityRolePersonInfo.ROLEID)
                .equal(EntityRolePersonInfo.PERSONID, userId);
        SqlQuery<EntityRolePermission> rolePermissionSqlQuery = SqlQuery.from(EntityRolePermission.class, false)
                .column(EntityRolePermissionInfo.PERMISSIONID)
                .in(EntityRolePermissionInfo.ROLEID, rolePersonSqlQuery);
        SqlQuery<Permission> sqlQuery = SqlQuery.from(Permission.class)
                .in(permissionRelation.getColumn(IdEntity.ID_COLUMN_NAME), rolePermissionSqlQuery)
                .equal(permissionRelation.getColumn("security_type"), permissionType)
                .equal(permissionRelation.getColumn("group"), permissionGroup);
        return commonMapper.selectByQuery(sqlQuery).stream().map(PermissionHolder::new).sorted().collect(Collectors.toList());
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
                                SqlQuery.from(EntityRolePerson.class, false)
                                        .column(EntityRolePersonInfo.PERSONID)
                                        .equal(EntityRolePersonInfo.ROLEID, roleId)
                        )
        ).stream()
                .map(o -> (Person) o)
                .collect(Collectors.toList());
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
    public boolean hasPermission(String userid, String permissionType, String permissionGroup, String permissionCode) {
        Assert.isTrue(!StringUtils.isEmpty(permissionCode), "权限编码不能为空！");
        List<PermissionHolder> permissionHolders = userPermissions(userid, permissionType, permissionGroup);
        for (PermissionHolder permissionHolder : permissionHolders) {
            if (permissionHolder.match(permissionCode)) {
                return true;
            }
        }
        return false;
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
                EntityRolePerson rolePerson = new EntityRolePerson();
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
        SqlQuery sqlQuery = SqlQuery.from(EntityRolePerson.class)
                .equal(EntityRolePersonInfo.PERSONID, userId)
                .in(EntityRolePersonInfo.ROLEID, roleIds);
        //删除用户角色关联
        return commonMapper.deleteByQuery(sqlQuery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPermissionToRole(String roleId, String... permission) {
        List<Role> roles = roleService.selectList(new RoleQuery.Builder().idEqual(roleId).build());
        Assert.isTrue(roles.size() == 1, "未查询到指定的角色");

        SqlQuery<Permission> permissionSqlQuery = SqlQuery.from(permissionRelation, false);
        permissionSqlQuery.column(permissionRelation.getColumn(IdEntity.ID_COLUMN_NAME))
                .in(permissionRelation.getColumn(IdEntity.ID_COLUMN_NAME),
                        SqlQuery.from(EntityRolePermission.class, false)
                                .column(EntityRolePermissionInfo.PERMISSIONID)
                                .equal(EntityRolePermissionInfo.ROLEID, roleId)
                );
        List<String> oldPermission = commonMapper.selectByQuery(permissionSqlQuery)
                .stream()
                .map(BaseEntity::getId)
                .collect(Collectors.toList());
        //添加新的权限
        for (String p : permission) {
            if (!oldPermission.contains(p)) {
                EntityRolePermission rolePermission = new EntityRolePermission();
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
                SqlQuery.from(EntityRolePermission.class)
                        .equal(EntityRolePermissionInfo.ROLEID, roleId)
                        .in(EntityRolePermissionInfo.PERMISSIONID, permissionIds)
        );
    }

    @Override
    public void afterPropertiesSet() {
        roleRelation = defaultDataBaseService.getTableInfo(Role.class);
        permissionRelation = defaultDataBaseService.getTableInfo(Permission.class);
        personRelation = defaultDataBaseService.getTableInfo(Person.class);
    }

    @Autowired
    OrganisePersonService organisePersonService;

    Person checkUser(String userId) {
        Person person = organisePersonService.getPerson(new PersonQuery.Builder().idEqual(userId).build());
        Assert.notNull(person, "未查询到指定的用户");
        return person;
    }
}
