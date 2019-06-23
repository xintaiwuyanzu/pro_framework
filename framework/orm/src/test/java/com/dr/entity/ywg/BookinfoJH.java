package com.dr.entity.ywg;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "BookInfoJH", comment = "图书借还", module = "yuanwanggu")
public class BookinfoJH {
    @Id
    @Column(name = "szBookID", comment = "条码号，图书唯一码", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 1)
    private String szBookID;
    @Column(name = "szName", comment = "题名", type = ColumnType.VARCHAR, length = 300, scale = 10, jdbcType = -9, order = 2)
    private String szName;
    @Column(name = "szAuthor", comment = "责任者", type = ColumnType.VARCHAR, length = 100, scale = 10, jdbcType = 12, order = 3)
    private String szAuthor;
    @Column(name = "fPrice", comment = "价格", type = ColumnType.FLOAT, length = 10, scale = 10, jdbcType = 3, order = 4)
    private double fPrice;
    @Column(name = "nPages", comment = "总页数", length = 10, scale = 10, jdbcType = 4, order = 5)
    private int nPages;
    @Column(name = "szMainWord", comment = "主题词", type = ColumnType.VARCHAR, length = 30, scale = 10, jdbcType = 12, order = 6)
    private String szMainWord;
    @Column(name = "szLibCD", comment = "馆藏地", type = ColumnType.VARCHAR, length = 30, scale = 10, jdbcType = 12, order = 7)
    private String szLibCD;
    @Column(name = "szMemo", comment = "备注", type = ColumnType.VARCHAR, length = 100, scale = 10, jdbcType = 12, order = 8)
    private String szMemo;
    @Column(name = "szQZH", comment = "全宗号", type = ColumnType.VARCHAR, length = 20, scale = 10, jdbcType = 12, order = 9)
    private String szQZH;
    @Column(name = "szMLH", comment = "目录号", type = ColumnType.VARCHAR, length = 20, scale = 10, jdbcType = 12, order = 10)
    private String szMLH;
    @Column(name = "szMJ", comment = "保管级别，密级", type = ColumnType.VARCHAR, length = 10, scale = 10, jdbcType = 12, order = 11)
    private String szMJ;
    @Column(name = "szCZH", comment = "存址号", type = ColumnType.VARCHAR, length = 20, scale = 10, jdbcType = 12, order = 12)
    private String szCZH;
    @Column(name = "szAJH", comment = "案卷号", type = ColumnType.VARCHAR, length = 20, scale = 10, jdbcType = 12, order = 13)
    private String szAJH;
    @Column(name = "szJH", comment = "件号", type = ColumnType.VARCHAR, length = 10, scale = 10, jdbcType = 12, order = 14)
    private String szJH;
    @Column(name = "szYH", comment = "页号", type = ColumnType.VARCHAR, length = 10, scale = 10, jdbcType = 12, order = 15)
    private String szYH;
    @Column(name = "szRQ", comment = "日期，格式为yyyyMMdd", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 16)
    private String szRQ;
    @Column(name = "szSMQZY", comment = "扫描起止页号", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 17)
    private String szSMQZY;
    @Column(name = "szSMYS", comment = "扫描页数", type = ColumnType.VARCHAR, length = 10, scale = 10, jdbcType = 12, order = 18)
    private String szSMYS;
    @Column(name = "szNameJM", comment = "题名简码", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 19)
    private String szNameJM;
    @Column(name = "szRemark1", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 20)
    private String szRemark1;
    @Column(name = "szRemark2", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 21)
    private String szRemark2;
    @Column(name = "szRemark3", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 22)
    private String szRemark3;
    @Column(name = "szRemark4", type = ColumnType.VARCHAR, length = 250, scale = 10, jdbcType = 12, order = 23)
    private String szRemark4;
    @Column(name = "szRemark5", type = ColumnType.VARCHAR, length = 250, scale = 10, jdbcType = 12, order = 24)
    private String szRemark5;

    public String getSzBookID() {
        return szBookID;
    }

    public void setSzBookID(String szBookID) {
        this.szBookID = szBookID;
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

    public int getNPages() {
        return nPages;
    }

    public void setNPages(int nPages) {
        this.nPages = nPages;
    }

    public String getSzMainWord() {
        return szMainWord;
    }

    public void setSzMainWord(String szMainWord) {
        this.szMainWord = szMainWord;
    }

    public String getSzLibCD() {
        return szLibCD;
    }

    public void setSzLibCD(String szLibCD) {
        this.szLibCD = szLibCD;
    }

    public String getSzMemo() {
        return szMemo;
    }

    public void setSzMemo(String szMemo) {
        this.szMemo = szMemo;
    }

    public String getSzQZH() {
        return szQZH;
    }

    public void setSzQZH(String szQZH) {
        this.szQZH = szQZH;
    }

    public String getSzMLH() {
        return szMLH;
    }

    public void setSzMLH(String szMLH) {
        this.szMLH = szMLH;
    }

    public String getSzMJ() {
        return szMJ;
    }

    public void setSzMJ(String szMJ) {
        this.szMJ = szMJ;
    }

    public String getSzCZH() {
        return szCZH;
    }

    public void setSzCZH(String szCZH) {
        this.szCZH = szCZH;
    }

    public String getSzAJH() {
        return szAJH;
    }

    public void setSzAJH(String szAJH) {
        this.szAJH = szAJH;
    }

    public String getSzJH() {
        return szJH;
    }

    public void setSzJH(String szJH) {
        this.szJH = szJH;
    }

    public String getSzYH() {
        return szYH;
    }

    public void setSzYH(String szYH) {
        this.szYH = szYH;
    }

    public String getSzRQ() {
        return szRQ;
    }

    public void setSzRQ(String szRQ) {
        this.szRQ = szRQ;
    }

    public String getSzSMQZY() {
        return szSMQZY;
    }

    public void setSzSMQZY(String szSMQZY) {
        this.szSMQZY = szSMQZY;
    }

    public String getSzSMYS() {
        return szSMYS;
    }

    public void setSzSMYS(String szSMYS) {
        this.szSMYS = szSMYS;
    }

    public String getSzNameJM() {
        return szNameJM;
    }

    public void setSzNameJM(String szNameJM) {
        this.szNameJM = szNameJM;
    }

    public String getSzRemark1() {
        return szRemark1;
    }

    public void setSzRemark1(String szRemark1) {
        this.szRemark1 = szRemark1;
    }

    public String getSzRemark2() {
        return szRemark2;
    }

    public void setSzRemark2(String szRemark2) {
        this.szRemark2 = szRemark2;
    }

    public String getSzRemark3() {
        return szRemark3;
    }

    public void setSzRemark3(String szRemark3) {
        this.szRemark3 = szRemark3;
    }

    public String getSzRemark4() {
        return szRemark4;
    }

    public void setSzRemark4(String szRemark4) {
        this.szRemark4 = szRemark4;
    }

    public String getSzRemark5() {
        return szRemark5;
    }

    public void setSzRemark5(String szRemark5) {
        this.szRemark5 = szRemark5;
    }

}
