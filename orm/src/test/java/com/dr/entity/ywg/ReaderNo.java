package com.dr.entity.ywg;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

import java.util.Date;

@Table(name = "Reader", comment = "滚筒等机器上的读者", module = "yuanwanggu")
public class ReaderNo {
    @Column(name = "nCardID", type = ColumnType.VARCHAR, length = 256, scale = 10, jdbcType = 12, order = 1)
    private String nCardID;
    @Column(name = "nEPCOrder", type = ColumnType.FLOAT, length = 19, scale = 10, jdbcType = -5, order = 2)
    private long nEPCOrder;
    @Id
    @Column(name = "szReaderID", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 3)
    private String szReaderID;
    @Column(name = "szName", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 4)
    private String szName;
    @Column(name = "bSex", type = ColumnType.BOOLEAN, length = 1, scale = 10, jdbcType = -7, order = 5)
    private boolean bSex;
    @Column(name = "dtBirthday", type = ColumnType.DATE, length = 3, scale = 10, jdbcType = 93, order = 6)
    private Date dtBirthday;
    @Column(name = "szCompany", type = ColumnType.VARCHAR, length = 300, scale = 10, jdbcType = 12, order = 7)
    private String szCompany;
    @Column(name = "szAddress", type = ColumnType.VARCHAR, length = 300, scale = 10, jdbcType = 12, order = 8)
    private String szAddress;
    @Column(name = "szTelephone", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 9)
    private String szTelephone;
    @Column(name = "dtRegisterDate", type = ColumnType.DATE, length = 3, scale = 10, jdbcType = 93, order = 10)
    private Date dtRegisterDate;
    @Column(name = "fDeposit", type = ColumnType.FLOAT, length = 10, scale = 10, jdbcType = 3, order = 11)
    private double fDeposit;
    @Column(name = "fBalance", type = ColumnType.FLOAT, length = 10, scale = 10, jdbcType = 3, order = 12)
    private double fBalance;
    @Column(name = "szCardPswd", type = ColumnType.VARCHAR, length = 20, scale = 10, jdbcType = 12, order = 13)
    private String szCardPswd;
    @Column(name = "nLibraryNo", length = 10, scale = 10, jdbcType = 4, order = 14)
    private int nLibraryNo;
    @Column(name = "szidentityCard", type = ColumnType.VARCHAR, length = 18, scale = 10, jdbcType = 12, order = 15)
    private String szidentityCard;
    @Column(name = "dtlapse", type = ColumnType.DATE, length = 3, scale = 10, jdbcType = 93, order = 16)
    private Date dtlapse;
    @Column(name = "nConvertStaffID", length = 10, scale = 10, jdbcType = 4, order = 17)
    private int nConvertStaffID;
    @Column(name = "dtConvertDate", type = ColumnType.DATE, length = 3, scale = 10, jdbcType = 93, order = 18)
    private Date dtConvertDate;
    @Column(name = "imgPic", type = ColumnType.BLOB, length = 16, scale = 10, jdbcType = -4, order = 19)
    private byte[] imgPic;
    @Column(name = "szFinger", type = ColumnType.VARCHAR, length = 16, scale = 10, jdbcType = -1, order = 20)
    private String szFinger;
    @Column(name = "nCardTypeID", length = 10, scale = 10, jdbcType = 4, order = 21)
    private int nCardTypeID;
    @Column(name = "nCardStatusID", length = 10, scale = 10, jdbcType = 4, order = 22)
    private int nCardStatusID;
    @Column(name = "szEmail", type = ColumnType.VARCHAR, length = 100, scale = 10, jdbcType = 12, order = 23)
    private String szEmail;
    @Column(name = "szMobile", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 24)
    private String szMobile;
    @Column(name = "nCardOrder", type = ColumnType.FLOAT, length = 5, scale = 10, jdbcType = -5, order = 25)
    private long nCardOrder;
    @Column(name = "szRemark", type = ColumnType.VARCHAR, length = 16, scale = 10, jdbcType = -1, order = 26)
    private String szRemark;
    @Column(name = "fOwe", type = ColumnType.FLOAT, length = 10, scale = 10, jdbcType = 3, order = 27)
    private double fOwe;
    @Column(name = "szMajorName", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 28)
    private String szMajorName;
    @Column(name = "szEduLevel", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 29)
    private String szEduLevel;
    @Column(name = "szMajorNameSub", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 30)
    private String szMajorNameSub;
    @Column(name = "szProRanks", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 31)
    private String szProRanks;
    @Column(name = "nCanBorrowNum", type = ColumnType.VARCHAR, length = 50)
    private String nCanBorrowNum;

    public String getNCardID() {
        return nCardID;
    }

    public void setNCardID(String nCardID) {
        this.nCardID = nCardID;
    }

