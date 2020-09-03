package com.dr.entity.ywg;

import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

import java.util.Date;

@Table(name = "BookInfo", comment = "图书主表信息", module = "yuanwanggu")
public class BookInfo implements IdEntity {
    @Column(name = "nLabelID", type = ColumnType.VARCHAR, length = 256, scale = 10, jdbcType = 12, order = 1)
    private String nLabelID;
    @Column(name = "nEPCOrder", type = ColumnType.FLOAT, length = 19, scale = 10, jdbcType = -5, order = 2)
    private long nEPCOrder;
    @Column(name = "szBookCaseNo", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 3)
    private String szBookCaseNo;
    @Column(name = "nBooksTypeID", length = 10, scale = 10, jdbcType = 4, order = 4)
    private int nBooksTypeID;
    @Column(name = "nPublishID", length = 10, scale = 10, jdbcType = 4, order = 5)
    private int nPublishID;
    private String publishName;
    @Column(name = "szBookSSID", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 6)
    private String szBookSSID;
    @Column(name = "szSeriesID", type = ColumnType.VARCHAR, length = 100, scale = 10, jdbcType = 12, order = 7)
    private String szSeriesID;
    @Column(name = "nBookStatus", length = 10, scale = 10, jdbcType = 4, order = 8)
    private int nBookStatus;
    @Column(name = "szCardID", type = ColumnType.VARCHAR, length = 30, scale = 10, jdbcType = 12, order = 9)
    private String szCardID;
    @Column(name = "dtBorrowDate", type = ColumnType.DATE, scale = 10, jdbcType = 93, order = 10)
    private Date dtBorrowDate;
    @Column(name = "dtNeedBackDate", type = ColumnType.DATE, scale = 10, jdbcType = 93, order = 11)
    private Date dtNeedBackDate;
    @Column(name = "dtLastRead", type = ColumnType.DATE, length = 3, scale = 10, jdbcType = 93, order = 12)
    private Date dtLastRead;
    @Column(name = "nStaffID", length = 10, scale = 10, jdbcType = 4, order = 13)
    private int nStaffID;
    @Id
    @Column(name = "szBookID", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 14)
    private String szBookID;
    @Column(name = "szISBN", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 15)
    private String szISBN;
    @Column(name = "szName", type = ColumnType.VARCHAR, length = 300, scale = 10, jdbcType = -9, order = 16)
    private String szName;
    @Column(name = "szAuthor", type = ColumnType.VARCHAR, length = 100, scale = 10, jdbcType = 12, order = 17)
    private String szAuthor;
    @Column(name = "fPrice", type = ColumnType.FLOAT, length = 10, scale = 10, jdbcType = 3, order = 18)
    private double fPrice;
    @Column(name = "dtPublishDate", type = ColumnType.DATE, length = 3, scale = 10, jdbcType = 93, order = 19)
    private Date dtPublishDate;
    @Column(name = "nPages", length = 10, scale = 10, jdbcType = 4, order = 20)
    private int nPages;
    @Column(name = "szBookIndex", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 21)
    private String szBookIndex;
    @Column(name = "szMainWord", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = -9, order = 22)
    private String szMainWord;
    @Column(name = "szClassNO", type = ColumnType.VARCHAR, length = 10, scale = 10, jdbcType = 12, order = 23)
    private String szClassNO;
    @Column(name = "szlibCD", type = ColumnType.VARCHAR, length = 30, scale = 10, jdbcType = 12, order = 24)
    private String szlibCD;
    @Column(name = "szMemo", type = ColumnType.VARCHAR, length = 100, scale = 10, jdbcType = 12, order = 25)
    private String szMemo;
    @Column(name = "nStartYear", length = 10, scale = 10, jdbcType = 4, order = 26)
    private int nStartYear;
    @Column(name = "szPublishyear", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 27)
    private String szPublishyear;
    @Column(name = "nConvertStaffID", length = 10, scale = 10, jdbcType = 4, order = 28)
    private int nConvertStaffID;
    @Column(name = "dtConvertDate", type = ColumnType.DATE, length = 3, scale = 10, jdbcType = 93, order = 29)
    private Date dtConvertDate;
    @Column(name = "nUpdateStaffID", length = 10, scale = 10, jdbcType = 4, order = 30)
    private int nUpdateStaffID;
    @Column(name = "dtUpdateDate", type = ColumnType.DATE, length = 3, scale = 10, jdbcType = 93, order = 31)
    private Date dtUpdateDate;
    @Column(name = "szMoneyType", type = ColumnType.VARCHAR, length = 10, scale = 10, jdbcType = 12, order = 32)
    private String szMoneyType;
    @Column(name = "szPretendIndexNum", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 33)
    private String szPretendIndexNum;
    @Column(name = "bForceSortCase", type = ColumnType.BOOLEAN, jdbcType = -7, order = 34)
    private boolean bForceSortCase;
    @Column(name = "szConvertStaff", type = ColumnType.VARCHAR, length = 20, scale = 10, jdbcType = 12, order = 35)
    private String szConvertStaff;
    @Column(name = "dtAutoUpdate", type = ColumnType.DATE, length = 3, scale = 10, jdbcType = 93, order = 36)
    private Date dtAutoUpdate;
    @Column(name = "nRenewTime", length = 10, scale = 10, jdbcType = 4, order = 37)
    private int nRenewTime;
    @Column(name = "nBookLenType", comment = "图书长度类型", length = 10, scale = 10, jdbcType = 4, order = 38)
    private int nBookLenType;
    @Column(name = "szBookLen", comment = "图书长度", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 39)
    private String szBookLen;
    @Column(name = "nBookThickness", comment = "图书厚度", length = 10, scale = 10, jdbcType = 4, order = 40)
    private int nBookThickness;
    @Column(name = "nSetInfoCount", comment = "卷册总数", length = 10, scale = 10, jdbcType = 4, order = 41)
    private int nSetInfoCount;
    @Column(name = "nSetInfoOrder", comment = "卷册序号", length = 10, scale = 10, jdbcType = 4, order = 42)
    private int nSetInfoOrder;
    @Column(name = "szTemplibCD", comment = "临时馆藏地", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 43)
    private String szTemplibCD;
    @Column(name = "szMedicalLib", comment = "分馆标识", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 44)
    private String szMedicalLib;
    @Column(name = "bBookLabelChecked", type = ColumnType.BOOLEAN, jdbcType = -7, order = 45)
    private boolean bBookLabelChecked;
    @Column(name = "bLostBookFlag", length = 10, scale = 10, jdbcType = 4, order = 46)
    private int bLostBookFlag;

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

