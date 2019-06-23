package com.dr.entity.ilas;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "KINDNUM", comment = "书次号库", module = "ilasmodul")
public class KindNum {
    @Id
    @Column(name = "KIN0", type = ColumnType.FLOAT, jdbcType = 3, order = 1)
    private double KIN0;
    @Column(name = "KINA", type = ColumnType.VARCHAR, length = 4, jdbcType = 12, order = 2)
    private String KINA;
    @Column(name = "KINB", type = ColumnType.VARCHAR, length = 23, jdbcType = 12, order = 3)
    private String KINB;
    @Column(name = "KINC", type = ColumnType.FLOAT, jdbcType = 3, order = 4)
    private double KINC;

    public double getKIN0() {
        return KIN0;
    }

    public void setKIN0(double KIN0) {
        this.KIN0 = KIN0;
    }

    public String getKINA() {
        return KINA;
    }

    public void setKINA(String KINA) {
        this.KINA = KINA;
    }

    public String getKINB() {
        return KINB;
    }

    public void setKINB(String KINB) {
        this.KINB = KINB;
    }

    public double getKINC() {
        return KINC;
    }

    public void setKINC(double KINC) {
        this.KINC = KINC;
    }

}
