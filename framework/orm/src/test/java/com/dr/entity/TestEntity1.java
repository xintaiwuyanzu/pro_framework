package com.dr.entity;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "databasechangelog111", comment = "测试表", simple = "测试表", module = "test1")
public class TestEntity1 {

    @Column(name = "name2", comment = "姓名", simple = "用户名", order = 1)
    String name;
    @Column(name = "comment1", comment = "姓名", simple = "用户名", order = 1)
    String name3;
    @Column(name = "name1", comment = "姓名", simple = "用户名", order = 2, length = 1001)
    String name1;
    @Id
    @Column(name = "id", comment = "姓名", simple = "用户名")
    String id;
    @Column
    int intCol;
    @Column
    double doubleCol;
    @Column
    long longCol;
    @Column(scale = 5, length = 10)
    float floatCol;
    @Column(type = ColumnType.DATE)
    double dateCol;
    @Column(type = ColumnType.DATE)
    double dateCol1;
    @Column
    boolean booleanCol;
    @Column(type = ColumnType.CLOB, nullable = false)
    String clobCol;
    @Column(type = ColumnType.BLOB)
    String blobCol;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIntCol() {
        return intCol;
    }

    public void setIntCol(int intCol) {
        this.intCol = intCol;
    }

    public double getDoubleCol() {
        return doubleCol;
    }

    public void setDoubleCol(double doubleCol) {
        this.doubleCol = doubleCol;
    }

    public long getLongCol() {
        return longCol;
    }

    public void setLongCol(long longCol) {
        this.longCol = longCol;
    }

    public float getFloatCol() {
        return floatCol;
    }

    public void setFloatCol(float floatCol) {
        this.floatCol = floatCol;
    }

    public double getDateCol() {
        return dateCol;
    }

    public void setDateCol(double dateCol) {
        this.dateCol = dateCol;
    }

    public double getDateCol1() {
        return dateCol1;
    }

    public void setDateCol1(double dateCol1) {
        this.dateCol1 = dateCol1;
    }

    public boolean isBooleanCol() {
        return booleanCol;
    }

    public void setBooleanCol(boolean booleanCol) {
        this.booleanCol = booleanCol;
    }

    public String getClobCol() {
        return clobCol;
    }

    public void setClobCol(String clobCol) {
        this.clobCol = clobCol;
    }

    public String getBlobCol() {
        return blobCol;
    }

    public void setBlobCol(String blobCol) {
        this.blobCol = blobCol;
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }
}
