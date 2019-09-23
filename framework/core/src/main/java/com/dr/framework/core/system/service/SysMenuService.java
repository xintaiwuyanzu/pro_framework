package com.dr.framework.core.system.service;

import com.dr.framework.common.entity.TreeNode;

import java.util.List;

/**
 * 系统菜单相关的service
 *
 * @author dr
 */
public interface SysMenuService {
    /**
     * 查询菜单树
     *
     * @param sysId    系统号
     * @param personId 人员id  人员id为空则返回空数据
     * @param all      是否查询所有的菜单，包括禁用的菜单
     * @return
     */
    List<TreeNode> menuTree(String sysId, String personId, boolean all);

}
