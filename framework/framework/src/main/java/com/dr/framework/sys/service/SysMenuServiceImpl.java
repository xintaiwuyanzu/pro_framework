package com.dr.framework.sys.service;

import com.dr.framework.common.entity.StatusEntity;
import com.dr.framework.common.entity.TreeNode;
import com.dr.framework.common.service.CommonService;
import com.dr.framework.common.service.DataBaseService;
import com.dr.framework.common.service.DefaultDataBaseService;
import com.dr.framework.core.menu.entity.SysMenu;
import com.dr.framework.core.orm.module.EntityRelation;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.system.entity.SubSystem;
import com.dr.framework.core.system.service.SysMenuService;
import com.dr.framework.util.Constants;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 系统菜单service实现类
 *
 * @author dr
 */
@Service
public class SysMenuServiceImpl extends CommonService implements SysMenuService, InitDataService.DataInit, InitializingBean {
    EntityRelation sysmenuRelation;
    @Autowired
    DefaultDataBaseService defaultDataBaseService;

    /**
     * TODO 权限判断
     *
     * @param sysId    系统号
     * @param personId 人员id  人员id为空则返回空数据
     * @param all      是否查询所有的菜单，包括禁用的菜单
     * @return
     */
    @Override
    public List<TreeNode> menuTree(String sysId, String personId, boolean all) {
        Assert.notNull(sysmenuRelation, "未启用系统菜单模块！");
        SqlQuery<SysMenu> sqlQuery = SqlQuery.from(SysMenu.class, true);
        if (!all) {
            sqlQuery.equal(
                    sysmenuRelation.getColumn(StatusEntity.STATUS_COLUMN_KEY)
                    , StatusEntity.STATUS_ENABLE
            );
        }
        sqlQuery.equal(
                sysmenuRelation.getColumn("sys_id")
        );
        List<SysMenu> sysMenus = selectList(sqlQuery);
        return listToTree(sysMenus, sysId, SysMenu::getName);
    }

    /**
     * 初始化默认菜单
     *
     * @param dataBaseService
     */
    @Override
    public void initData(DataBaseService dataBaseService) {
        if (dataBaseService.tableExist("SYS_menu", Constants.SYS_MODULE_NAME)) {
            if (!exists(SysMenu.class, SubSystem.DEFAULT_SYSTEM_ID)) {
                SysMenu parent = new SysMenu();
                parent.setName("系统管理");
                parent.setParentId(SubSystem.DEFAULT_SYSTEM_ID);
                parent.setStatus("1");
                parent.setLeaf(false);
                parent.setIcon("grid");
                parent.setSysId(SubSystem.DEFAULT_SYSTEM_ID);
                parent.setId(SubSystem.DEFAULT_SYSTEM_ID + "main");

                SysMenu sysMenu = new SysMenu();
                sysMenu.setId("sysMenu");
                sysMenu.setName("菜单管理");
                sysMenu.setParentId(parent.getId());
                sysMenu.setStatus("1");
                sysMenu.setLeaf(true);
                sysMenu.setIcon("align-justify");
                sysMenu.setSysId(SubSystem.DEFAULT_SYSTEM_ID);
                sysMenu.setUrl("/main/sysMenu");
                insertIfNotExist(parent, sysMenu);
            }
        }
    }

    /**
     * 获取sysmenu表结构
     */
    @Override
    public void afterPropertiesSet() {
        sysmenuRelation = defaultDataBaseService.getTableInfo(SysMenu.class);
    }
}
