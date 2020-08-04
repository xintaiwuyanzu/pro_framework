package com.dr.framework.common.entity;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.util.Constants;

@Table(name = Constants.COMMON_TABLE_PREFIX + "EXTEND", comment = "通用扩展字段表", module = Constants.COMMON_MODULE_NAME)
public class Extend extends BaseEntity {
    @Column(name = "ref_table", comment = "主表名称", length = 50)
    private String refTable;
    @Column(name = "ref_id", comment = "主表Id", length = 50)
    private String refId;
    @Column(name = "ext_str1", comment = "扩展字段string1")
    private String extStr1;
    @Column(name = "ext_str2", comment = "扩展字段string2", length = 1000)
    private String extStr2;
    @Column(name = "ext_str3", comment = "扩展字段string3", length = 2000)
    private String extStr3;
    @Column(name = "ext_str4", comment = "扩展字段string4", length = 2000)
    private String extStr4;
    @Column(name = "ext_date1", comment = "扩展字段date1", type = ColumnType.DATE)
    private long extDate1;
    @Column(name = "ext_date2", comment = "扩展字段date2", type = ColumnType.DATE)
    private long extDate2;
    @Column(name = "ext_long1", comment = "扩展字段Long1")
    private long extLong1;
    @Column(name = "ext_long2", comment = "扩展字段Long2")
    private long extLong2;
    @Column(name = "ext_long3", comment = "扩展字段Long3")
    private long extLong3;
    @Column(name = "ext_long4", comment = "扩展字段Long4")
    private long extLong4;

    public String getRefTable() {
        return refTable;
    }

    public void setRefTable(String refTable) {
        this.refTable = refTable;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getExtStr1() {
        return extStr1;
    }

    public void setExtStr1(String extStr1) {
        this.extStr1 = extStr1;
    }

    public String getExtStr2() {
        return extStr2;
    }

    public void setExtStr2(String extStr2) {
        this.extStr2 = extStr2;
    }

    public String getExtStr3() {
        return extStr3;
    }

    public void setExtStr3(String extStr3) {
        this.extStr3 = extStr3;
    }

    public String getExtStr4() {
        return extStr4;
    }

    public void setExtStr4(String extStr4) {
        this.extStr4 = extStr4;
    }

    public long getExtDate1() {
        return extDate1;
    }

    public void setExtDate1(long extDate1) {
        this.extDate1 = extDate1;
    }

    public long getExtDate2() {
        return extDate2;
    }

    public void setExtDate2(long extDate2) {
        this.extDate2 = extDate2;
    }

    public long getExtLong1() {
        return extLong1;
    }

    public void setExtLong1(long extLong1) {
        this.extLong1 = extLong1;
    }

    public long getExtLong2() {
        return extLong2;
    }

    public void setExtLong2(long extLong2) {
        this.extLong2 = extLong2;
    }

    public long getExtLong3() {
        return extLong3;
    }

    public void setExtLong3(long extLong3) {
        this.extLong3 = extLong3;
    }

    public long getExtLong4() {
        return extLong4;
    }

    public void setExtLong4(long extLong4) {
        this.extLong4 = extLong4;
    }
}
