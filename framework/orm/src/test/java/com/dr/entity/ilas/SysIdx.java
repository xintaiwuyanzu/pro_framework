package com.dr.entity.ilas;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "SYS_IDX", comment = "ilas系统索引", module = "ilasmodul")
public class SysIdx {
    @Id
    @Column(name = "IDXA", length = 4, jdbcType = 3, order = 1)
    private Integer IDXA;
    @Column(name = "IDXB", length = 5, jdbcType = 3, order = 2)
    private Integer IDXB;
    @Column(name = "IDXC", type = ColumnType.VARCHAR, length = 50, jdbcType = 12, order = 3)
    private String IDXC;
    @Column(name = "IDXD", type = ColumnType.VARCHAR, length = 50, jdbcType = 12, order = 4)
    private String IDXD;
    @Column(name = "IDXE", type = ColumnType.VARCHAR, length = 200, jdbcType = 12, order = 5)
    private String IDXE;
    @Column(name = "IDXF", type = ColumnType.VARCHAR, length = 1, jdbcType = 12, order = 6)
    private String IDXF;

    public Integer getIDXA() {
        return IDXA;
    }

    public void setIDXA(Integer IDXA) {
        this.IDXA = IDXA;
    }

    public Integer getIDXB() {
        return IDXB;
    }

    public void setIDXB(Integer IDXB) {
        this.IDXB = IDXB;
    }

    public String getIDXC() {
        return IDXC;
    }

    public void setIDXC(String IDXC) {
        this.IDXC = IDXC;
    }

    public String getIDXD() {
        return IDXD;
    }

    public void setIDXD(String IDXD) {
        this.IDXD = IDXD;
    }

    public String getIDXE() {
        return IDXE;
    }

    public void setIDXE(String IDXE) {
        this.IDXE = IDXE;
    }

    public String getIDXF() {
        return IDXF;
    }

    public void setIDXF(String IDXF) {
        this.IDXF = IDXF;
    }

}
