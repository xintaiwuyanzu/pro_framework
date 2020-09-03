package com.dr.entity;

import com.dr.framework.common.entity.BaseEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "LOGINFO", module = "tushu", comment = "日志")
public class LogInfo extends BaseEntity {

    /**
     * 读者id
     */
    @Column(name = "PERSONID")
    private String personId;
    /**
     * 读者姓名
     */
    @Column(name = "PERSONNAME")
    private String personName;
    /**
     * 索书号
     */
    @Column(name = "CLASSNO")
    private String classno;
    /**
     * 书名
     */
    @Column(name = "BOOKNAME")
    private String bookName;
    /**
     * 条码号
     */
    @Column(name = "RFID")
    private String rfid;
    /**
     * 时间
     */
    @Column(name = "ADDDATE")
    private String adddate;
    /**
     * 借还标志
     */
    @Column(name = "JHSTUST")
    private String jhstust;
    /**
     * 管藏
     */
    @Column(name = "GUANZ")
    private long guanz;

    /**
     * 备注
     */
    @Column(name = "BEIZHI")
    private String beizhi;

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getClassno() {
        return classno;
    }

    public void setClassno(String classno) {
        this.classno = classno;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public String getAdddate() {
        return adddate;
    }

    public void setAdddate(String adddate) {
        this.adddate = adddate;
    }

    public String getJhstust() {
        return jhstust;
    }

    public void setJhstust(String jhstust) {
        this.jhstust = jhstust;
    }

    public String getBeizhi() {
        return beizhi;
    }

    public void setBeizhi(String beizhi) {
        this.beizhi = beizhi;
    }

    public long getGuanz() {
        return guanz;
    }

    public void setGuanz(long guanz) {
        this.guanz = guanz;
    }
}
