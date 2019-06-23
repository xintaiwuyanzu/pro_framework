package com.dr.entity;

import com.dr.framework.common.entity.BaseTreeEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;

/**
 * 中图分类法
 */
@Table(name = "BookTypes", comment = "中国图书分类法", module = "tushu")
public class BookTypes extends BaseTreeEntity<String> {
    @Column(name = "label1", comment = "显示名称", length = 200)
    private String label;
    @Column(name = "types", comment = "分类类型", length = 100)
    private String type;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
