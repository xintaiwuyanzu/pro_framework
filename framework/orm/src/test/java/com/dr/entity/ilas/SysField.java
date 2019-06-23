package com.dr.entity.ilas;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "SYS_FIELD", comment = "ilas表名字段含义映射表", module = "ilasmodul")
public class SysField {
    @Id
    @Column(name = "FLDA", length = 6, jdbcType = 3, order = 1)
    private Integer FLDA;
    @Column(name = "FLDB", length = 4, jdbcType = 3, order = 2)
    private Integer FLDB;
    @Column(name = "FLDC", type = ColumnType.VARCHAR, length = 10, jdbcType = 12, order = 3)
    private String FLDC;
    @Column(name = "FLDD", type = ColumnType.VARCHAR, length = 10, jdbcType = 12, order = 4)
    private String FLDD;
    @Column(name = "FLDE", length = 10, jdbcType = 3, order = 5)
    private Integer FLDE;
    @Column(name = "FLDF", length = 10, jdbcType = 3, order = 6)
    private Integer FLDF;
    @Column(name = "FLDG", type = ColumnType.VARCHAR, length = 50, jdbcType = 12, order = 7)
    private String FLDG;
    @Column(name = "FLDH", type = ColumnType.VARCHAR, length = 10, jdbcType = 12, order = 8)
    private String FLDH;
    @Column(name = "FLDI", type = ColumnType.VARCHAR, length = 1, jdbcType = 12, order = 9)
    private String FLDI;

    public Integer getFLDA() {
        return FLDA;
    }

    public void setFLDA(Integer FLDA) {
        this.FLDA = FLDA;
    }

    public Integer getFLDB() {
        return FLDB;
    }

    public void setFLDB(Integer FLDB) {
        this.FLDB = FLDB;
    }

    public String getFLDC() {
        return FLDC;
    }

    public void setFLDC(String FLDC) {
        this.FLDC = FLDC;
    }

    public String getFLDD() {
        return FLDD;
    }

    public void setFLDD(String FLDD) {
        this.FLDD = FLDD;
    }

    public Integer getFLDE() {
        return FLDE;
    }

    public void setFLDE(Integer FLDE) {
        this.FLDE = FLDE;
    }

    public Integer getFLDF() {
        return FLDF;
    }

    public void setFLDF(Integer FLDF) {
        this.FLDF = FLDF;
    }

    public String getFLDG() {
        return FLDG;
    }

    public void setFLDG(String FLDG) {
        this.FLDG = FLDG;
    }

    public String getFLDH() {
        return FLDH;
    }

    public void setFLDH(String FLDH) {
        this.FLDH = FLDH;
    }

    public String getFLDI() {
        return FLDI;
    }

    public void setFLDI(String FLDI) {
        this.FLDI = FLDI;
    }

}
