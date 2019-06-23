package com.dr.entity.ilas;

import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "PUBLISH", comment = "出版社", module = "ilasmodul")
public class Publish implements IdEntity {
    @Id
    @Column(name = "PUBA", comment = "机构码", type = ColumnType.VARCHAR, length = 15, jdbcType = 12, order = 1)
    private String puba;
    @Column(name = "PUB0", comment = "PUB0", type = ColumnType.VARCHAR, length = 1, jdbcType = 12, order = 2)
    private String PUB0;
    @Column(name = "PUBB", comment = "机构名", type = ColumnType.VARCHAR, length = 80, jdbcType = 12, order = 3)
    private String pubb;
    @Column(name = "PUBC", comment = "机构性质", type = ColumnType.VARCHAR, length = 12, jdbcType = 12, order = 4)
    private String PUBC;
    @Column(name = "PUBD", comment = "所在地区名", type = ColumnType.VARCHAR, length = 40, jdbcType = 12, order = 5)
    private String PUBD;
    @Column(name = "PUBE", comment = "地区编码", type = ColumnType.VARCHAR, length = 12, jdbcType = 12, order = 6)
    private String PUBE;
    @Column(name = "PUBF", comment = "联系人", type = ColumnType.VARCHAR, length = 24, jdbcType = 12, order = 7)
    private String PUBF;
    @Column(name = "PUBG", comment = "详细地址", type = ColumnType.VARCHAR, length = 100, jdbcType = 12, order = 8)
    private String PUBG;
    @Column(name = "PUBH", comment = "电话", type = ColumnType.VARCHAR, length = 40, jdbcType = 12, order = 9)
    private String PUBH;
    @Column(name = "PUBI", comment = "邮编", type = ColumnType.VARCHAR, length = 16, jdbcType = 12, order = 10)
    private String PUBI;
    @Column(name = "PUBJ", comment = "E-mail", type = ColumnType.VARCHAR, length = 40, jdbcType = 12, order = 11)
    private String PUBJ;
    @Column(name = "PUBK", comment = "银行名", type = ColumnType.VARCHAR, length = 80, jdbcType = 12, order = 12)
    private String PUBK;
    @Column(name = "PUBL", comment = "银行帐号", type = ColumnType.VARCHAR, length = 40, jdbcType = 12, order = 13)
    private String PUBL;
    @Column(name = "PUBM", comment = "币种", type = ColumnType.VARCHAR, length = 12, jdbcType = 12, order = 14)
    private String PUBM;

    public String getPuba() {
        return puba;
    }

    public void setPuba(String PUBA) {
        this.puba = PUBA;
    }

    public String getPUB0() {
        return PUB0;
    }

    public void setPUB0(String PUB0) {
        this.PUB0 = PUB0;
    }

    public String getPubb() {
        return pubb;
    }

    public void setPubb(String PUBB) {
        this.pubb = PUBB;
    }

    public String getPUBC() {
        return PUBC;
    }

    public void setPUBC(String PUBC) {
        this.PUBC = PUBC;
    }

    public String getPUBD() {
        return PUBD;
    }

    public void setPUBD(String PUBD) {
        this.PUBD = PUBD;
    }

    public String getPUBE() {
        return PUBE;
    }

    public void setPUBE(String PUBE) {
        this.PUBE = PUBE;
    }

    public String getPUBF() {
        return PUBF;
    }

    public void setPUBF(String PUBF) {
        this.PUBF = PUBF;
    }

    public String getPUBG() {
        return PUBG;
    }

    public void setPUBG(String PUBG) {
        this.PUBG = PUBG;
    }

    public String getPUBH() {
        return PUBH;
    }

    public void setPUBH(String PUBH) {
        this.PUBH = PUBH;
    }

    public String getPUBI() {
        return PUBI;
    }

    public void setPUBI(String PUBI) {
        this.PUBI = PUBI;
    }

    public String getPUBJ() {
        return PUBJ;
    }

    public void setPUBJ(String PUBJ) {
        this.PUBJ = PUBJ;
    }

    public String getPUBK() {
        return PUBK;
    }

    public void setPUBK(String PUBK) {
        this.PUBK = PUBK;
    }

    public String getPUBL() {
        return PUBL;
    }

    public void setPUBL(String PUBL) {
        this.PUBL = PUBL;
    }

    public String getPUBM() {
        return PUBM;
    }

    public void setPUBM(String PUBM) {
        this.PUBM = PUBM;
    }

    @Override
    public String getId() {
        return getPuba();
    }

    @Override
    public void setId(String s) {
        setPuba(s);
    }
}
