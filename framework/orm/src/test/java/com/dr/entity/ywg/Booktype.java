package com.dr.entity.ywg;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "BooksType", module = "yuanwanggu", comment = "图书类型")
public class Booktype {
    /**
     * 类型名称
     */
    @Column(name = "szBooksType")
    private String booktypename;
    /**
     * 添加时间
     */
    private String addtime;
    /**
     * 类型id
     */
    @Id
    @Column(name = "nBooksTypeID")
    private String booktypeid;

    /**
     * 备注
     */
    private String beizhu;

    public String getBooktypename() {
        return booktypename;
    }

    public void setBooktypename(String booktypename) {
        this.booktypename = booktypename;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getBooktypeid() {
        return booktypeid;
    }

    public void setBooktypeid(String booktypeid) {
        this.booktypeid = booktypeid;
    }

    public String getBeizhu() {
        return beizhu;
    }

    public void setBeizhu(String beizhu) {
        this.beizhu = beizhu;
    }

}
