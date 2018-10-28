package com.dr.framework.sys.controller;

import com.dr.framework.common.controller.BaseController;
import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.common.service.CommonService;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.sys.entity.SubSystem;
import com.dr.framework.sys.entity.SysMenu;
import com.dr.framework.sys.entity.SysMenuInfo;
import com.dr.framework.sys.service.InitDataService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 系统菜单
 */
@RestController
@RequestMapping("/api/sysmenu")
public class SysMenuController extends BaseController<SysMenu> implements InitDataService.DataInit {

    @RequestMapping("/menutree")
    public ResultEntity menutree(boolean all, @RequestParam(defaultValue = "default") String sysId) {
        SqlQuery<SysMenu> sqlQuery = SqlQuery.from(SysMenu.class, true);
        if (!all) {
            sqlQuery.equal(SysMenuInfo.STATUS, "1");
        }
        sqlQuery.equal(SysMenuInfo.SYSID);
        List<SysMenu> sysMenus = commonService.selectList(sqlQuery);
        return ResultEntity.success(CommonService.listToTree(sysMenus, sysId, sysMenu -> sysMenu.getName()));
    }

    @Override
    public void initData() {
        if (!commonService.exists(SysMenu.class, SubSystem.DEFAULT_SYSTEM_ID)) {
            SysMenu parent = new SysMenu();
            parent.setName("系统管理");
            parent.setParentId(SubSystem.DEFAULT_SYSTEM_ID);
            parent.setStatus("1");
            parent.setLeaf(false);
            parent.setIcon("grid");
            parent.setSysId(SubSystem.DEFAULT_SYSTEM_ID);
            parent.setId(UUID.randomUUID().toString());

            SysMenu sysMenu = new SysMenu();
            sysMenu.setName("菜单管理");
            sysMenu.setParentId(parent.getId());
            sysMenu.setStatus("1");
            sysMenu.setLeaf(true);
            sysMenu.setIcon("align-justify");
            sysMenu.setSysId(SubSystem.DEFAULT_SYSTEM_ID);
            sysMenu.setUrl("/main/sysMenu");
            commonService.insertIfNotExist(parent, sysMenu);
        }
    }
}
