package com.dr.framework.common.form.entity;

import com.dr.framework.common.entity.BaseOrderedEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.util.Constants;

/**
 * 表单字段关联表
 *
 * @author dr
 */
@Table(name = "Form_Display_Group", module = Constants.SYS_MODULE_NAME, comment = "表单显示关联")
public class FormDisplayGroup extends BaseOrderedEntity {
    @Column
    private String displayId;
    @Column(comment = "字段定义id（外键）")
    private String fieldId;
    @Column(name = "field_type")
    private String type;

    @Column
    private String parentId;

    /**
     * ========
     * 下面是显示属性
     * ========
     */
    @Column
    private String displayType;
    @Column
    private String selectId;
    @Column
    private String displayWidth;
    @Column
    private String maxWidth;
    @Column
    private String minWidth;
    @Column
    private String displayName;
    @Column
    private String displayComment;
    @Column
    private String defaultValue;

    /**
     * ========
     * TODO 还应该有编辑属性
     * ========
     */

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public String getSelectId() {
        return selectId;
    }

    public void setSelectId(String selectId) {
        this.selectId = selectId;
    }

    public String getDisplayWidth() {
        return displayWidth;
    }

    public void setDisplayWidth(String displayWidth) {
        this.displayWidth = displayWidth;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayComment() {
        return displayComment;
    }

    public void setDisplayComment(String displayComment) {
        this.displayComment = displayComment;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(String maxWidth) {
        this.maxWidth = maxWidth;
    }

    public String getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(String minWidth) {
        this.minWidth = minWidth;
    }

    public String getDisplayId() {
        return displayId;
    }

    public void setDisplayId(String displayId) {
        this.displayId = displayId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
