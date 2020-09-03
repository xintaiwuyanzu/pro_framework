package com.dr.entity.ywg;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

import java.util.Date;

@Table(name = "BookLogoutHis", comment = "注销标签历史", module = "yuanwanggu")
public class BookLogoutHis {
    @Id
    @Column(name = "nBookLogoutID", length = 10, scale = 10, jdbcType = 4, order = 1)
    private Integer nBookLogoutID;
    @Column(name = "nLabelID", type = ColumnType.VARCHAR, length = 256, scale = 10, jdbcType = 12, order = 2)
    private String nLabelID;
    @Column(name = "nEPCOrder", type = ColumnType.FLOAT, length = 19, scale = 10, jdbcType = -5, order = 3)
    private long nEPCOrder;
    @Column(name = "szBookCaseNo", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 4)
    private String szBookCaseNo;
    @Column(name = "nBooksTypeID", length = 10, scale = 10, jdbcType = 4, order = 5)
    private int nBooksTypeID;
    @Column(name = "nPublishID", length = 10, scale = 10, jdbcType = 4, order = 6)
    private int nPublishID;
    @Column(name = "szBookSSID", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 7)
    private String szBookSSID;
    @Column(name = "szSeriesID", type = ColumnType.VARCHAR, length = 100, scale = 10, jdbcType = 12, order = 8)
    private String szSeriesID;
    @Column(name = "nBookStatus", length = 10, scale = 10, jdbcType = 4, order = 9)
    private int nBookStatus;
    @Column(name = "szBookID", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 10)
    private String szBookID;
    @Column(name = "szISBN", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 11)
    private String szISBN;
    @Column(name = "szName", type = ColumnType.VARCHAR, length = 300, scale = 10, jdbcType = -9, order = 12)
    private String szName;
    @Column(name = "szAuthor", type = ColumnType.VARCHAR, length = 100, scale = 10, jdbcType = 12, order = 13)
    private String szAuthor;
    @Column(name = "fPrice", length = 53, scale = 10, jdbcType = 8, order = 14)
    private double fPrice;
    @Column(name = "dtPublishDate", type = ColumnType.DATE, length = 3, scale = 10, jdbcType = 93, order = 15)
    private Date dtPublishDate;
    @Column(name = "nPages", length = 10, scale = 10, jdbcType = 4, order = 16)
    private int nPages;
    @Column(name = "szBookIndex", type = ColumnType.VARCHAR, length = 30, scale = 10, jdbcType = 12, order = 17)
    private String szBookIndex;
    @Column(name = "szMainWord", type = ColumnType.VARCHAR, length = 30, scale = 10, jdbcType = 12, order = 18)
    private String szMainWord;
    @Column(name = "szClassNO", type = ColumnType.VARCHAR, length = 10, scale = 10, jdbcType = 12, order = 19)
    private String szClassNO;
    @Column(name = "szlibCD", type = ColumnType.VARCHAR, length = 30, scale = 10, jdbcType = 12, order = 20)
    private String szlibCD;
    @Column(name = "szMemo", type = ColumnType.VARCHAR, length = 100, scale = 10, jdbcType = 12, order = 21)
    private String szMemo;
    @Column(name = "nStartYear", length = 10, scale = 10, jdbcType = 4, order = 22)
    private int nStartYear;
    @Column(name = "szPublishyear", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 23)
    private String szPublishyear;
    @Column(name = "nConvertStaffID", length = 10, scale = 10, jdbcType = 4, order = 24)
    private int nConvertStaffID;
    @Column(name = "dtConvertDate", type = ColumnType.DATE, length = 3, scale = 10, jdbcType = 93, order = 25)
    private Date dtConvertDate;
    @Column(name = "nLogoutStaffID", length = 10, scale = 10, jdbcType = 4, order = 26)
    private int nLogoutStaffID;
    @Column(name = "dtLogoutDate", type = ColumnType.DATE, length = 3, scale = 10, jdbcType = 93, order = 27)
    private Date dtLogoutDate;
    @Column(name = "szConvertStaff", type = ColumnType.VARCHAR, length = 20, scale = 10, jdbcType = 12, order = 28)
    private String szConvertStaff;

    public Integer getNBookLogoutID() {
        return nBookLogoutID;
    }

    public void setNBookLogoutID(Integer nBookLogoutID) {
        this.nBookLogoutID = nBookLogoutID;
    }

    public String getNLabelID() {
        return nLabelID;
    }

    public void setNLabelID(String nLabelID) {
        this.nLabelID = nLabelID;
    }

