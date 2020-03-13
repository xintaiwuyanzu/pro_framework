package com.dr.framework.core.orm.jdbc;

/**
 * 表示列
 *
 * @author dr
 */
public class Column extends com.dr.framework.core.orm.sql.Column {
    /**
     * 列名
     */
    private String name;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 表对象
     */
    private Relation relation;
    /**
     * 长度
     */
    private int size;
    /**
     * 类型，详细参考
     *
     * @see java.sql.Types
     */
    private int type;
    /**
     * 类型名称
     */
    private String typeName;
    /**
     * 列位置
     */
    private int position;
    /**
     * 列精度，小数点后位数
     */
    private int decimalDigits;
    /**
     * 备注
     */
    private String remark;
    /**
     * 默认值
     */
    private String defaultValue;
    /**
     * 是否可为空
     */
    private TrueOrFalse nullAble;
    /**
     * 是否可自增长
     */
    private TrueOrFalse autoIncrement;

    public Column(String table, String name, String alias) {
        super(table, name, alias);
        setTableName(table);
        setName(name);
    }

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

    @Override
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
