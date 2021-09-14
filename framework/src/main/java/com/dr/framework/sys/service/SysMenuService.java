package com.dr.framework.sys.service;

import com.dr.framework.common.entity.StatusEntity;
import com.dr.framework.common.page.Page;
import com.dr.framework.common.service.DataBaseService;
import com.dr.framework.common.service.PermissionResourceService;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.security.bo.PermissionResource;
import com.dr.framework.core.security.entity.SubSystem;
import com.dr.framework.core.security.entity.SysMenu;
import com.dr.framework.core.security.query.SubSysQuery;
import com.dr.framework.core.security.query.SysMenuQuery;
import com.dr.framework.core.security.service.ResourceProvider;
import com.dr.framework.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 菜单增删改查service
 *
 * @author dr
 */
@Service
public class SysMenuService extends PermissionResourceService<SysMenu> implements RelationHelper, ResourceProvider, InitDataService.DataInit {
    /**
     * 资源类型
     */
    public static final String RESOURCE_TYPE = "sysmenu";
    @Autowired
    protected SubSysService subSysService;

    public List<SysMenu> selectMenuList(SysMenuQuery query) {
        return selectList(sysMenuQueryToSqlQuery(query));
    }

    public Page<SysMenu> selectMenuPage(SysMenuQuery query, int start, int end) {
        return selectPage(sysMenuQueryToSqlQuery(query), start, end);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long insert(SysMenu sysMenu) {
        if (StringUtils.isEmpty(sysMenu.getStatus())) {
            sysMenu.setStatus(StatusEntity.STATUS_ENABLE_STR);
        }
        if (StringUtils.isEmpty(sysMenu.getSysId())) {
            sysMenu.setSysId(SubSystem.DEFAULT_SYSTEM_ID);
        }
        return super.insert(sysMenu);
    }

    protected SqlQuery<SysMenu> sysMenuQueryToSqlQuery(SysMenuQuery query) {
        SqlQuery<SysMenu> sysMenuSqlQuery = SqlQuery.from(entityRelation);
        checkBuildInQuery(entityRelation, sysMenuSqlQuery, SysMenu.ID_COLUMN_NAME, query.getIds());
        if (!StringUtils.isEmpty(query.getPersonId())) {
            sysMenuSqlQuery.in(entityRelation.getColumn(SysMenu.ID_COLUMN_NAME),
                    SqlQuery.from(EntityRolePermission.class, false)
                            .column(EntityRolePermissionInfo.PERMISSIONID)
                            .in(EntityRolePermissionInfo.ROLEID,
                                    SqlQuery.from(EntityRolePerson.class, false)
                                            .column(EntityRolePersonInfo.ROLEID)
                                            .equal(EntityRolePersonInfo.PERSONID, query.getPersonId())
                            )
            );
        }
        if (query.getRoleIdIn() != null && !query.getRoleIdIn().isEmpty()) {
            sysMenuSqlQuery.in(entityRelation.getColumn(SysMenu.ID_COLUMN_NAME),
                    SqlQuery.from(EntityRolePermission.class, false)
                            .column(EntityRolePermissionInfo.PERMISSIONID)
                            .in(EntityRolePermissionInfo.ROLEID, query.getRoleIdIn()
                            )
            );
        }
        if (query.isEnable()) {
            sysMenuSqlQuery.equal(entityRelation.getColumn(StatusEntity.STATUS_COLUMN_KEY), StatusEntity.STATUS_ENABLE_STR);
        }
        sysMenuSqlQuery.orderBy(entityRelation.getColumn(SysMenu.ORDER_COLUMN_NAME));
        return sysMenuSqlQuery;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initData(DataBaseService dataBaseService) {
        //初始化系统菜单
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
                insert(parent);
                SysMenu sysMenu = new SysMenu();
                sysMenu.setId("sysMenu");
                sysMenu.setName("菜单管理");
                sysMenu.setParentId(rootMenuId);
                sysMenu.setLeaf(true);
                sysMenu.setOrder(0);
                sysMenu.setIcon("align-justify");
                sysMenu.setUrl("/system/menu");
                insert(sysMenu);

                SysMenu organise = new SysMenu();
                organise.setParentId(rootMenuId);
                organise.setLeaf(true);
                organise.setName("机构管理");
                organise.setUrl("/system/organise");
                organise.setOrder(1);
                insert(organise);
                SysMenu person = new SysMenu();
                person.setParentId(rootMenuId);
                person.setLeaf(true);
                person.setName("人员管理");
                person.setUrl("/system/person");
                person.setOrder(2);
                insert(person);


                SysMenu role = new SysMenu();
                role.setParentId(rootMenuId);
                role.setLeaf(true);
                role.setName("角色管理");
                role.setUrl("/system/role");
                role.setOrder(3);
                insert(role);

                SysMenu permission = new SysMenu();
                permission.setParentId(rootMenuId);
                permission.setLeaf(true);
                permission.setName("权限管理");
                permission.setUrl("/system/permission");
                permission.setOrder(4);
                insert(permission);

                SysMenu sys = new SysMenu();
                sys.setParentId(rootMenuId);
                sys.setLeaf(true);
                sys.setName("子系统管理");
                sys.setUrl("/system/sys");
                sys.setOrder(5);

                SysMenu dict = new SysMenu();
                dict.setParentId(rootMenuId);
                dict.setLeaf(true);
                dict.setName("字典管理");
                dict.setUrl("/system/dict");
                dict.setOrder(6);
                insert(dict);
            }
        }
    }

    @Override
    public int order() {
        return 20;
    }

    @Override
    protected String getCacheName() {
        return "core.security.menu";
    }

    @Override
    public List<? extends PermissionResource> getGroupResource() {
        return subSysService.selectSubSysList(new SubSysQuery.Builder().build());
    }

    @Override
    public List<? extends PermissionResource> getResources(String groupId) {
        return selectMenuList(new SysMenuQuery.Builder().statusEnable().build());
    }

    @Override
    public String getType() {
        return RESOURCE_TYPE;
    }

    @Override
    public String getName() {
        return "系统菜单";
    }
}
