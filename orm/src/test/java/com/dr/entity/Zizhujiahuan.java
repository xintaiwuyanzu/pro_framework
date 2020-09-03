package com.dr.entity;

import com.dr.framework.common.entity.BaseEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "Zizhujiahuan", module = "tushu", comment = "自主借还")
public class Zizhujiahuan extends BaseEntity {
    /**
     * 进入时间
     */
    @Column(name = "JRDATE")
    private long jrdate;

    /**
     * 闸机位置
     */
    @Column(name = "ZJADDRESS")
    private String zjaddress;
    /**
     * 身份证号
     */
    @Column(name = "IDCARDT")
    private String idcardt;
    /**
     * 卡的类型
     */
    @Column(name = "CARDTTYPE")
    private String cardttype;

    /**
     * 书的数量
     */
    @Column(name = "BOOKST")
    private String bookst;
    /**
     * 管藏
     */
    @Column(name = "GUANZ")
    private String guanz;

    /**
     * 标志(进出管状态)
     */
    @Column(name = "BIAOZHI")
    private int biaozhi;

    /**
     * 备注
     */
    @Column(name = "BEIZHU")
    private String beizhu;

    @Column(name = "name1")
    private String name;

    public long getJrdate() {
        return jrdate;
    }

    public void setJrdate(long jrdate) {
        this.jrdate = jrdate;
    }

    public String getBeizhu() {
        return beizhu;
    }

    public void setBeizhu(String beizhu) {
        this.beizhu = beizhu;
    }

    public String getGuanz() {
        return guanz;
    }

    public void setGuanz(String guanz) {
        this.guanz = guanz;
    }

    public int getBiaozhi() {
        return biaozhi;
    }

    public void setBiaozhi(int biaozhi) {
        this.biaozhi = biaozhi;
    }

    public String getZjaddress() {
        return zjaddress;
    }

    public void setZjaddress(String zjaddress) {
        this.zjaddress = zjaddress;
    }

    public String getIdcardt() {
        return idcardt;
    }

    public void setIdcardt(String idcardt) {
        this.idcardt = idcardt;
    }

    public String getCardttype() {
        return cardttype;
    }

    public void setCardttype(String cardttype) {
        this.cardttype = cardttype;
    }

    public String getBookst() {
        return bookst;
    }

    public void setBookst(String bookst) {
        this.bookst = bookst;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
