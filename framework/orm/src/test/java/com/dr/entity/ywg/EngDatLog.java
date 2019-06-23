package com.dr.entity.ywg;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

import java.util.Date;

@Table(name = "ENG_DAT_LOG", comment = "远望谷所有操作日志", module = "yuanwanggu")
public class EngDatLog {
    @Id
    @Column(name = "nDEGID", length = 10, scale = 10, jdbcType = 4, order = 1)
    private Integer nDEGID;
    @Column(name = "szKeyValue", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 2)
    private String szKeyValue;
    @Column(name = "nInstType", length = 10, scale = 10, jdbcType = 4, order = 3)
    private int nInstType;
    @Column(name = "dtOperate", type = ColumnType.DATE, length = 3, scale = 10, jdbcType = 93, order = 4)
    private Date dtOperate;
    @Column(name = "szDBType", type = ColumnType.VARCHAR, length = 10, scale = 10, jdbcType = 12, order = 5)
    private String szDBType;
    @Column(name = "szBusiness", type = ColumnType.VARCHAR, length = 10, scale = 10, jdbcType = 12, order = 6)
    private String szBusiness;
    @Column(name = "staffname", type = ColumnType.VARCHAR, length = 20, scale = 10, jdbcType = 12, order = 7)
    private String staffname;
    @Column(name = "szSysName", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 8)
    private String szSysName;
    @Column(name = "szClientName", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 9)
    private String szClientName;
    @Column(name = "szDescription", type = ColumnType.VARCHAR, length = 100, scale = 10, jdbcType = 12, order = 10)
    private String szDescription;
    @Column(name = "bOffLine", type = ColumnType.BOOLEAN, length = 1, scale = 10, jdbcType = -7, order = 11)
    private boolean bOffLine;

    public Integer getNDEGID() {
        return nDEGID;
    }

    public void setNDEGID(Integer nDEGID) {
        this.nDEGID = nDEGID;
    }

    public String getSzKeyValue() {
        return szKeyValue;
    }

    public void setSzKeyValue(String szKeyValue) {
        this.szKeyValue = szKeyValue;
    }

    public int getNInstType() {
        return nInstType;
    }

    public void setNInstType(int nInstType) {
        this.nInstType = nInstType;
    }

    public Date getDtOperate() {
        return dtOperate;
    }

    public void setDtOperate(Date dtOperate) {
        this.dtOperate = dtOperate;
    }

    public String getSzDBType() {
        return szDBType;
    }

    public void setSzDBType(String szDBType) {
        this.szDBType = szDBType;
    }

    public String getSzBusiness() {
        return szBusiness;
    }

    public void setSzBusiness(String szBusiness) {
        this.szBusiness = szBusiness;
    }

    public String getStaffname() {
        return staffname;
    }

    public void setStaffname(String staffname) {
        this.staffname = staffname;
    }

    public String getSzSysName() {
        return szSysName;
    }

    public void setSzSysName(String szSysName) {
        this.szSysName = szSysName;
    }

    public String getSzClientName() {
        return szClientName;
    }

    public void setSzClientName(String szClientName) {
        this.szClientName = szClientName;
    }

    public String getSzDescription() {
        return szDescription;
    }

    public void setSzDescription(String szDescription) {
        this.szDescription = szDescription;
    }

    public boolean isBOffLine() {
        return bOffLine;
    }

    public void setBOffLine(boolean bOffLine) {
        this.bOffLine = bOffLine;
    }

}
