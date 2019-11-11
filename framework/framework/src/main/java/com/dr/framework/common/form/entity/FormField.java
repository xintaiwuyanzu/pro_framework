package com.dr.framework.common.form.entity;

import com.dr.framework.common.entity.BaseOrderedEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.util.Constants;

/**
 * @author dr
 */
@Table(name = "Form_Field", module = Constants.SYS_MODULE_NAME, comment = "表单字段")
public class FormField extends BaseOrderedEntity {
    @Column
    private String tableId;
    /**
     * 为了减少数据量，字段id应该设置成表单名称_字段名称_字段排序
     */
    @Column(name = "Field_Name")
    private String name;

    public static final String FIELD_DATA_TYPE_VARCHAR = "varchar";
    public static final String FIELD_DATA_TYPE_LONG = "LONG";
    public static final String FIELD_DATA_TYPE_BOOLEAN = "BOOLEAN";
    public static final String FIELD_DATA_TYPE_FLOAT = "FLOAT";
    public static final String FIELD_DATA_TYPE_CLOB = "CLOB";
    public static final String FIELD_DATA_TYPE_BLOB = "BLOB";
    public static final String FIELD_DATA_TYPE_DATE = "DATE";
    @Column(comment = "数据类型")
    private String dataType;
    /**
     * 默认值应该能够处理表达式的
     */
    @Column(comment = "默认值")
    private String defaultValue;

    @Column(comment = "数据长度", name = "f_length")
    private long length;
    @Column(comment = "数据精度", name = "f_scale")
    private long scale;
    @Column(comment = "是否建立索引", name = "f_index")
    private boolean index;
    @Column(comment = "声明索引名称")
    private String indexName;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getScale() {
        return scale;
    }

    public void setScale(long scale) {
        this.scale = scale;
    }

    public boolean isIndex() {
        return index;
    }

    public void setIndex(boolean index) {
        this.index = index;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }
}
