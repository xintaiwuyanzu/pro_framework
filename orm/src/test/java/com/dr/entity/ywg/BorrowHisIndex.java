package com.dr.entity.ywg;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

import java.util.Date;

@Table(name = "BorrowHisIndex", comment = "借阅记录索引", module = "yuanwanggu")
public class BorrowHisIndex {
    @Id
    @Column(name = "nHisIndexID", length = 10, scale = 10, jdbcType = 4, order = 1)
    private Integer nHisIndexID;
    @Column(name = "dtOperateDate", length = 3, scale = 10, jdbcType = 93, order = 2)
    private Date dtOperateDate;
    @Column(name = "nOperateType", length = 10, scale = 10, jdbcType = 4, order = 3)
    private int nOperateType;
    @Column(name = "szIPAdd", type = ColumnType.VARCHAR, length = 20, scale = 10, jdbcType = 12, order = 4)
    private String szIPAdd;
    @Column(name = "szMacAdd", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 5)
    private String szMacAdd;
    @Column(name = "szMachineName", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 6)
    private String szMachineName;
    @Column(name = "nStaffID", length = 10, scale = 10, jdbcType = 4, order = 7)
    private int nStaffID;
    @Column(name = "szGlideID", type = ColumnType.VARCHAR, length = 20, scale = 10, jdbcType = 12, order = 8)
    private String szGlideID;
    @Column(name = "nGlideIndex", length = 10, scale = 10, jdbcType = 4, order = 9)
    private int nGlideIndex;
    @Column(name = "nSucCount", length = 10, scale = 10, jdbcType = 4, order = 10)
    private int nSucCount;
    @Column(name = "nFailCount", length = 10, scale = 10, jdbcType = 4, order = 11)
    private int nFailCount;
    @Column(name = "szReaderID", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 12)
    private String szReaderID;
    @Column(name = "szReaderName", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 13)
    private String szReaderName;
    @Column(name = "szReaderPwd", type = ColumnType.VARCHAR, length = 100, scale = 10, jdbcType = 12, order = 14)
    private String szReaderPwd;
    @Column(name = "szInterLendNO", comment = "馆际互借流水号", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 15)
    private String szInterLendNO;
    @Column(name = "szInterLendLib", comment = "馆际互借借入馆", type = ColumnType.VARCHAR, length = 20, scale = 10, jdbcType = 12, order = 16)
    private String szInterLendLib;

    public int getNHisIndexID() {
        return nHisIndexID;
    }

    public Date getDtOperateDate() {
        return dtOperateDate;
    }

    public void setDtOperateDate(Date dtOperateDate) {
        this.dtOperateDate = dtOperateDate;
    }

    public int getNOperateType() {
        return nOperateType;
    }

    public void setNOperateType(int nOperateType) {
        this.nOperateType = nOperateType;
    }

    public String getSzIPAdd() {
        return szIPAdd;
    }

    public void setSzIPAdd(String szIPAdd) {
        this.szIPAdd = szIPAdd;
    }

    public String getSzMacAdd() {
        return szMacAdd;
    }

    public void setSzMacAdd(String szMacAdd) {
        this.szMacAdd = szMacAdd;
    }

    public String getSzMachineName() {
        return szMachineName;
    }

    public void setSzMachineName(String szMachineName) {
        this.szMachineName = szMachineName;
    }

    public int getNStaffID() {
        return nStaffID;
    }

    public void setNStaffID(int nStaffID) {
        this.nStaffID = nStaffID;
    }

    public String getSzGlideID() {
        return szGlideID;
    }

    public void setSzGlideID(String szGlideID) {
        this.szGlideID = szGlideID;
    }

    public int getNGlideIndex() {
        return nGlideIndex;
    }

    public void setNGlideIndex(int nGlideIndex) {
        this.nGlideIndex = nGlideIndex;
    }

    public int getNSucCount() {
        return nSucCount;
    }

    public void setNSucCount(int nSucCount) {
        this.nSucCount = nSucCount;
    }

    public int getNFailCount() {
        return nFailCount;
    }

    public void setNFailCount(int nFailCount) {
        this.nFailCount = nFailCount;
    }

    public String getSzReaderID() {
        return szReaderID;
    }

    public void setSzReaderID(String szReaderID) {
        this.szReaderID = szReaderID;
    }

    public String getSzReaderName() {
        return szReaderName;
    }

    public void setSzReaderName(String szReaderName) {
        this.szReaderName = szReaderName;
    }

    public String getSzReaderPwd() {
        return szReaderPwd;
    }

    public void setSzReaderPwd(String szReaderPwd) {
        this.szReaderPwd = szReaderPwd;
    }

    public String getSzInterLendNO() {
        return szInterLendNO;
    }

    public void setSzInterLendNO(String szInterLendNO) {
        this.szInterLendNO = szInterLendNO;
    }

    public String getSzInterLendLib() {
        return szInterLendLib;
    }

    public void setSzInterLendLib(String szInterLendLib) {
        this.szInterLendLib = szInterLendLib;
    }

}
