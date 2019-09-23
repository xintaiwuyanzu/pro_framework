package com.dr.framework.sys.controller;

import com.dr.framework.common.controller.BaseController;
import com.dr.framework.common.entity.ResultListEntity;
import com.dr.framework.common.entity.TreeNode;
import com.dr.framework.core.menu.entity.SysMenu;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.system.service.SysMenuService;
import com.dr.framework.core.web.annotations.Current;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统菜单
 *
 * @author dr
 */
@RestController
@RequestMapping("${common.api-path:/api}/sysmenu")
public class SysMenuController extends BaseController<SysMenu> {
    @Autowired(required = false)
    SysMenuService sysMenuService;

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
        Assert.notNull(sysMenuService, "系统菜单模块未启用！");
        return ResultListEntity.success(sysMenuService.menuTree(sysId, person.getId(), all));
    }
}
