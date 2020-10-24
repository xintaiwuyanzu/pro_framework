package com.dr.framework.sys.controller;

import com.dr.framework.common.controller.BaseServiceController;
import com.dr.framework.common.entity.ResultListEntity;
import com.dr.framework.common.entity.TreeNode;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.security.entity.SysMenu;
import com.dr.framework.core.web.annotations.Current;
import com.dr.framework.sys.service.SysMenuService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 系统菜单
 *
 * @author dr
 */
@RestController
@RequestMapping("${common.api-path:/api}/sysmenu")
public class SysMenuController extends BaseServiceController<SysMenuService, SysMenu> {

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
        //TODO
        return null;
    }

    @Override
    protected SqlQuery<SysMenu> buildPageQuery(HttpServletRequest request, SysMenu entity) {
        //TODO
        return null;
    }
}