    public long getNEPCOrder() {
        return nEPCOrder;
    }

    public void setNEPCOrder(long nEPCOrder) {
        this.nEPCOrder = nEPCOrder;
    }

    public String getSzBookCaseNo() {
        return szBookCaseNo;
    }

    public void setSzBookCaseNo(String szBookCaseNo) {
        this.szBookCaseNo = szBookCaseNo;
    }

    public int getNBooksTypeID() {
        return nBooksTypeID;
    }

    public void setNBooksTypeID(int nBooksTypeID) {
        this.nBooksTypeID = nBooksTypeID;
    }

    public int getNPublishID() {
        return nPublishID;
    }

    public void setNPublishID(int nPublishID) {
        this.nPublishID = nPublishID;
    }

    public String getSzBookSSID() {
        return szBookSSID;
    }

    public void setSzBookSSID(String szBookSSID) {
        this.szBookSSID = szBookSSID;
    }

    public String getSzSeriesID() {
        return szSeriesID;
    }

    public void setSzSeriesID(String szSeriesID) {
        this.szSeriesID = szSeriesID;
    }

    public int getNBookStatus() {
        return nBookStatus;
    }

    public void setNBookStatus(int nBookStatus) {
        this.nBookStatus = nBookStatus;
    }

    public String getSzBookID() {
        return szBookID;
    }

    public void setSzBookID(String szBookID) {
        this.szBookID = szBookID;
    }

    public String getSzISBN() {
        return szISBN;
    }

    public void setSzISBN(String szISBN) {
        this.szISBN = szISBN;
    }

    public String getSzName() {
        return szName;
    }

    public void setSzName(String szName) {
        this.szName = szName;
    }

    public String getSzAuthor() {
        return szAuthor;
    }

    public void setSzAuthor(String szAuthor) {
        this.szAuthor = szAuthor;
    }

    public double getFPrice() {
        return fPrice;
    }

    public void setFPrice(double fPrice) {
        this.fPrice = fPrice;
    }

    public Date getDtPublishDate() {
        return dtPublishDate;
    }

    public void setDtPublishDate(Date dtPublishDate) {
        this.dtPublishDate = dtPublishDate;
    }

    public int getNPages() {
        return nPages;
    }

    public void setNPages(int nPages) {
        this.nPages = nPages;
    }

    public String getSzBookIndex() {
        return szBookIndex;
    }

    public void setSzBookIndex(String szBookIndex) {
        this.szBookIndex = szBookIndex;
    }

    public String getSzMainWord() {
        return szMainWord;
    }

    public void setSzMainWord(String szMainWord) {
        this.szMainWord = szMainWord;
    }

    public String getSzClassNO() {
        return szClassNO;
    }

    public void setSzClassNO(String szClassNO) {
        this.szClassNO = szClassNO;
    }

    public String getSzlibCD() {
        return szlibCD;
    }

    public void setSzlibCD(String szlibCD) {
        this.szlibCD = szlibCD;
    }

    public String getSzMemo() {
        return szMemo;
    }

    public void setSzMemo(String szMemo) {
        this.szMemo = szMemo;
    }

    public int getNStartYear() {
        return nStartYear;
    }

    public void setNStartYear(int nStartYear) {
        this.nStartYear = nStartYear;
    }

    public String getSzPublishyear() {
        return szPublishyear;
    }

    public void setSzPublishyear(String szPublishyear) {
        this.szPublishyear = szPublishyear;
    }

    public int getNConvertStaffID() {
        return nConvertStaffID;
    }

    public void setNConvertStaffID(int nConvertStaffID) {
        this.nConvertStaffID = nConvertStaffID;
    }

    public Date getDtConvertDate() {
        return dtConvertDate;
    }

    public void setDtConvertDate(Date dtConvertDate) {
        this.dtConvertDate = dtConvertDate;
    }

    public int getNLogoutStaffID() {
        return nLogoutStaffID;
    }

    public void setNLogoutStaffID(int nLogoutStaffID) {
        this.nLogoutStaffID = nLogoutStaffID;
    }

    public Date getDtLogoutDate() {
        return dtLogoutDate;
    }

    public void setDtLogoutDate(Date dtLogoutDate) {
        this.dtLogoutDate = dtLogoutDate;
    }

    public String getSzConvertStaff() {
        return szConvertStaff;
    }

    public void setSzConvertStaff(String szConvertStaff) {
        this.szConvertStaff = szConvertStaff;
    }

}
