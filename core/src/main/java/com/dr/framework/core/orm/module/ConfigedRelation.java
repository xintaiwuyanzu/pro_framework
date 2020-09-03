package com.dr.framework.core.orm.module;

import com.dr.framework.core.orm.jdbc.Relation;

/**
 * 表示运行时配置创建的表信息
 */
public class ConfigedRelation extends Relation {
    /**
     * 对应数据库主键
     */
    private String id;

    public ConfigedRelation(boolean isTable) {
        super(isTable);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
