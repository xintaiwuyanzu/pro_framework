package com.dr.entity.ywg;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

import java.util.Date;

@Table(name = "BookLoginHis", comment = "远望谷标签注册历史", module = "yuanwanggu")
public class BookLoginHis {
    @Id
    @Column(name = "nBookLoginID", length = 10, scale = 10, jdbcType = 4, order = 1)
    private Integer nBookLoginID;
    @Column(name = "nLabelID", type = ColumnType.VARCHAR, length = 256, scale = 10, jdbcType = 12, order = 2)
    private String nLabelID;
    @Column(name = "szBookID", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 3)
    private String szBookID;
    @Column(name = "nOldLabelID", type = ColumnType.VARCHAR, length = 256, scale = 10, jdbcType = 12, order = 4)
    private String nOldLabelID;
    @Column(name = "szOldBookID", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 5)
    private String szOldBookID;
    @Column(name = "nModifyType", length = 10, scale = 10, jdbcType = 4, order = 6)
    private int nModifyType;
    @Column(name = "nUpdateStaffID", length = 10, scale = 10, jdbcType = 4, order = 7)
    private int nUpdateStaffID;
    @Column(name = "dtUpdateDate", type = ColumnType.DATE, length = 3, scale = 10, jdbcType = 93, order = 8)
    private Date dtUpdateDate;
    @Column(name = "szTagVersion", comment = "图书标签版本号", type = ColumnType.VARCHAR, length = 10, scale = 10, jdbcType = 12, order = 9)
    private String szTagVersion;
    @Column(name = "szContentIndex", comment = "内容索引", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 10)
    private String szContentIndex;

    public Integer getNBookLoginID() {
        return nBookLoginID;
    }

    public void setNBookLoginID(Integer nBookLoginID) {
        this.nBookLoginID = nBookLoginID;
    }

    public String getNLabelID() {
        return nLabelID;
    }

    public void setNLabelID(String nLabelID) {
        this.nLabelID = nLabelID;
    }

    public String getSzBookID() {
        return szBookID;
    }

    public void setSzBookID(String szBookID) {
        this.szBookID = szBookID;
    }

    public String getNOldLabelID() {
        return nOldLabelID;
    }

    public void setNOldLabelID(String nOldLabelID) {
        this.nOldLabelID = nOldLabelID;
    }

    public String getSzOldBookID() {
        return szOldBookID;
    }

    public void setSzOldBookID(String szOldBookID) {
        this.szOldBookID = szOldBookID;
    }

    public int getNModifyType() {
        return nModifyType;
    }

    public void setNModifyType(int nModifyType) {
        this.nModifyType = nModifyType;
    }

    public int getNUpdateStaffID() {
        return nUpdateStaffID;
    }

    public void setNUpdateStaffID(int nUpdateStaffID) {
        this.nUpdateStaffID = nUpdateStaffID;
    }

    public Date getDtUpdateDate() {
        return dtUpdateDate;
    }

    public void setDtUpdateDate(Date dtUpdateDate) {
        this.dtUpdateDate = dtUpdateDate;
    }

    public String getSzTagVersion() {
        return szTagVersion;
    }

    public void setSzTagVersion(String szTagVersion) {
        this.szTagVersion = szTagVersion;
    }

    public String getSzContentIndex() {
        return szContentIndex;
    }

    public void setSzContentIndex(String szContentIndex) {
        this.szContentIndex = szContentIndex;
    }

}
