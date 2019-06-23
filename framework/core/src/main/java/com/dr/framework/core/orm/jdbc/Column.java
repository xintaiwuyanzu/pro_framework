package com.dr.framework.core.orm.jdbc;

/**
 * 表示列
 *
 * @author dr
 */
public class Column {
    private String name;
    private String tableName;
    private Relation relation;
    private int size;
    private int type;
    private int position;
    private int decimalDigits;
    private String remark;
    private String typeName;
    private String defaultValue;
    private TrueOrFalse nullAble;
    private TrueOrFalse autoIncrement;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getDecimalDigits() {
        return decimalDigits;
    }

    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public TrueOrFalse getNullAble() {
        return nullAble;
    }

    public void setNullAble(TrueOrFalse nullAble) {
        this.nullAble = nullAble;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public TrueOrFalse getAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(TrueOrFalse autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Relation getRelation() {
        return relation;
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
    }
}
