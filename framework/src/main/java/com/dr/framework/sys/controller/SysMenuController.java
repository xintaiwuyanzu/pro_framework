package com.dr.framework.sys.controller;

import com.dr.framework.common.controller.BaseServiceController;
import com.dr.framework.common.entity.ResultListEntity;
import com.dr.framework.common.entity.TreeNode;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.security.bo.PermissionResource;
import com.dr.framework.core.security.entity.SysMenu;
import com.dr.framework.core.security.service.ResourceManager;
import com.dr.framework.core.web.annotations.Current;
import com.dr.framework.sys.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 系统菜单
 *
 * @author dr
 */
@RestController
@RequestMapping("${common.api-path:/api}/sysmenu")
public class SysMenuController extends BaseServiceController<SysMenuService, SysMenu> {

    @Autowired
    ResourceManager resourceManager;

    /**
     * 加载系统菜单
     *
     * @param sysId  系统id，默认为default
     * @param person 当前登录人员，没登陆会报错
     * @param all    是否加载禁用菜单
     * @return
     */
    @RequestMapping("/menutree")
    public ResultListEntity<TreeNode<? extends PermissionResource>> menutree(
            @RequestParam(defaultValue = com.dr.framework.core.util.Constants.DEFAULT) String sysId
            , @Current Person person
            , boolean all) {
        List<TreeNode<? extends PermissionResource>> treeNodes;
        if (all) {
            treeNodes = resourceManager.getResourcesTree(SysMenuService.RESOURCE_TYPE, sysId);
        } else {
            treeNodes = resourceManager.getPersonResourceTree(person.getId(), SysMenuService.RESOURCE_TYPE, sysId);
        }
        return ResultListEntity.success(treeNodes);
    }

    @Override
    protected SqlQuery<SysMenu> buildPageQuery(HttpServletRequest request, SysMenu entity) {
        //TODO
        return null;
    }
}
