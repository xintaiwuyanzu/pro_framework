package com.dr.framework.sys.entity;

import com.dr.framework.common.entity.BaseStatusEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Index;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.util.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author dr
 */
@Table(name = Constants.SYS_TABLE_PREFIX + "DICT", comment = "字典表", module = Constants.SYS_MODULE_NAME)
public class SysDict extends BaseStatusEntity<Integer> {

    @Column(name = "type_info", nullable = false)
    private String type;
    @JsonProperty("key")
    @Index(unique = true)
    @Column(name = "key_info", nullable = false)
    private String keyInfo;
    @Column(name = "value_info", type = ColumnType.CLOB)
    private String value;
    @Column(name = "description", length = 1000)
    private String description;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return keyInfo;
    }

    public void setKey(String key) {
        this.keyInfo = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
