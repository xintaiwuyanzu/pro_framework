package com.dr.framework.common.form.entity;

import com.dr.framework.common.entity.BaseCreateInfoEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.util.Constants;

/**
 * 表单显示方案
 *
 * @author dr
 */
@Table(name = "Form_Display", comment = "显示方案", module = Constants.SYS_MODULE_NAME)
public class FormDisplay extends BaseCreateInfoEntity {
    @Column
    private String tableId;
    /**
     * 建表定义
     */
    public static final String FORM_DISPLAY_TYPE_CREATE = "create";
    /**
     * 首页显示定义
     */
    public static final String FORM_DISPLAY_TYPE_INDEX = "index";
    /**
     * 首页搜索表单定义
     */
    public static final String FORM_DISPLAY_TYPE_INDEX_SEARCH = "index_search";
    /**
     * 添加表单定义
     */
    public static final String FORM_DISPLAY_TYPE_FORM_ADD = "form_add";
    /**
     * 编辑表单定义
     */
    public static final String FORM_DISPLAY_TYPE_FORM_EDIT = "form_edit";
    /**
     * 详情表单
     */
    public static final String FORM_DISPLAY_TYPE_FORM_DETAIL = "form_edit";

    @Column
    private String displayType;
    @Column(name = "displayName")
    private String name;
    @Column(length = 500, name = "simple_name")
    private String simple;
    @Column(length = 2000)
    private String description;
    /**
     * 同类型的表单显示方案可以有多个
     */
    @Column(comment = "是否是默认的显示方案")
    private boolean isDefault;

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSimple() {
        return simple;
    }

    public void setSimple(String simple) {
        this.simple = simple;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
