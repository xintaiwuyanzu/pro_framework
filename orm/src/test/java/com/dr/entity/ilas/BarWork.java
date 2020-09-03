package com.dr.entity.ilas;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "BARWORK", comment = "采编馆藏库", module = "ilasmodul")
public class BarWork {
    @Id
    @Column(name = "HLDA", type = ColumnType.VARCHAR, length = 64, jdbcType = 12, order = 1)
    private String HLDA;
    @Column(name = "HLDB", type = ColumnType.FLOAT, jdbcType = 3, order = 2)
    private double HLDB;
    @Column(name = "HLDU", type = ColumnType.FLOAT, jdbcType = 3, order = 3)
    private double HLDU;
    @Column(name = "HLDF", type = ColumnType.VARCHAR, length = 4, jdbcType = 12, order = 4)
    private String HLDF;
    @Column(name = "HLDS", type = ColumnType.FLOAT, jdbcType = 3, order = 5)
    private double HLDS;
    @Column(name = "HLDE", type = ColumnType.VARCHAR, length = 4, jdbcType = 12, order = 6)
    private String HLDE;

    public String getHLDA() {
        return HLDA;
    }

    public void setHLDA(String HLDA) {
        this.HLDA = HLDA;
    }

    public double getHLDB() {
        return HLDB;
    }

    public void setHLDB(double HLDB) {
        this.HLDB = HLDB;
    }

    public double getHLDU() {
        return HLDU;
    }

    public void setHLDU(double HLDU) {
        this.HLDU = HLDU;
    }

    public String getHLDF() {
        return HLDF;
    }

    public void setHLDF(String HLDF) {
        this.HLDF = HLDF;
    }

    public double getHLDS() {
        return HLDS;
    }

    public void setHLDS(double HLDS) {
        this.HLDS = HLDS;
    }

    public String getHLDE() {
        return HLDE;
    }

    public void setHLDE(String HLDE) {
        this.HLDE = HLDE;
    }

}