    public long getNEPCOrder() {
        return nEPCOrder;
    }

    public void setNEPCOrder(long nEPCOrder) {
        this.nEPCOrder = nEPCOrder;
    }

    public String getSzReaderID() {
        return szReaderID;
    }

    public void setSzReaderID(String szReaderID) {
        this.szReaderID = szReaderID;
    }

    public String getSzName() {
        return szName;
    }

    public void setSzName(String szName) {
        this.szName = szName;
    }

    public boolean isBSex() {
        return bSex;
    }

    public void setBSex(boolean bSex) {
        this.bSex = bSex;
    }

    public Date getDtBirthday() {
        return dtBirthday;
    }

    public void setDtBirthday(Date dtBirthday) {
        this.dtBirthday = dtBirthday;
    }

    public String getSzCompany() {
        return szCompany;
    }

    public void setSzCompany(String szCompany) {
        this.szCompany = szCompany;
    }

    public String getSzAddress() {
        return szAddress;
    }

    public void setSzAddress(String szAddress) {
        this.szAddress = szAddress;
    }

    public String getSzTelephone() {
        return szTelephone;
    }

    public void setSzTelephone(String szTelephone) {
        this.szTelephone = szTelephone;
    }

    public Date getDtRegisterDate() {
        return dtRegisterDate;
    }

    public void setDtRegisterDate(Date dtRegisterDate) {
        this.dtRegisterDate = dtRegisterDate;
    }

    public double getFDeposit() {
        return fDeposit;
    }

    public void setFDeposit(double fDeposit) {
        this.fDeposit = fDeposit;
    }

    public double getFBalance() {
        return fBalance;
    }

    public void setFBalance(double fBalance) {
        this.fBalance = fBalance;
    }

    public String getSzCardPswd() {
        return szCardPswd;
    }

    public void setSzCardPswd(String szCardPswd) {
        this.szCardPswd = szCardPswd;
    }

    public int getNLibraryNo() {
        return nLibraryNo;
    }

    public void setNLibraryNo(int nLibraryNo) {
        this.nLibraryNo = nLibraryNo;
    }

    public String getSzidentityCard() {
        return szidentityCard;
    }

    public void setSzidentityCard(String szidentityCard) {
        this.szidentityCard = szidentityCard;
    }

    public Date getDtlapse() {
        return dtlapse;
    }

    public void setDtlapse(Date dtlapse) {
        this.dtlapse = dtlapse;
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

    public byte[] getImgPic() {
        return imgPic;
    }

    public void setImgPic(byte[] imgPic) {
        this.imgPic = imgPic;
    }

    public String getSzFinger() {
        return szFinger;
    }

    public void setSzFinger(String szFinger) {
        this.szFinger = szFinger;
    }

    public int getNCardTypeID() {
        return nCardTypeID;
    }

    public void setNCardTypeID(int nCardTypeID) {
        this.nCardTypeID = nCardTypeID;
    }

    public int getNCardStatusID() {
        return nCardStatusID;
    }

    public void setNCardStatusID(int nCardStatusID) {
        this.nCardStatusID = nCardStatusID;
    }

    public String getSzEmail() {
        return szEmail;
    }

    public void setSzEmail(String szEmail) {
        this.szEmail = szEmail;
    }

    public String getSzMobile() {
        return szMobile;
    }

    public void setSzMobile(String szMobile) {
        this.szMobile = szMobile;
    }

    public long getNCardOrder() {
        return nCardOrder;
    }

    public void setNCardOrder(long nCardOrder) {
        this.nCardOrder = nCardOrder;
    }

    public String getSzRemark() {
        return szRemark;
    }

    public void setSzRemark(String szRemark) {
        this.szRemark = szRemark;
    }

    public double getFOwe() {
        return fOwe;
    }

    public void setFOwe(double fOwe) {
        this.fOwe = fOwe;
    }

    public String getSzMajorName() {
        return szMajorName;
    }

    public void setSzMajorName(String szMajorName) {
        this.szMajorName = szMajorName;
    }

    public String getSzEduLevel() {
        return szEduLevel;
    }

    public void setSzEduLevel(String szEduLevel) {
        this.szEduLevel = szEduLevel;
    }

    public String getSzMajorNameSub() {
        return szMajorNameSub;
    }

    public void setSzMajorNameSub(String szMajorNameSub) {
        this.szMajorNameSub = szMajorNameSub;
    }

    public String getSzProRanks() {
        return szProRanks;
    }

    public void setSzProRanks(String szProRanks) {
        this.szProRanks = szProRanks;
    }

    public String getnCanBorrowNum() {
        return nCanBorrowNum;
    }

    public void setnCanBorrowNum(String nCanBorrowNum) {
        this.nCanBorrowNum = nCanBorrowNum;
    }
}
