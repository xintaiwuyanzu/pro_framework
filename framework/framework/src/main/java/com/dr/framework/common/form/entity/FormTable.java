package com.dr.framework.common.form.entity;

import com.dr.framework.common.entity.BaseCreateInfoEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.util.Constants;

/**
 * 自定义表单
 *
 * @author dr
 */
@Table(name = "Form_Table", module = Constants.SYS_MODULE_NAME, comment = "自定义表单")
public class FormTable extends BaseCreateInfoEntity {
    @Column(name = "tableName", comment = "名称")
    private String name;
    @Column(length = 1000)
    private String description;
    @Column(name = "table_type")
    private String type;
    @Column(length = 1000)
    private String tags;
    @Column(name = "table_module")
    private String module;
    //TODO 应该有子表单定义的内容

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
}