    public String getPublishName() {
        return publishName;
    }

    public void setPublishName(String publishName) {
        this.publishName = publishName;
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

    public String getSzCardID() {
        return szCardID;
    }

    public void setSzCardID(String szCardID) {
        this.szCardID = szCardID;
    }

    public Date getDtBorrowDate() {
        return dtBorrowDate;
    }

    public void setDtBorrowDate(Date dtBorrowDate) {
        this.dtBorrowDate = dtBorrowDate;
    }

    public Date getDtNeedBackDate() {
        return dtNeedBackDate;
    }

    public void setDtNeedBackDate(Date dtNeedBackDate) {
        this.dtNeedBackDate = dtNeedBackDate;
    }

    public Date getDtLastRead() {
        return dtLastRead;
    }

    public void setDtLastRead(Date dtLastRead) {
        this.dtLastRead = dtLastRead;
    }

    public int getNStaffID() {
        return nStaffID;
    }

    public void setNStaffID(int nStaffID) {
        this.nStaffID = nStaffID;
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

    public String getSzMoneyType() {
        return szMoneyType;
    }

    public void setSzMoneyType(String szMoneyType) {
        this.szMoneyType = szMoneyType;
    }

    public String getSzPretendIndexNum() {
        return szPretendIndexNum;
    }

    public void setSzPretendIndexNum(String szPretendIndexNum) {
        this.szPretendIndexNum = szPretendIndexNum;
    }

    public boolean isBForceSortCase() {
        return bForceSortCase;
    }

    public void setBForceSortCase(boolean bForceSortCase) {
        this.bForceSortCase = bForceSortCase;
    }

    public String getSzConvertStaff() {
        return szConvertStaff;
    }

    public void setSzConvertStaff(String szConvertStaff) {
        this.szConvertStaff = szConvertStaff;
    }

    public Date getDtAutoUpdate() {
        return dtAutoUpdate;
    }

    public void setDtAutoUpdate(Date dtAutoUpdate) {
        this.dtAutoUpdate = dtAutoUpdate;
    }

    public int getNRenewTime() {
        return nRenewTime;
    }

    public void setNRenewTime(int nRenewTime) {
        this.nRenewTime = nRenewTime;
    }

    public int getNBookLenType() {
        return nBookLenType;
    }

    public void setNBookLenType(int nBookLenType) {
        this.nBookLenType = nBookLenType;
    }

    public String getSzBookLen() {
        return szBookLen;
    }

    public void setSzBookLen(String szBookLen) {
        this.szBookLen = szBookLen;
    }

    public int getNBookThickness() {
        return nBookThickness;
    }

    public void setNBookThickness(int nBookThickness) {
        this.nBookThickness = nBookThickness;
    }

    public int getNSetInfoCount() {
        return nSetInfoCount;
    }

    public void setNSetInfoCount(int nSetInfoCount) {
        this.nSetInfoCount = nSetInfoCount;
    }

    public int getNSetInfoOrder() {
        return nSetInfoOrder;
    }

    public void setNSetInfoOrder(int nSetInfoOrder) {
        this.nSetInfoOrder = nSetInfoOrder;
    }

    public String getSzTemplibCD() {
        return szTemplibCD;
    }

    public void setSzTemplibCD(String szTemplibCD) {
        this.szTemplibCD = szTemplibCD;
    }

    public String getSzMedicalLib() {
        return szMedicalLib;
    }

    public void setSzMedicalLib(String szMedicalLib) {
        this.szMedicalLib = szMedicalLib;
    }

    public boolean isBBookLabelChecked() {
        return bBookLabelChecked;
    }

    public void setBBookLabelChecked(boolean bBookLabelChecked) {
        this.bBookLabelChecked = bBookLabelChecked;
    }

    public int getBLostBookFlag() {
        return bLostBookFlag;
    }

    public void setBLostBookFlag(int bLostBookFlag) {
        this.bLostBookFlag = bLostBookFlag;
    }

    @Override
    public String getId() {
        return getSzBookID();
    }

    @Override
    public void setId(String id) {
        setSzBookID(id);
    }
}
