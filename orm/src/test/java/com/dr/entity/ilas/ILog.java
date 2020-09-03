package com.dr.entity.ilas;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "ILOG", comment = "日志表", module = "ilasmodul")
public class ILog {
    //添加读者
    public static final Integer LOG_TYPE_ADD_READER = 3011;
    // 修改读者
    public static final Integer LOG_TYPE_CHANGE_READER = 3012;
    //读者记录删除
    public static final Integer LOG_TYPE_delete_READER = 3013;
    //修改读者证号
    public static final Integer LOG_TYPE_CHANGE_READER_NO = 3015;
    //修改读者状态
    public static final Integer LOG_TYPE_CHANGE_READER_STATUS = 3014;
    //读者续期
    public static final Integer LOG_TYPE_READER_RENEWAL = 3020;
    //流通借出
    public static final Integer LOG_TYPE_BORROW = 3031;
    //流通租出
    public static final Integer LOG_TYPE_RENT = 3032;
    //流通还回
    public static final Integer LOG_TYPE_RETURN = 3033;
    //流通还回（改还日）
    public static final Integer LOG_TYPE_RETURN_CHANGE = 3034;
    //流通续借
    public static final Integer LOG_TYPE_RENEW = 3035;
    //流通损坏处理
    public static final Integer LOG_TYPE_DAMAGE = 3036;
    //流通丢失处理
    public static final Integer LOG_TYPE_LOST = 3037;
    //预约得到图书
    public static final Integer LOG_TYPE_RESERVE = 3040;
    //专项流通借出
    public static final Integer LOG_TYPE_专项借出 = 3041;
    //专项流通还回
    public static final Integer LOG_TYPE_专项还回 = 3042;
    //专项还回（改还日）
    public static final Integer LOG_TYPE_专项改还日 = 3043;
    //专项流通续借
    public static final Integer LOG_TYPE_专项续借 = 3044;
    //专项流通损坏
    public static final Integer LOG_TYPE_专项损坏 = 3045;
    //专项流通丢失
    public static final Integer LOG_TYPE_专项丢失 = 3046;
    //读者阅览登记
    public static final Integer LOG_TYPE_阅览登记 = 3051;
    //读者阅览文献
    public static final Integer LOG_TYPE_阅览文献 = 3052;
    //闭架借书登记
    public static final Integer LOG_TYPE_闭架借书登记 = 3053;
    //取消闭架借书登记
    public static final Integer LOG_TYPE_取消登记 = 3054;
    //闭架借书下架
    public static final Integer LOG_TYPE_下架 = 3055;
    //入藏确认
    public static final Integer LOG_TYPE_上架 = 3021;
    //读者阅览离馆
    public static final Integer LOG_TYPE_LEAVE = 3058;
    //读者交押金
    public static final Integer LOG_TYPE_READER_PAY_MONEY = 3981;
    //读者交过期欠款
    public static final Integer LOG_TYPE_READER_PAY_ARREARS = 3921;
    //取消过期欠费
    public static final Integer LOG_TYPE_READER_CANCEL_ARREARS = 3941;
    //读者退押金
    public static final Integer LOG_TYPE_READER_CASH_MONEY = 3982;
    //采编书目删除
    public static final Integer LOG_TYPE_REMOVE = 1053;
    @Id
    @Column(name = "LOG0", comment = "主建，是从序列获取的", type = ColumnType.FLOAT, jdbcType = 3, order = 1)
    private double LOG0;
    @Column(name = "LOGA1", comment = "操作日期yyyyMMdd", type = ColumnType.FLOAT, jdbcType = 3, order = 2)
    private double LOGA1;
    @Column(name = "LOGA2", comment = "操作时间 当前时间戳/1000 秒", type = ColumnType.FLOAT, jdbcType = 3, order = 3)
    private double LOGA2;
    @Column(name = "LOGB", type = ColumnType.FLOAT, jdbcType = 3, order = 4)
    private double LOGB;
    @Column(name = "LOGC", comment = "操作类型", type = ColumnType.FLOAT, jdbcType = 3, order = 5)
    private double LOGC;
    @Column(name = "LOGD", comment = "nDeviceNo", type = ColumnType.FLOAT, jdbcType = 3, order = 6)
    private double LOGD;
    @Column(name = "LOGE", comment = "nUserNo", type = ColumnType.FLOAT, jdbcType = 3, order = 12)
    private double LOGE;
    @Column(name = "LOGF", comment = "dbno", type = ColumnType.FLOAT, jdbcType = 3, order = 8)
    private double LOGF;
    @Column(name = "LOGG", comment = "nData1", type = ColumnType.FLOAT, jdbcType = 3, order = 9)
    private double LOGG;
    @Column(name = "LOGH", comment = "nData2", type = ColumnType.FLOAT, jdbcType = 3, order = 10)
    private double LOGH;
    @Column(name = "LOGI", comment = "strData3", type = ColumnType.VARCHAR, length = 36, jdbcType = 12, order = 11)
    private String LOGI;
    @Column(name = "LOGJ", comment = "strAddr", type = ColumnType.VARCHAR, length = 36, jdbcType = 12, order = 7)
    private String LOGJ;

    public double getLOG0() {
        return LOG0;
    }

    public void setLOG0(double LOG0) {
        this.LOG0 = LOG0;
    }

    public double getLOGA1() {
        return LOGA1;
    }

    public void setLOGA1(double LOGA1) {
        this.LOGA1 = LOGA1;
    }

    public double getLOGA2() {
        return LOGA2;
    }

    public void setLOGA2(double LOGA2) {
        this.LOGA2 = LOGA2;
    }

    public double getLOGB() {
        return LOGB;
    }

    public void setLOGB(double LOGB) {
        this.LOGB = LOGB;
    }

    public double getLOGC() {
        return LOGC;
    }

    public void setLOGC(double LOGC) {
        this.LOGC = LOGC;
    }

    public double getLOGD() {
        return LOGD;
    }

    public void setLOGD(double LOGD) {
        this.LOGD = LOGD;
    }

    public String getLOGJ() {
        return LOGJ;
    }

    public void setLOGJ(String LOGJ) {
        this.LOGJ = LOGJ;
    }

    public double getLOGF() {
        return LOGF;
    }

    public void setLOGF(double LOGF) {
        this.LOGF = LOGF;
    }

    public double getLOGG() {
        return LOGG;
    }

    public void setLOGG(double LOGG) {
        this.LOGG = LOGG;
    }

    public double getLOGH() {
        return LOGH;
    }

    public void setLOGH(double LOGH) {
        this.LOGH = LOGH;
    }

    public String getLOGI() {
        return LOGI;
    }

    public void setLOGI(String LOGI) {
        this.LOGI = LOGI;
    }

    public double getLOGE() {
        return LOGE;
    }

    public void setLOGE(double LOGE) {
        this.LOGE = LOGE;
    }

}
