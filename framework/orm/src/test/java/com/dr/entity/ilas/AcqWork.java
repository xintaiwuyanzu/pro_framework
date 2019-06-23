package com.dr.entity.ilas;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "ACQWORK", comment = "采访业务库", module = "ilasmodul")
public class AcqWork {
    @Id
    @Column(name = "ACQ0", type = ColumnType.FLOAT, jdbcType = 3, order = 1)
    private double ACQ0;
    @Column(name = "ACQA", type = ColumnType.VARCHAR, length = 1, jdbcType = 12, order = 2)
    private String ACQA;
    @Column(name = "ACQB", type = ColumnType.VARCHAR, length = 4, jdbcType = 12, order = 3)
    private String ACQB;
    @Column(name = "ACQD", type = ColumnType.FLOAT, jdbcType = 3, order = 4)
    private double ACQD;
    @Column(name = "ACQE", type = ColumnType.VARCHAR, length = 16, jdbcType = 12, order = 5)
    private String ACQE;
    @Column(name = "ACQG", type = ColumnType.VARCHAR, length = 36, jdbcType = 12, order = 6)
    private String ACQG;
    @Column(name = "ACQH", type = ColumnType.VARCHAR, length = 30, jdbcType = 12, order = 7)
    private String ACQH;
    @Column(name = "ACQJ", type = ColumnType.FLOAT, jdbcType = 3, order = 8)
    private Integer ACQJ;
    @Column(name = "ACQK", type = ColumnType.FLOAT, jdbcType = 3, order = 9)
    private double ACQK;
    @Column(name = "ACQL", type = ColumnType.FLOAT, jdbcType = 3, order = 10)
    private double ACQL;
    @Column(name = "ACQM", type = ColumnType.VARCHAR, length = 30, jdbcType = 12, order = 11)
    private String ACQM;
    @Column(name = "ACQN", type = ColumnType.VARCHAR, length = 12, jdbcType = 12, order = 12)
    private String ACQN;
    @Column(name = "ACQO", type = ColumnType.FLOAT, jdbcType = 3, order = 13)
    private double ACQO;
    @Column(name = "ACQP", type = ColumnType.FLOAT, jdbcType = 3, order = 14)
    private double ACQP;
    @Column(name = "ACQQ", type = ColumnType.VARCHAR, length = 6, jdbcType = 12, order = 15)
    private String ACQQ;
    @Column(name = "ACQR", type = ColumnType.FLOAT, jdbcType = 3, order = 16)
    private double ACQR;
    @Column(name = "ACQS", type = ColumnType.VARCHAR, length = 6, jdbcType = 12, order = 17)
    private String ACQS;
    @Column(name = "ACQT", type = ColumnType.FLOAT, jdbcType = 3, order = 18)
    private double ACQT;
    @Column(name = "ACQU", type = ColumnType.VARCHAR, length = 40, jdbcType = 12, order = 19)
    private String ACQU;
    @Column(name = "ACQV", type = ColumnType.FLOAT, jdbcType = 3, order = 20)
    private double ACQV;
    @Column(name = "ACQW", type = ColumnType.VARCHAR, length = 2, jdbcType = 12, order = 21)
    private String ACQW;
    @Column(name = "ACQX", type = ColumnType.VARCHAR, length = 2, jdbcType = 12, order = 22)
    private String ACQX;
    @Column(name = "ACQY", type = ColumnType.VARCHAR, length = 6, jdbcType = 12, order = 23)
    private String ACQY;
    @Column(name = "ACQZ", type = ColumnType.VARCHAR, length = 2, jdbcType = 12, order = 24)
    private String ACQZ;
    @Column(name = "ACQA1", type = ColumnType.FLOAT, jdbcType = 3, order = 25)
    private double ACQA1;
    @Column(name = "ACQB1", type = ColumnType.FLOAT, jdbcType = 3, order = 26)
    private double ACQB1;
    @Column(name = "ACQC1", type = ColumnType.VARCHAR, length = 24, jdbcType = 12, order = 27)
    private String ACQC1;
    @Column(name = "ACQD1", type = ColumnType.VARCHAR, length = 28, jdbcType = 12, order = 28)
    private String ACQD1;
    @Column(name = "ACQ1", type = ColumnType.FLOAT, jdbcType = 3, order = 29)
    private double ACQ1;
    @Column(name = "ACQ9", type = ColumnType.VARCHAR, length = 2048, jdbcType = 12, order = 30)
    private String ACQ9;

