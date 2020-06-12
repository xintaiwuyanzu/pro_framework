package com.dr.framework.sys.controller;

import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.common.entity.ResultListEntity;
import com.dr.framework.common.entity.TreeNode;
import com.dr.framework.common.page.Page;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.security.entity.SysMenu;
import com.dr.framework.core.security.query.SysMenuQuery;
import com.dr.framework.core.security.service.SecurityManager;
import com.dr.framework.core.web.annotations.Current;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统菜单
 *
 * @author dr
 */
@RestController
@RequestMapping("${common.api-path:/api}/sysmenu")
public class SysMenuController {
    SecurityManager securityManager;

    public SysMenuController(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    /**
     * 加载系统菜单
     *
     * @param sysId  系统id，默认为default
     * @param person 当前登录人员，没登陆会报错
     * @param all    是否加载禁用菜单
     * @return
     */
    @RequestMapping("/menutree")
    public ResultListEntity<TreeNode> menutree(
            @RequestParam(defaultValue = com.dr.framework.core.util.Constants.DEFAULT) String sysId
            , @Current Person person
            , boolean all) {
        return ResultListEntity.success(securityManager.menuTree(sysId, person.getId(), all));
    }

    @RequestMapping("/insert")
    public ResultEntity<SysMenu> insert(SysMenu entity) {
        return ResultEntity.success(securityManager.addMenu(entity));
    }

    @RequestMapping("/update")
    public ResultEntity<SysMenu> update(SysMenu entity) {
        return ResultEntity.success(securityManager.updateMenu(entity));
    }

    @RequestMapping("/delete")
    public ResultEntity<Boolean> delete(SysMenu entity) {
        return ResultEntity.success(securityManager.deleteMenu(entity.getId()));
    }

    @RequestMapping("/detail")
    public ResultEntity<SysMenu> detail(String id, SysMenu entity) {
        List<SysMenu> sysMenus = securityManager.selectMenuList(
                new SysMenuQuery.Builder()
                        .idEqual(entity.getId())
                        .build()
        );
        if (sysMenus != null && sysMenus.size() == 1) {
            return ResultEntity.success(sysMenus.get(0));
        } else {
            return ResultEntity.error("未查询到指定数据");
        }
    }

    @RequestMapping("/page")
    public ResultEntity page(SysMenu entity,
                             @RequestParam(defaultValue = "0") int pageIndex,
                             @RequestParam(defaultValue = Page.DEFAULT_PAGE_SIZE_STR) int pageSize,
                             @RequestParam(defaultValue = "true") boolean page) {
        SysMenuQuery query = new SysMenuQuery.Builder().sysIdEqual(entity.getSysId()).roleIdIn(entity.getId()).build();
        if (page) {
            return ResultEntity.success(securityManager.selectMenuPage(query, pageIndex * pageSize, (pageIndex + 1) * pageSize));
        } else {
            return ResultEntity.success(securityManager.selectMenuList(query));
        }
    }
}
