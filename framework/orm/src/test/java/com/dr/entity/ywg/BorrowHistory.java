package com.dr.entity.ywg;

import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

import java.util.Date;

@Table(name = "BorrowHistory", comment = "借还记录", module = "yuanwanggu")
public class BorrowHistory implements IdEntity {
    @Id
    @Column(name = "nHistoryID", length = 10, scale = 10, jdbcType = 4, order = 1)
    private Integer nHistoryID;
    @Column(name = "nHisIndexID", length = 10, scale = 10, jdbcType = 4, order = 2)
    private int nHisIndexID;
    @Column(name = "szBookID", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 3)
    private String szBookID;
    @Column(name = "szReaderID", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 4)
    private String szReaderID;
    @Column(name = "bResult", comment = "0:操作失败 1：操作成功", type = ColumnType.BOOLEAN, length = 1, scale = 10, jdbcType = -7, order = 5)
    private boolean bResult;
    @Column(name = "szMemo", comment = "失败原因", type = ColumnType.VARCHAR, length = 200, scale = 10, jdbcType = 12, order = 6)
    private String szMemo;
    @Column(name = "dtNeedBack", length = 3, scale = 10, jdbcType = 93, order = 7)
    private Date dtNeedBack;
    @Column(name = "szBookIndex", type = ColumnType.VARCHAR, length = 30, scale = 10, jdbcType = 12, order = 8)
    private String szBookIndex;
    @Column(name = "bBooking", type = ColumnType.BOOLEAN, length = 1, scale = 10, jdbcType = -7, order = 9)
    private boolean bBooking;
    @Column(name = "szBookName", type = ColumnType.VARCHAR, length = 300, scale = 10, jdbcType = -9, order = 10)
    private String szBookName;
    @Column(name = "szReaderName", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 11)
    private String szReaderName;
    @Column(name = "nBooksTypeID", length = 10, scale = 10, jdbcType = 4, order = 12)
    private int nBooksTypeID;
    @Column(name = "szBooksType", type = ColumnType.VARCHAR, length = 64, scale = 10, jdbcType = 12, order = 13)
    private String szBooksType;
    @Column(name = "szLibCDByType", type = ColumnType.VARCHAR, length = 30, scale = 10, jdbcType = 12, order = 14)
    private String szLibCDByType;
    @Column(name = "nHavePrint", type = ColumnType.FLOAT, length = 5, scale = 10, jdbcType = -5, order = 15)
    private long nHavePrint;
    @Column(name = "szCheckInfo", comment = "分拣信息", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 16)
    private String szCheckInfo;
    @Column(name = "szCardType", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 17)
    private String szCardType;
    private Date dtOperateDate;
    private int nOperateType;

    public int getNHistoryID() {
        return nHistoryID;
    }

    public int getNHisIndexID() {
        return nHisIndexID;
    }

    public void setNHisIndexID(int nHisIndexID) {
        this.nHisIndexID = nHisIndexID;
    }

    public String getSzBookID() {
        return szBookID;
    }

    public void setSzBookID(String szBookID) {
        this.szBookID = szBookID;
    }

    public String getSzReaderID() {
        return szReaderID;
    }

    public void setSzReaderID(String szReaderID) {
        this.szReaderID = szReaderID;
    }

    public boolean isBResult() {
        return bResult;
    }

    public void setBResult(boolean bResult) {
        this.bResult = bResult;
    }

    public String getSzMemo() {
        return szMemo;
    }

    public void setSzMemo(String szMemo) {
        this.szMemo = szMemo;
    }

    public Date getDtNeedBack() {
        return dtNeedBack;
    }

    public void setDtNeedBack(Date dtNeedBack) {
        this.dtNeedBack = dtNeedBack;
    }

    public String getSzBookIndex() {
        return szBookIndex;
    }

    public void setSzBookIndex(String szBookIndex) {
        this.szBookIndex = szBookIndex;
    }

    public boolean isBBooking() {
        return bBooking;
    }

    public void setBBooking(boolean bBooking) {
        this.bBooking = bBooking;
    }

    public String getSzBookName() {
        return szBookName;
    }

    public void setSzBookName(String szBookName) {
        this.szBookName = szBookName;
    }

    public String getSzReaderName() {
        return szReaderName;
    }

    public void setSzReaderName(String szReaderName) {
        this.szReaderName = szReaderName;
    }

    public int getNBooksTypeID() {
        return nBooksTypeID;
    }

    public void setNBooksTypeID(int nBooksTypeID) {
        this.nBooksTypeID = nBooksTypeID;
    }

    public String getSzBooksType() {
        return szBooksType;
    }

    public void setSzBooksType(String szBooksType) {
        this.szBooksType = szBooksType;
    }

    public String getSzLibCDByType() {
        return szLibCDByType;
    }

    public void setSzLibCDByType(String szLibCDByType) {
        this.szLibCDByType = szLibCDByType;
    }

    public long getNHavePrint() {
        return nHavePrint;
    }

    public void setNHavePrint(long nHavePrint) {
        this.nHavePrint = nHavePrint;
    }

    public String getSzCheckInfo() {
        return szCheckInfo;
    }

    public void setSzCheckInfo(String szCheckInfo) {
        this.szCheckInfo = szCheckInfo;
    }

    public String getSzCardType() {
        return szCardType;
    }

    public void setSzCardType(String szCardType) {
        this.szCardType = szCardType;
    }

    public Date getDtOperateDate() {
        return dtOperateDate;
    }

    public void setDtOperateDate(Date dtOperateDate) {
        this.dtOperateDate = dtOperateDate;
    }

    public int getnOperateType() {
        return nOperateType;
    }

    public void setnOperateType(int nOperateType) {
        this.nOperateType = nOperateType;
    }

    @Override
    public String getId() {
        return getNHistoryID() + "";
    }

    @Override
    public void setId(String s) {
        //TODO 不管这个
    }
}
