package com.dr.framework.sys.service;

import com.dr.framework.common.entity.StatusEntity;
import com.dr.framework.common.page.Page;
import com.dr.framework.common.service.CacheAbleService;
import com.dr.framework.common.service.DataBaseService;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.security.entity.SubSystem;
import com.dr.framework.core.security.entity.SysMenu;
import com.dr.framework.core.security.query.SysMenuQuery;
import com.dr.framework.util.Constants;
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
public class SysMenuService extends CacheAbleService<SysMenu> implements RelationHelper, InitDataService.DataInit {

    public List<SysMenu> selectMenuList(SysMenuQuery query) {
        return selectList(sysMenuQueryToSqlQuery(query));
    }

    public Page<SysMenu> selectMenuPage(SysMenuQuery query, int start, int end) {
        return selectPage(sysMenuQueryToSqlQuery(query), start, end);
    }

    /**
     * 根据主键删除菜单
     * TODO
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public long deleteByIds(String... ids) {
        //删除菜单
        //删除菜单角色关联
        return 0;
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
        sysMenuSqlQuery.orderBy(entityRelation.getColumn(SysMenu.ORDER_COLUMN_NAME));
        return sysMenuSqlQuery;
    }

    @Override
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
                //addMenu(parent);
                SysMenu sysMenu = new SysMenu();
                sysMenu.setId("sysMenu");
                sysMenu.setName("菜单管理");
                sysMenu.setParentId(rootMenuId);
                sysMenu.setLeaf(true);
                sysMenu.setOrder(0);
                sysMenu.setIcon("align-justify");
                sysMenu.setUrl("/main/sysMenu");
                //addMenu(sysMenu);

                SysMenu organise = new SysMenu();
                organise.setParentId(rootMenuId);
                organise.setLeaf(true);
                organise.setName("机构管理");
                organise.setUrl("/main/organise");
                organise.setOrder(1);
                //addMenu(organise);

                SysMenu person = new SysMenu();
                person.setParentId(rootMenuId);
                person.setLeaf(true);
                person.setName("人员管理");
                person.setUrl("/main/person");
                person.setOrder(2);
               // addMenu(person);


                SysMenu role = new SysMenu();
                role.setParentId(rootMenuId);
                role.setLeaf(true);
                role.setName("权限管理");
                role.setUrl("/system/role");
                role.setOrder(3);
               // addMenu(role);

                //addMenuToUser("admin", parent.getId(), sysMenu.getId(), organise.getId(), person.getId(), role.getId());
            }
        }
    }

    @Override
    protected String getCacheName() {
        return "core.security.menu";
    }
}
