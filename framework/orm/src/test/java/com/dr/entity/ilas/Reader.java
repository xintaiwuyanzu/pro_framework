package com.dr.entity.ilas;

import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "NEW_READER", comment = "读者", module = "ilasmodul")
public class Reader implements IdEntity {
    @Id
    @Column(name = "R000", jdbcType = 3, order = 1)
    private int R000;
    @Column(name = "R01A", comment = "读者姓名", type = ColumnType.VARCHAR, length = 64, jdbcType = 12, order = 2)
    private String R01A;
    @Column(name = "R01B", comment = "读者证号", type = ColumnType.VARCHAR, length = 64, jdbcType = 12, order = 3)
    private String R01B;
    @Column(name = "R01C", type = ColumnType.VARCHAR, length = 64, jdbcType = 12, order = 4)
    private String R01C;
    @Column(name = "R01I", comment = "身份证号", type = ColumnType.VARCHAR, length = 64, jdbcType = 12, order = 5)
    private String R01I;
    @Column(name = "R01L", comment = "办证地点", type = ColumnType.VARCHAR, length = 32, jdbcType = 12, order = 6)
    private String R01L;
    @Column(name = "R01P", comment = "密码", type = ColumnType.VARCHAR, length = 64, jdbcType = 12, order = 7)
    private String R01P;
    @Column(name = "R01S", comment = "状态,n:有效,l:挂失,d:注销,y:冻结", length = 1, jdbcType = 1, order = 8)
    private String R01S;
    @Column(name = "R021", type = ColumnType.VARCHAR, length = 128, jdbcType = 12, order = 9)
    private String R021;
    @Column(name = "R022", type = ColumnType.VARCHAR, length = 128, jdbcType = 12, order = 10)
    private String R022;
    @Column(name = "R023", type = ColumnType.VARCHAR, length = 128, jdbcType = 12, order = 11)
    private String R023;
    @Column(name = "R024", type = ColumnType.VARCHAR, length = 128, jdbcType = 12, order = 12)
    private String R024;
    @Column(name = "R02A", type = ColumnType.VARCHAR, length = 16, jdbcType = 12, order = 13)
    private String R02A;
    @Column(name = "R02B", comment = "出生年月", type = ColumnType.VARCHAR, length = 16, jdbcType = 12, order = 14)
    private String R02B;
    @Column(name = "R02S", comment = "性别", length = 1, jdbcType = 1, order = 15)
    private String R02S;
    @Column(name = "R02T", type = ColumnType.VARCHAR, length = 4, jdbcType = 12, order = 16)
    private String R02T;
    @Column(name = "R02V", type = ColumnType.VARCHAR, length = 32, jdbcType = 12, order = 17)
    private String R02V;
    @Column(name = "R03A", comment = "居住地址", type = ColumnType.VARCHAR, length = 256, jdbcType = 12, order = 18)
    private String R03A;
    @Column(name = "R03E", comment = "邮箱地址", type = ColumnType.VARCHAR, length = 64, jdbcType = 12, order = 19)
    private String R03E;
    @Column(name = "R03H", type = ColumnType.VARCHAR, length = 32, jdbcType = 12, order = 20)
    private String R03H;
    @Column(name = "R03L", type = ColumnType.VARCHAR, length = 128, jdbcType = 12, order = 21)
    private String R03L;
    @Column(name = "R03M", comment = "手机号", type = ColumnType.VARCHAR, length = 128, jdbcType = 12, order = 22)
    private String R03M;
    @Column(name = "R03N", type = ColumnType.VARCHAR, length = 128, jdbcType = 12, order = 23)
    private String R03N;
    @Column(name = "R03S", type = ColumnType.VARCHAR, length = 128, jdbcType = 12, order = 24)
    private String R03S;
    @Column(name = "R03T", type = ColumnType.VARCHAR, length = 128, jdbcType = 12, order = 25)
    private String R03T;
    @Column(name = "R03U", comment = "工作单位", type = ColumnType.VARCHAR, length = 256, jdbcType = 12, order = 26)
    private String R03U;
    @Column(name = "R04B", comment = "有效期开始时间", type = ColumnType.VARCHAR, length = 32, jdbcType = 12, order = 27)
    private String R04B;
    @Column(name = "R04D", comment = "创卡时间", type = ColumnType.VARCHAR, length = 32, jdbcType = 12, order = 28)
    private String R04D;
    @Column(name = "R04E", comment = "有效期结束时间", type = ColumnType.VARCHAR, length = 32, jdbcType = 12, order = 29)
    private String R04E;
    @Column(name = "R04Z", type = ColumnType.VARCHAR, length = 32, jdbcType = 12, order = 30)
    private String R04Z;
    @Column(name = "R10A", type = ColumnType.VARCHAR, length = 1024, jdbcType = 12, order = 31)
    private String R10A;
    @Column(name = "R20A", type = ColumnType.VARCHAR, length = 256, jdbcType = 12, order = 32)
    private String R20A;
    private boolean falge;
    private String bianmincard;
    private String yizhong;
    private String erzhong;

