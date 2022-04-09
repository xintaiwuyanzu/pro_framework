package com.dr.framework.sys.service;

import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.common.entity.StatusEntity;
import com.dr.framework.common.page.Page;
import com.dr.framework.common.service.CacheAbleService;
import com.dr.framework.common.service.DataBaseService;
import com.dr.framework.core.orm.module.EntityRelation;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.security.entity.Role;
import com.dr.framework.core.security.event.SecurityEvent;
import com.dr.framework.core.security.query.RoleQuery;
import com.dr.framework.core.security.service.SecurityManager;
import com.dr.framework.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色相关的service
 *
 * @author dr
 */
@Service
public class RoleService extends CacheAbleService<Role> implements RelationHelper, InitDataService.DataInit {
    public static final String adminRoleId = com.dr.framework.core.util.Constants.DEFAULT + "admin";
    @Lazy
    @Autowired
    SecurityManager securityManager;
    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long insert(Role entity) {
        if (!ObjectUtils.isEmpty(entity.getStatus())) {
            entity.setStatus(StatusEntity.STATUS_ENABLE);
        }
        //判断指定的编码是否存在
        Assert.isTrue(StringUtils.hasText(entity.getCode()), "权限编码不能为空");
        Assert.isTrue(!commonMapper.existsByQuery(SqlQuery.from(getEntityRelation())
                .equal(getEntityRelation().getColumn("security_code"), entity.getCode())
        ), "已存在指定的权限编码");
        eventPublisher.publishEvent(new SecurityEvent<>(entity));
        return super.insert(entity);
    }

    @Override
    public long updateBySqlQuery(SqlQuery<Role> sqlQuery) {
        throw new UnsupportedOperationException("该方法不支持");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long updateById(Role role) {
        //先查出来数据
        Role old = commonMapper.selectById(Role.class, role.getId());
        Assert.notNull(old, "未查询到指定的数据");
        if (StringUtils.hasText(role.getCode())) {
            Assert.isTrue(role.getCode().equals(old.getCode()), "角色编码不能修改");
        }
        EntityRelation roleRelation = getEntityRelation();
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
        cache.evictIfPresent(role.getId());
        eventPublisher.publishEvent(new SecurityEvent<>(role));
        return 1;
    }

    public long deleteRole(String... roleCode) {
        Assert.isTrue(roleCode.length > 0, "角色编码不能为空！");
        EntityRelation roleRelation = getEntityRelation();

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
        count += commonMapper.deleteByQuery(SqlQuery.from(EntityRolePermission.class).
                in(EntityRolePermissionInfo.ROLEID, roleIds)
        );
        //删除角色人员关联
        count += commonMapper.deleteByQuery(SqlQuery.from(EntityRolePerson.class).
                in(EntityRolePersonInfo.ROLEID, roleCode)
        );
        //删除角色人员组关联
        count += commonMapper.deleteByQuery(SqlQuery.from(EntityRoleGroup.class).
                in(EntityRoleGroupInfo.ROLEID, roleCode)
        );
        eventPublisher.publishEvent(new SecurityEvent<>(""));
        return count;
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public List<Role> selectList(RoleQuery query) {
        return selectList(roleQueryToSqlQuery(query));
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public Page<Role> selectPage(RoleQuery query, int start, int end) {
        return selectPage(roleQueryToSqlQuery(query), start, end);
    }

    protected SqlQuery<Role> roleQueryToSqlQuery(RoleQuery query) {
        EntityRelation roleRelation = getEntityRelation();
        SqlQuery<Role> sqlQuery = SqlQuery.from(roleRelation);
        checkBuildInQuery(roleRelation, sqlQuery, IdEntity.ID_COLUMN_NAME, query.getIds());
        checkBuildLikeQuery(roleRelation, sqlQuery, "security_code", query.getCodeLike());
        sqlQuery.orderBy(roleRelation.getColumn(Role.ORDER_COLUMN_NAME));
        return sqlQuery;
    }

    @Override
    protected String getCacheName() {
        return "core.security.role";
    }

    @Override
    public void initData(DataBaseService dataBaseService) {
        //给admin添加超级用户
        if (dataBaseService.tableExist("SYS_role", Constants.SYS_MODULE_NAME)) {
            if (!commonMapper.exists(Role.class, adminRoleId)) {
                //添加超级管理员角色
                Role role = new Role();
                role.setCode("admin");
                role.setDescription("超级管理员");
                role.setName("超级管理员");
                role.setId(adminRoleId);
                role.setSys(true);
                insert(role);
                //给admin用户添加超级管理员角色
                securityManager.addRoleToUser("admin", role.getId());
            }
        }
    }

    @Override
    public int order() {
        return 10;
    }
}
