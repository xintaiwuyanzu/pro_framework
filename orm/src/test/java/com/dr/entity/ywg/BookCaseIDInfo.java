package com.dr.entity.ywg;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

import java.util.Date;

@Table(name = "BookCaseIDInfo", comment = "BookCaseIDInfo", module = "yuanwanggu")
public class BookCaseIDInfo {
    @Column(name = "nBookCaseID", type = ColumnType.VARCHAR, length = 256, scale = 10, jdbcType = 12, order = 1)
    private String nBookCaseID;
    @Column(name = "nEPCOrder", type = ColumnType.FLOAT, length = 19, scale = 10, jdbcType = -5, order = 2)
    private long nEPCOrder;
    @Column(name = "nLayer", length = 10, scale = 10, jdbcType = 4, order = 3)
    private int nLayer;
    @Column(name = "dtLastOrderCase", type = ColumnType.DATE, length = 3, scale = 10, jdbcType = 93, order = 4)
    private Date dtLastOrderCase;
    @Column(name = "dtLastMatchCase", type = ColumnType.DATE, length = 3, scale = 10, jdbcType = 93, order = 5)
    private Date dtLastMatchCase;
    @Column(name = "dtLastUpdate", type = ColumnType.DATE, length = 3, scale = 10, jdbcType = 93, order = 6)
    private Date dtLastUpdate;
    @Column(name = "nOnNum", length = 10, scale = 10, jdbcType = 4, order = 7)
    private int nOnNum;
    @Column(name = "nWrongNum", length = 10, scale = 10, jdbcType = 4, order = 8)
    private int nWrongNum;
    @Id
    @Column(name = "szBookCaseNo", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 9)
    private String szBookCaseNo;
    @Column(name = "nOrder", length = 10, scale = 10, jdbcType = 4, order = 10)
    private int nOrder;
    @Column(name = "nBookCount", length = 10, scale = 10, jdbcType = 4, order = 11)
    private int nBookCount;
    @Column(name = "nBookCaseInfoID", type = ColumnType.FLOAT, length = 19, scale = 10, jdbcType = -5, order = 12)
    private long nBookCaseInfoID;
    @Column(name = "szFirstBookID", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 13)
    private String szFirstBookID;
    @Column(name = "nID", length = 10, scale = 10, jdbcType = 4, order = 14)
    private int nID;
    @Column(name = "szPretendIndexNum", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 15)
    private String szPretendIndexNum;
    @Column(name = "szCaseNoTrans", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 16)
    private String szCaseNoTrans;
    @Column(name = "szlibCD", type = ColumnType.VARCHAR, length = 10, scale = 10, jdbcType = 12, order = 17)
    private String szlibCD;
    @Column(name = "szBookIndex", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 18)
    private String szBookIndex;
    @Column(name = "szLyrBarCode", type = ColumnType.VARCHAR, length = 20, scale = 10, jdbcType = 12, order = 19)
    private String szLyrBarCode;

    public String getNBookCaseID() {
        return nBookCaseID;
    }

    public void setNBookCaseID(String nBookCaseID) {
        this.nBookCaseID = nBookCaseID;
    }

    public long getNEPCOrder() {
        return nEPCOrder;
    }

    public void setNEPCOrder(long nEPCOrder) {
        this.nEPCOrder = nEPCOrder;
    }

    public int getNLayer() {
        return nLayer;
    }

    public void setNLayer(int nLayer) {
        this.nLayer = nLayer;
    }

    public Date getDtLastOrderCase() {
        return dtLastOrderCase;
    }

    public void setDtLastOrderCase(Date dtLastOrderCase) {
        this.dtLastOrderCase = dtLastOrderCase;
    }

    public Date getDtLastMatchCase() {
        return dtLastMatchCase;
    }

    public void setDtLastMatchCase(Date dtLastMatchCase) {
        this.dtLastMatchCase = dtLastMatchCase;
    }

    public Date getDtLastUpdate() {
        return dtLastUpdate;
    }

    public void setDtLastUpdate(Date dtLastUpdate) {
        this.dtLastUpdate = dtLastUpdate;
    }

    public int getNOnNum() {
        return nOnNum;
    }

    public void setNOnNum(int nOnNum) {
        this.nOnNum = nOnNum;
    }

    public int getNWrongNum() {
        return nWrongNum;
    }

    public void setNWrongNum(int nWrongNum) {
        this.nWrongNum = nWrongNum;
    }

    public String getSzBookCaseNo() {
        return szBookCaseNo;
    }

    public void setSzBookCaseNo(String szBookCaseNo) {
        this.szBookCaseNo = szBookCaseNo;
    }

    public int getNOrder() {
        return nOrder;
    }

    public void setNOrder(int nOrder) {
        this.nOrder = nOrder;
    }

    public int getNBookCount() {
        return nBookCount;
    }

    public void setNBookCount(int nBookCount) {
        this.nBookCount = nBookCount;
    }

    public long getNBookCaseInfoID() {
        return nBookCaseInfoID;
    }

    public void setNBookCaseInfoID(long nBookCaseInfoID) {
        this.nBookCaseInfoID = nBookCaseInfoID;
    }

    public String getSzFirstBookID() {
        return szFirstBookID;
    }

    public void setSzFirstBookID(String szFirstBookID) {
        this.szFirstBookID = szFirstBookID;
    }

    public int getNID() {
        return nID;
    }

    public void setNID(int nID) {
        this.nID = nID;
    }

    public String getSzPretendIndexNum() {
        return szPretendIndexNum;
    }

    public void setSzPretendIndexNum(String szPretendIndexNum) {
        this.szPretendIndexNum = szPretendIndexNum;
    }

    public String getSzCaseNoTrans() {
        return szCaseNoTrans;
    }

    public void setSzCaseNoTrans(String szCaseNoTrans) {
        this.szCaseNoTrans = szCaseNoTrans;
    }

    public String getSzlibCD() {
        return szlibCD;
    }

    public void setSzlibCD(String szlibCD) {
        this.szlibCD = szlibCD;
    }

    public String getSzBookIndex() {
        return szBookIndex;
    }

    public void setSzBookIndex(String szBookIndex) {
        this.szBookIndex = szBookIndex;
    }

    public String getSzLyrBarCode() {
        return szLyrBarCode;
    }

    public void setSzLyrBarCode(String szLyrBarCode) {
        this.szLyrBarCode = szLyrBarCode;
    }

}
