package com.dr.entity.ilas;

import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "S_REGIST", comment = "期刊记到库", module = "ilasmodul")
public class SRegist implements IdEntity {
    @Id
    @Column(name = "REG0", type = ColumnType.FLOAT, jdbcType = 3, order = 1)
    private double REG0;
    @Column(name = "REGA", type = ColumnType.VARCHAR, length = 1, jdbcType = 12, order = 2)
    private String REGA;
    @Column(name = "REGZ", type = ColumnType.VARCHAR, length = 1, jdbcType = 12, order = 3)
    private String REGZ;
    @Column(name = "REGC", type = ColumnType.VARCHAR, length = 4, jdbcType = 12, order = 4)
    private String REGC;
    @Column(name = "REGB", type = ColumnType.VARCHAR, length = 4, jdbcType = 12, order = 5)
    private String REGB;
    @Column(name = "REGD", type = ColumnType.FLOAT, jdbcType = 3, order = 6)
    private double REGD;
    @Column(name = "REGE", type = ColumnType.FLOAT, jdbcType = 3, order = 7)
    private double REGE;
    @Column(name = "REGF", type = ColumnType.FLOAT, jdbcType = 3, order = 8)
    private double REGF;
    @Column(name = "REGP", type = ColumnType.FLOAT, jdbcType = 3, order = 9)
    private double REGP;
    @Column(name = "REGV", type = ColumnType.VARCHAR, length = 20, jdbcType = 12, order = 10)
    private String REGV;
    @Column(name = "REGU", type = ColumnType.VARCHAR, length = 12, jdbcType = 12, order = 11)
    private String REGU;
    @Column(name = "REGN", type = ColumnType.VARCHAR, length = 30, jdbcType = 12, order = 12)
    private String REGN;
    @Column(name = "REGT", type = ColumnType.FLOAT, jdbcType = 3, order = 13)
    private double REGT;
    @Column(name = "REGJ", type = ColumnType.FLOAT, jdbcType = 3, order = 14)
    private double REGJ;
    @Column(name = "REGK", type = ColumnType.FLOAT, jdbcType = 3, order = 15)
    private double REGK;
    @Column(name = "REGL", type = ColumnType.FLOAT, jdbcType = 3, order = 16)
    private double REGL;
    @Column(name = "REGC1", type = ColumnType.VARCHAR, length = 12, jdbcType = 12, order = 17)
    private String REGC1;
    @Column(name = "REGD1", type = ColumnType.VARCHAR, length = 44, jdbcType = 12, order = 18)
    private String REGD1;
    @Column(name = "REG1", type = ColumnType.FLOAT, jdbcType = 3, order = 19)
    private double REG1;
    @Column(name = "REG9", type = ColumnType.VARCHAR, length = 2048, jdbcType = 12, order = 20)
    private String REG9;

    public double getREG0() {
        return REG0;
    }

    public void setREG0(double REG0) {
        this.REG0 = REG0;
    }

    public String getREGA() {
        return REGA;
    }

    public void setREGA(String REGA) {
        this.REGA = REGA;
    }

    public String getREGZ() {
        return REGZ;
    }

    public void setREGZ(String REGZ) {
        this.REGZ = REGZ;
    }

    public String getREGC() {
        return REGC;
    }

    public void setREGC(String REGC) {
        this.REGC = REGC;
    }

    public String getREGB() {
        return REGB;
    }

    public void setREGB(String REGB) {
        this.REGB = REGB;
    }

    public double getREGD() {
        return REGD;
    }

    public void setREGD(double REGD) {
        this.REGD = REGD;
    }

    public double getREGE() {
        return REGE;
    }

    public void setREGE(double REGE) {
        this.REGE = REGE;
    }

    public double getREGF() {
        return REGF;
    }

    public void setREGF(double REGF) {
        this.REGF = REGF;
    }

    public double getREGP() {
        return REGP;
    }

    public void setREGP(double REGP) {
        this.REGP = REGP;
    }

    public String getREGV() {
        return REGV;
    }

    public void setREGV(String REGV) {
        this.REGV = REGV;
    }

    public String getREGU() {
        return REGU;
    }

    public void setREGU(String REGU) {
        this.REGU = REGU;
    }

    public String getREGN() {
        return REGN;
    }

    public void setREGN(String REGN) {
        this.REGN = REGN;
    }

    public double getREGT() {
        return REGT;
    }

    public void setREGT(double REGT) {
        this.REGT = REGT;
    }

    public double getREGJ() {
        return REGJ;
    }

    public void setREGJ(double REGJ) {
        this.REGJ = REGJ;
    }

    public double getREGK() {
        return REGK;
    }

    public void setREGK(double REGK) {
        this.REGK = REGK;
    }

    public double getREGL() {
        return REGL;
    }

    public void setREGL(double REGL) {
        this.REGL = REGL;
    }

    public String getREGC1() {
        return REGC1;
    }

    public void setREGC1(String REGC1) {
        this.REGC1 = REGC1;
    }

    public String getREGD1() {
        return REGD1;
    }

    public void setREGD1(String REGD1) {
        this.REGD1 = REGD1;
    }

    public double getREG1() {
        return REG1;
    }

    public void setREG1(double REG1) {
        this.REG1 = REG1;
    }

    public String getREG9() {
        return REG9;
    }

    public void setREG9(String REG9) {
        this.REG9 = REG9;
    }

    @Override
    public String getId() {
        return String.valueOf(getREG0());
    }

    @Override
    public void setId(String id) {

    }
}
