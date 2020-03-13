package com.dr.entity.ilas;

import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "CIRFIN", comment = "缴费信息||流通财经库", module = "ilasmodul")
public class Cirfin implements IdEntity {
    @Column(name = "FIN0", comment = "事务记录号", type = ColumnType.FLOAT, jdbcType = 3, order = 1)
    private double FIN0;

    @Column(name = "FINR", comment = "读者记录号", type = ColumnType.FLOAT, jdbcType = 3, order = 2)
    private double FINR;

    /**
     * reader.financial.recordType
     */
    @Column(name = "FINT", comment = "记录类别", type = ColumnType.VARCHAR, length = 1, jdbcType = 12, order = 3)
    private String FINT;

    /**
     * 对应数据字典reader.financial.type
     */
    @Column(name = "FINA", comment = "财经类型", type = ColumnType.VARCHAR, length = 3, jdbcType = 12, order = 4)
    private String FINA;

    @Column(name = "FINC", comment = "地点/年", type = ColumnType.VARCHAR, length = 4, jdbcType = 12, order = 5)
    private String FINC;

    @Column(name = "FIND", comment = "专项服务标识", type = ColumnType.VARCHAR, length = 1, jdbcType = 12, order = 6)
    private String FIND;
    /**
     * reader.financial.status
     */
    @Column(name = "FINF", comment = "状态", type = ColumnType.VARCHAR, length = 1, jdbcType = 12, order = 7)
    private String FINF;

    @Column(name = "FING", comment = "文献条码号", type = ColumnType.VARCHAR, length = 16, jdbcType = 12, order = 8)
    private String FING;

    @Column(name = "FINH", comment = "倍率/余款", type = ColumnType.FLOAT, jdbcType = 3, order = 9)
    private double FINH;

    @Column(name = "FINI", comment = "日期", type = ColumnType.FLOAT, jdbcType = 3, order = 10)
    private double FINI;

    @Column(name = "FINJ", comment = "时间", type = ColumnType.FLOAT, jdbcType = 3, order = 11)
    private double FINJ;

    @Column(name = "FINK", comment = "款数", type = ColumnType.FLOAT, jdbcType = 3, order = 12)
    private double FINK;

    @Column(name = "FINL", comment = "经手人", type = ColumnType.VARCHAR, length = 12, jdbcType = 12, order = 13)
    private String FINL;

    @Column(name = "FINQ", comment = "付款方式", type = ColumnType.VARCHAR, length = 1, jdbcType = 12, order = 14)
    private String FINQ;

    @Column(name = "FINZ", comment = "附注", type = ColumnType.VARCHAR, length = 34, jdbcType = 12, order = 15)
    private String FINZ;
    @Id
    @Column(name = "FIN00", comment = "主建", type = ColumnType.FLOAT, jdbcType = 3, order = 16)
    private double FIN00;

    public double getFIN0() {
        return FIN0;
    }

    public void setFIN0(double FIN0) {
        this.FIN0 = FIN0;
    }

    public double getFINR() {
        return FINR;
    }

    public void setFINR(double FINR) {
        this.FINR = FINR;
    }

    public String getFINT() {
        return FINT;
    }

    public void setFINT(String FINT) {
        this.FINT = FINT;
    }

    public String getFINA() {
        return FINA;
    }

    public void setFINA(String FINA) {
        this.FINA = FINA;
    }

    public String getFINC() {
        return FINC;
    }

    public void setFINC(String FINC) {
        this.FINC = FINC;
    }

    public String getFIND() {
        return FIND;
    }

    public void setFIND(String FIND) {
        this.FIND = FIND;
    }

    public String getFINF() {
        return FINF;
    }

    public void setFINF(String FINF) {
        this.FINF = FINF;
    }

    public String getFING() {
        return FING;
    }

    public void setFING(String FING) {
        this.FING = FING;
    }

    public double getFINH() {
        return FINH;
    }

    public void setFINH(double FINH) {
        this.FINH = FINH;
    }

    public double getFINI() {
        return FINI;
    }

    public void setFINI(double FINI) {
        this.FINI = FINI;
    }

    public double getFINJ() {
        return FINJ;
    }

    public void setFINJ(double FINJ) {
        this.FINJ = FINJ;
    }

    public double getFINK() {
        return FINK;
    }

    public void setFINK(double FINK) {
        this.FINK = FINK;
    }

    public String getFINL() {
        return FINL;
    }

    public void setFINL(String FINL) {
        this.FINL = FINL;
    }

    public String getFINQ() {
        return FINQ;
    }

    public void setFINQ(String FINQ) {
        this.FINQ = FINQ;
    }

    public String getFINZ() {
        return FINZ;
    }

    public void setFINZ(String FINZ) {
        this.FINZ = FINZ;
    }

    public double getFIN00() {
        return FIN00;
    }

    public void setFIN00(double FIN00) {
        this.FIN00 = FIN00;
    }

    @Override
    public String getId() {
        return String.valueOf(getFIN00());
    }

    @Override
    public void setId(String s) {
        setFIN00(Double.parseDouble(s));
    }
}