    public double getACQ0() {
        return ACQ0;
    }

    public void setACQ0(double ACQ0) {
        this.ACQ0 = ACQ0;
    }

    public String getACQA() {
        return ACQA;
    }

    public void setACQA(String ACQA) {
        this.ACQA = ACQA;
    }

    public String getACQB() {
        return ACQB;
    }

    public void setACQB(String ACQB) {
        this.ACQB = ACQB;
    }

    public double getACQD() {
        return ACQD;
    }

    public void setACQD(double ACQD) {
        this.ACQD = ACQD;
    }

    public String getACQE() {
        return ACQE;
    }

    public void setACQE(String ACQE) {
        this.ACQE = ACQE;
    }

    public String getACQG() {
        return ACQG;
    }

    public void setACQG(String ACQG) {
        this.ACQG = ACQG;
    }

    public String getACQH() {
        return ACQH;
    }

    public void setACQH(String ACQH) {
        this.ACQH = ACQH;
    }

    public Integer getACQJ() {
        return ACQJ;
    }

    public void setACQJ(int ACQJ) {
        this.ACQJ = ACQJ;
    }

    public double getACQK() {
        return ACQK;
    }

    public void setACQK(double ACQK) {
        this.ACQK = ACQK;
    }

    public double getACQL() {
        return ACQL;
    }

    public void setACQL(double ACQL) {
        this.ACQL = ACQL;
    }

    public String getACQM() {
        return ACQM;
    }

    public void setACQM(String ACQM) {
        this.ACQM = ACQM;
    }

    public String getACQN() {
        return ACQN;
    }

    public void setACQN(String ACQN) {
        this.ACQN = ACQN;
    }

    public double getACQO() {
        return ACQO;
    }

    public void setACQO(double ACQO) {
        this.ACQO = ACQO;
    }

    public double getACQP() {
        return ACQP;
    }

    public void setACQP(double ACQP) {
        this.ACQP = ACQP;
    }

    public String getACQQ() {
        return ACQQ;
    }

    public void setACQQ(String ACQQ) {
        this.ACQQ = ACQQ;
    }

    public double getACQR() {
        return ACQR;
    }

    public void setACQR(double ACQR) {
        this.ACQR = ACQR;
    }

    public String getACQS() {
        return ACQS;
    }

    public void setACQS(String ACQS) {
        this.ACQS = ACQS;
    }

    public double getACQT() {
        return ACQT;
    }

    public void setACQT(double ACQT) {
        this.ACQT = ACQT;
    }

    public String getACQU() {
        return ACQU;
    }

    public void setACQU(String ACQU) {
        this.ACQU = ACQU;
    }

    public double getACQV() {
        return ACQV;
    }

    public void setACQV(double ACQV) {
        this.ACQV = ACQV;
    }

    public String getACQW() {
        return ACQW;
    }

    public void setACQW(String ACQW) {
        this.ACQW = ACQW;
    }

    public String getACQX() {
        return ACQX;
    }

    public void setACQX(String ACQX) {
        this.ACQX = ACQX;
    }

    public String getACQY() {
        return ACQY;
    }

    public void setACQY(String ACQY) {
        this.ACQY = ACQY;
    }

    public String getACQZ() {
        return ACQZ;
    }

    public void setACQZ(String ACQZ) {
        this.ACQZ = ACQZ;
    }

    public double getACQA1() {
        return ACQA1;
    }

    public void setACQA1(double ACQA1) {
        this.ACQA1 = ACQA1;
    }

    public double getACQB1() {
        return ACQB1;
    }

    public void setACQB1(double ACQB1) {
        this.ACQB1 = ACQB1;
    }

    public String getACQC1() {
        return ACQC1;
    }

    public void setACQC1(String ACQC1) {
        this.ACQC1 = ACQC1;
    }

    public String getACQD1() {
        return ACQD1;
    }

    public void setACQD1(String ACQD1) {
        this.ACQD1 = ACQD1;
    }

    public double getACQ1() {
        return ACQ1;
    }

    public void setACQ1(double ACQ1) {
        this.ACQ1 = ACQ1;
    }

    public String getACQ9() {
        return ACQ9;
    }

    public void setACQ9(String ACQ9) {
        this.ACQ9 = ACQ9;
    }

}