    public String getYizhong() {
        return yizhong;
    }

    public void setYizhong(String yizhong) {
        this.yizhong = yizhong;
    }

    public String getErzhong() {
        return erzhong;
    }

    public void setErzhong(String erzhong) {
        this.erzhong = erzhong;
    }

    public int getR000() {
        return R000;
    }

    public void setR000(int r000) {
        this.R000 = r000;
    }

    public String getR01A() {
        return R01A;
    }

    public void setR01A(String R01A) {
        this.R01A = R01A;
    }

    public String getR01B() {
        return R01B;
    }

    public void setR01B(String R01B) {
        this.R01B = R01B;
    }

    public String getR01C() {
        return R01C;
    }

    public void setR01C(String R01C) {
        this.R01C = R01C;
    }

    public String getR01I() {
        return R01I;
    }

    public void setR01I(String R01I) {
        this.R01I = R01I;
    }

    public String getR01L() {
        return R01L;
    }

    public void setR01L(String R01L) {
        this.R01L = R01L;
    }

    public String getR01P() {
        return R01P;
    }

    public void setR01P(String R01P) {
        this.R01P = R01P;
    }

    public String getR01S() {
        return R01S;
    }

    public void setR01S(String R01S) {
        this.R01S = R01S;
    }

    public String getR021() {
        return R021;
    }

    public void setR021(String R021) {
        this.R021 = R021;
    }

    public String getR022() {
        return R022;
    }

    public void setR022(String R022) {
        this.R022 = R022;
    }

    public String getR023() {
        return R023;
    }

    public void setR023(String R023) {
        this.R023 = R023;
    }

    public String getR024() {
        return R024;
    }

    public void setR024(String R024) {
        this.R024 = R024;
    }

    public String getR02A() {
        return R02A;
    }

    public void setR02A(String R02A) {
        this.R02A = R02A;
    }

    public String getR02B() {
        return R02B;
    }

    public void setR02B(String R02B) {
        this.R02B = R02B;
    }

    public String getR02S() {
        return R02S;
    }

    public void setR02S(String R02S) {
        this.R02S = R02S;
    }

    public String getR02T() {
        return R02T;
    }

    public void setR02T(String R02T) {
        this.R02T = R02T;
    }

    public String getR02V() {
        return R02V;
    }

    public void setR02V(String R02V) {
        this.R02V = R02V;
    }

    public String getR03A() {
        return R03A;
    }

    public void setR03A(String R03A) {
        this.R03A = R03A;
    }

    public String getR03E() {
        return R03E;
    }

    public void setR03E(String R03E) {
        this.R03E = R03E;
    }

    public String getR03H() {
        return R03H;
    }

    public void setR03H(String R03H) {
        this.R03H = R03H;
    }

    public String getR03L() {
        return R03L;
    }

    public void setR03L(String R03L) {
        this.R03L = R03L;
    }

    public String getR03M() {
        return R03M;
    }

    public void setR03M(String R03M) {
        this.R03M = R03M;
    }

    public String getR03N() {
        return R03N;
    }

    public void setR03N(String R03N) {
        this.R03N = R03N;
    }

    public String getR03S() {
        return R03S;
    }

    public void setR03S(String R03S) {
        this.R03S = R03S;
    }

    public String getR03T() {
        return R03T;
    }

    public void setR03T(String R03T) {
        this.R03T = R03T;
    }

    public String getR03U() {
        return R03U;
    }

    public void setR03U(String R03U) {
        this.R03U = R03U;
    }

    public String getR04B() {
        return R04B;
    }

    public void setR04B(String R04B) {
        this.R04B = R04B;
    }

    public String getR04D() {
        return R04D;
    }

    public void setR04D(String R04D) {
        this.R04D = R04D;
    }

    public String getR04E() {
        return R04E;
    }

    public void setR04E(String R04E) {
        this.R04E = R04E;
    }

    public String getR04Z() {
        return R04Z;
    }

    public void setR04Z(String R04Z) {
        this.R04Z = R04Z;
    }

    public String getR10A() {
        return R10A;
    }

    public void setR10A(String R10A) {
        this.R10A = R10A;
    }

    public String getR20A() {
        return R20A;
    }

    public void setR20A(String R20A) {
        this.R20A = R20A;
    }

    @Override
    public String getId() {
        return String.valueOf(R000);
    }

    @Override
    public void setId(String s) {
        this.R000 = Integer.getInteger(s);
    }

    public boolean isFalge() {
        return falge;
    }

    public void setFalge(boolean falge) {
        this.falge = falge;
    }

    public String getBianmincard() {
        return bianmincard;
    }

    public void setBianmincard(String bianmincard) {
        this.bianmincard = bianmincard;
    }
}
