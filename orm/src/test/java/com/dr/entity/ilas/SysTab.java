package com.dr.entity.ilas;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "SYS_TAB", comment = "ilas数据库表映射表", module = "ilasmodul")
public class SysTab {
    @Id
    @Column(name = "TABA", length = 4, jdbcType = 3, order = 1)
    private Integer TABA;
    @Column(name = "TABB", type = ColumnType.VARCHAR, length = 30, jdbcType = 12, order = 2)
    private String TABB;
    @Column(name = "TABC", type = ColumnType.VARCHAR, length = 30, jdbcType = 12, order = 3)
    private String TABC;
    @Column(name = "TABD", type = ColumnType.VARCHAR, length = 30, jdbcType = 12, order = 4)
    private String TABD;
    @Column(name = "TABE", length = 2, jdbcType = 3, order = 5)
    private Integer TABE;
    @Column(name = "TABF", type = ColumnType.VARCHAR, length = 10, jdbcType = 12, order = 6)
    private String TABF;
    @Column(name = "TABG", type = ColumnType.VARCHAR, length = 1, jdbcType = 12, order = 7)
    private String TABG;
    @Column(name = "TABH", type = ColumnType.VARCHAR, length = 30, jdbcType = 12, order = 8)
    private String TABH;
    @Column(name = "TABI", length = 1, jdbcType = 3, order = 9)
    private Integer TABI;
    @Column(name = "TABJ", type = ColumnType.VARCHAR, length = 40, jdbcType = 12, order = 10)
    private String TABJ;

    public Integer getTABA() {
        return TABA;
    }

    public void setTABA(Integer TABA) {
        this.TABA = TABA;
    }

    public String getTABB() {
        return TABB;
    }

    public void setTABB(String TABB) {
        this.TABB = TABB;
    }

    public String getTABC() {
        return TABC;
    }

    public void setTABC(String TABC) {
        this.TABC = TABC;
    }

    public String getTABD() {
        return TABD;
    }

    public void setTABD(String TABD) {
        this.TABD = TABD;
    }

    public Integer getTABE() {
        return TABE;
    }

    public void setTABE(Integer TABE) {
        this.TABE = TABE;
    }

    public String getTABF() {
        return TABF;
    }

    public void setTABF(String TABF) {
        this.TABF = TABF;
    }

    public String getTABG() {
        return TABG;
    }

    public void setTABG(String TABG) {
        this.TABG = TABG;
    }

    public String getTABH() {
        return TABH;
    }

    public void setTABH(String TABH) {
        this.TABH = TABH;
    }

    public Integer getTABI() {
        return TABI;
    }

    public void setTABI(Integer TABI) {
        this.TABI = TABI;
    }

    public String getTABJ() {
        return TABJ;
    }

    public void setTABJ(String TABJ) {
        this.TABJ = TABJ;
    }

}
