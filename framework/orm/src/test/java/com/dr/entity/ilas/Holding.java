package com.dr.entity.ilas;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "HOLDING", comment = "馆藏数据", module = "ilasmodul")
public class Holding {
    @Id
    @Column(name = "HLDA", type = ColumnType.VARCHAR, length = 64, jdbcType = 12, order = 1)
    private String HLDA;
    @Column(name = "HLDB", type = ColumnType.FLOAT, jdbcType = 3, order = 2)
    private double HLDB;
    @Column(name = "HLDC", type = ColumnType.VARCHAR, length = 8, jdbcType = 12, order = 3)
    private String HLDC;
    @Column(name = "HLDD", type = ColumnType.VARCHAR, length = 1, jdbcType = 12, order = 4)
    private String HLDD;
    @Column(name = "HLDE", type = ColumnType.VARCHAR, length = 4, jdbcType = 12, order = 5)
    private String HLDE;
    @Column(name = "HLDF", type = ColumnType.VARCHAR, length = 4, jdbcType = 12, order = 6)
    private String HLDF;
    @Column(name = "HLDG", type = ColumnType.VARCHAR, length = 2, jdbcType = 12, order = 7)
    private String HLDG;
    @Column(name = "HLDH", type = ColumnType.VARCHAR, length = 1, jdbcType = 12, order = 8)
    private String HLDH;
    @Column(name = "HLDI", type = ColumnType.FLOAT, jdbcType = 3, order = 9)
    private double HLDI;
    @Column(name = "HLDJ", type = ColumnType.VARCHAR, length = 8, jdbcType = 12, order = 10)
    private String HLDJ;
    @Column(name = "HLDK", type = ColumnType.VARCHAR, length = 6, jdbcType = 12, order = 11)
    private String HLDK;
    @Column(name = "HLDL", type = ColumnType.VARCHAR, length = 8, jdbcType = 12, order = 12)
    private String HLDL;
    @Column(name = "HLDM", type = ColumnType.FLOAT, jdbcType = 3, order = 13)
    private double HLDM;
    @Column(name = "HLDN", type = ColumnType.VARCHAR, length = 1, jdbcType = 12, order = 14)
    private String HLDN;
    @Column(name = "HLDO", type = ColumnType.FLOAT, jdbcType = 3, order = 15)
    private double HLDO;
    @Column(name = "HLDP", type = ColumnType.FLOAT, jdbcType = 3, order = 16)
    private double HLDP;
    @Column(name = "HLDQ", type = ColumnType.FLOAT, jdbcType = 3, order = 17)
    private double HLDQ;
    @Column(name = "HLDR", type = ColumnType.FLOAT, jdbcType = 3, order = 18)
    private double HLDR;
    @Column(name = "HLDS", type = ColumnType.FLOAT, jdbcType = 3, order = 19)
    private double HLDS;
    @Column(name = "HLDT", type = ColumnType.VARCHAR, length = 4, jdbcType = 12, order = 20)
    private String HLDT;
    @Column(name = "HLDU", type = ColumnType.FLOAT, jdbcType = 3, order = 21)
    private double HLDU;
    @Column(name = "HLDV", type = ColumnType.VARCHAR, length = 2, jdbcType = 12, order = 22)
    private String HLDV;
    @Column(name = "HLDW", type = ColumnType.VARCHAR, length = 4, jdbcType = 12, order = 23)
    private String HLDW;
    @Column(name = "HLDX", type = ColumnType.VARCHAR, length = 1, jdbcType = 12, order = 24)
    private String HLDX;
    @Column(name = "HLD1", type = ColumnType.FLOAT, jdbcType = 3, order = 25)
    private double HLD1;
    @Column(name = "HLD9", type = ColumnType.VARCHAR, length = 2048, jdbcType = 12, order = 26)
    private String HLD9;

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

    public String getHLDC() {
        return HLDC;
    }

    public void setHLDC(String HLDC) {
        this.HLDC = HLDC;
    }

    public String getHLDD() {
        return HLDD;
    }

    public void setHLDD(String HLDD) {
        this.HLDD = HLDD;
    }

    public String getHLDE() {
        return HLDE;
    }

    public void setHLDE(String HLDE) {
        this.HLDE = HLDE;
    }

    public String getHLDF() {
        return HLDF;
    }

    public void setHLDF(String HLDF) {
        this.HLDF = HLDF;
    }

    public String getHLDG() {
        return HLDG;
    }

    public void setHLDG(String HLDG) {
        this.HLDG = HLDG;
    }

    public String getHLDH() {
        return HLDH;
    }

    public void setHLDH(String HLDH) {
        this.HLDH = HLDH;
    }

    public double getHLDI() {
        return HLDI;
    }

    public void setHLDI(double HLDI) {
        this.HLDI = HLDI;
    }

    public String getHLDJ() {
        return HLDJ;
    }

    public void setHLDJ(String HLDJ) {
        this.HLDJ = HLDJ;
    }

    public String getHLDK() {
        return HLDK;
    }

    public void setHLDK(String HLDK) {
        this.HLDK = HLDK;
    }

    public String getHLDL() {
        return HLDL;
    }

    public void setHLDL(String HLDL) {
        this.HLDL = HLDL;
    }

    public double getHLDM() {
        return HLDM;
    }

    public void setHLDM(double HLDM) {
        this.HLDM = HLDM;
    }

    public String getHLDN() {
        return HLDN;
    }

    public void setHLDN(String HLDN) {
        this.HLDN = HLDN;
    }

    public double getHLDO() {
        return HLDO;
    }

    public void setHLDO(double HLDO) {
        this.HLDO = HLDO;
    }

    public double getHLDP() {
        return HLDP;
    }

    public void setHLDP(double HLDP) {
        this.HLDP = HLDP;
    }

    public double getHLDQ() {
        return HLDQ;
    }

    public void setHLDQ(double HLDQ) {
        this.HLDQ = HLDQ;
    }

    public double getHLDR() {
        return HLDR;
    }

    public void setHLDR(double HLDR) {
        this.HLDR = HLDR;
    }

    public double getHLDS() {
        return HLDS;
    }

    public void setHLDS(double HLDS) {
        this.HLDS = HLDS;
    }

    public String getHLDT() {
        return HLDT;
    }

    public void setHLDT(String HLDT) {
        this.HLDT = HLDT;
    }

    public double getHLDU() {
        return HLDU;
    }

    public void setHLDU(double HLDU) {
        this.HLDU = HLDU;
    }

    public String getHLDV() {
        return HLDV;
    }

    public void setHLDV(String HLDV) {
        this.HLDV = HLDV;
    }

    public String getHLDW() {
        return HLDW;
    }

    public void setHLDW(String HLDW) {
        this.HLDW = HLDW;
    }

    public String getHLDX() {
        return HLDX;
    }

    public void setHLDX(String HLDX) {
        this.HLDX = HLDX;
    }

    public double getHLD1() {
        return HLD1;
    }

    public void setHLD1(double HLD1) {
        this.HLD1 = HLD1;
    }

    public String getHLD9() {
        return HLD9;
    }

    public void setHLD9(String HLD9) {
        this.HLD9 = HLD9;
    }

}
