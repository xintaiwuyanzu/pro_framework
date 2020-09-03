package com.dr.entity.ilas;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;

public class MarcTable {
    @Id
    @Column(name = "MRC0", type = ColumnType.FLOAT, jdbcType = 3, order = 1)
    private double MRC0;
    @Column(name = "MRCA", type = ColumnType.FLOAT, length = 2, jdbcType = 3, order = 2)
    private double MRCA;
    @Column(name = "MRCB", type = ColumnType.FLOAT, length = 1, jdbcType = 3, order = 3)
    private double MRCB;
    @Column(name = "MRCC", jdbcType = 2004, order = 4)
    private byte[] MRCC;

    public double getMRC0() {
        return MRC0;
    }

    public void setMRC0(double MRC0) {
        this.MRC0 = MRC0;
    }

    public double getMRCA() {
        return MRCA;
    }

    public void setMRCA(double MRCA) {
        this.MRCA = MRCA;
    }

    public double getMRCB() {
        return MRCB;
    }

    public void setMRCB(double MRCB) {
        this.MRCB = MRCB;
    }

    public byte[] getMRCC() {
        return MRCC;
    }

    public void setMRCC(byte[] MRCC) {
        this.MRCC = MRCC;
    }
}
