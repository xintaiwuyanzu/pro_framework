package com.dr.entity.ilas;

import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

/**
 * ilas 图书数据库
 */
@Table(name = "HoldloanView", comment = "图书基本信息视图", module = "ilasmodul")
public class HoldloanView implements IdEntity {
    @Id
    @Column(comment = "图书id")
    private String hlda;
    @Column(name = "tit1")
    private String title;
    @Column(name = "aut1")
    private String author;
    @Column
    private String isbn;
    @Column(name = "cal1")
    private String callno;
    @Column
    private String hlde;
    @Column
    private String hldf;
    @Column
    private String hldh;
    @Column
    private String hldj;
    @Column
    private String hldk;
    @Column
    private String hldl;
    @Column
    private String hldn;
    @Column
    private String hldq;
    @Column
    private String hldr;
    @Column
    private String hldi;
    @Column
    private String hldb;
    @Column
    private String hld1;
    @Column
    private String hld9;

    public String getHlda() {
        return hlda;
    }

    public void setHlda(String hlda) {
        this.hlda = hlda;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCallno() {
        return callno;
    }

    public void setCallno(String callno) {
        this.callno = callno;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }


    public String getHlde() {
        return hlde;
    }

    public void setHlde(String hlde) {
        this.hlde = hlde;
    }


    public String getHldf() {
        return hldf;
    }

    public void setHldf(String hldf) {
        this.hldf = hldf;
    }


    public String getHldh() {
        return hldh;
    }

    public void setHldh(String hldh) {
        this.hldh = hldh;
    }


    public String getHldj() {
        return hldj;
    }

    public void setHldj(String hldj) {
        this.hldj = hldj;
    }


    public String getHldk() {
        return hldk;
    }

    public void setHldk(String hldk) {
        this.hldk = hldk;
    }


    public String getHldl() {
        return hldl;
    }

    public void setHldl(String hldl) {
        this.hldl = hldl;
    }


    public String getHldn() {
        return hldn;
    }

    public void setHldn(String hldn) {
        this.hldn = hldn;
    }


    public String getHldq() {
        return hldq;
    }

    public void setHldq(String hldq) {
        this.hldq = hldq;
    }


    public String getHldr() {
        return hldr;
    }

    public void setHldr(String hldr) {
        this.hldr = hldr;
    }


    public String getHldi() {
        return hldi;
    }

    public void setHldi(String hldi) {
        this.hldi = hldi;
    }


    public String getHldb() {
        return hldb;
    }

    public void setHldb(String hldb) {
        this.hldb = hldb;
    }


    public String getHld1() {
        return hld1;
    }

    public void setHld1(String hld1) {
        this.hld1 = hld1;
    }


    public String getHld9() {
        return hld9;
    }

    public void setHld9(String hld9) {
        this.hld9 = hld9;
    }

    @Override
    public String getId() {
        return getHlda();
    }

    @Override
    public void setId(String s) {
        setHlda(s);
    }
}
