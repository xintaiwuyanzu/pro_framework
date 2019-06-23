package com.dr.entity.ilas;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;

public class IdxTable {
    @Id
    @Column(name = "IDXA", type = ColumnType.FLOAT, jdbcType = 3, order = 1)
    private double IDXA;
    @Column(name = "IDXB", type = ColumnType.FLOAT, length = 5, jdbcType = 3, order = 2)
    private double IDXB;
    @Column(name = "IDXC", type = ColumnType.VARCHAR, length = 200, jdbcType = 12, order = 3)
    private String IDXC;
    @Column(name = "IDXD", type = ColumnType.VARCHAR, length = 1500, jdbcType = 12, order = 4)
    private String IDXD;

    public double getIDXA() {
        return IDXA;
    }

    public void setIDXA(double IDXA) {
        this.IDXA = IDXA;
    }

    public double getIDXB() {
        return IDXB;
    }

    public void setIDXB(double IDXB) {
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
}
