package com.dr.framework.core.menu.entity;

import com.dr.framework.common.entity.BaseTreeEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.core.util.Constants;

/**
 * 系统菜单
 *
 * @author dr
 */
@Table(name = Constants.SYS_TABLE_PREFIX + "menu"
        , comment = "系统菜单"
        , module = Constants.SYS_MODULE_NAME
        , genInfo = false)
public class SysMenu extends BaseTreeEntity<String> {
    @Column(comment = "图标")
    private String icon;
    @Column(name = "menuName", comment = "显示名称")
    private String name;
    @Column(comment = "url链接")
    private String url;
    @Column(name = "sys_id", comment = "所属子系统")
    private String sysId;
    @Column(comment = "是否父节点")
    private boolean leaf;
    @Column(comment = "权限ID")
    private String permissionId;
    @Column(comment = "描述")
    private String description;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public Boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }
}
