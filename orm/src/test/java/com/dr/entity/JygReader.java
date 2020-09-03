package com.dr.entity;

import com.dr.framework.common.entity.BaseEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "JYGREADER", module = "tushu", comment = "读者")
public class JygReader extends BaseEntity {

    /**
     * 读者姓名
     */
    @Column(name = "READERNAME")
    private String readerName;
    /**
     * 卡片类型
     */
    @Column(name = "CARDTYPE")
    private String cardtype;
    /**
     * 读者证号
     */
    @Column(name = "READERID")
    private String readerId;
    /**
     * 便民卡号
     */
    @Column(name = "BIANMINCARD")
    private String bianmincard;
    /**
     * 身份证号
     */
    @Column(name = "IDCORDT")
    private String idcordt;
    /**
     * 办证地点
     */
    @Column(name = "ADDRESS")
    private String address;
    /**
     * 密码
     */
    @Column(name = "PASWORD")
    private String pasword;
    /**
     * 出生年月
     */
    @Column(name = "CHUSHENGDATE")
    private String chushengdate;
    /**
     * 性别
     */
    @Column(name = "SEX")
    private String sex;
    /**
     * 居住地址
     */
    @Column(name = "JZADDRESS" ,type = ColumnType.VARCHAR, length = 1000)
    private String jzaddress;
    /**
     * 邮箱地址
     */
    @Column(name = "EMILE", type = ColumnType.VARCHAR, length = 1000)
    private String emile;
    /**
     * 手机号
     */
    @Column(name = "SHOUJIHAO")
    private String shoujihao;
    /**
     * 创卡时间
     */
    @Column(name = "JIANKADATE")
    private String jiankadate;
    /**
     * 有效期开始时间
     */
    @Column(name = "STATEDATE")
    private String statedate;
    /**
     * 有效期结束时间
     */
    @Column(name = "ENDDATE")
    private String enddate;
    /**
     * 状态
     */
    @Column(name = "STATETYPE")
    private String Statetype;

    /**
     * 开户管
     */
    @Column(name = "KAIHUGUAN")
    private String kaihuguan;

    /**
     * 一中学生证号码
     */
    @Column(name = "yizhong")
    private String yizhong;

    /**
     * 二中学生证号码
     */
    @Column(name = "erzhong")
    private String erzhong;



    @Column(name = "photo",comment = "照片", type = ColumnType.CLOB, length = 4000 )
    private String photo;

    @Column(name = "flage" )
    private boolean flage;

    public String getReaderName() {
        return readerName;
    }

    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }

    public String getCardtype() {
        return cardtype;
    }

    public void setCardtype(String cardtype) {
        this.cardtype = cardtype;
    }

    public String getReaderId() {
        return readerId;
    }

    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }

    public String getBianmincard() {
        return bianmincard;
    }

    public void setBianmincard(String bianmincard) {
        this.bianmincard = bianmincard;
    }

    public String getIdcordt() {
        return idcordt;
    }

    public void setIdcordt(String idcordt) {
        this.idcordt = idcordt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPasword() {
        return pasword;
    }

    public void setPasword(String pasword) {
        this.pasword = pasword;
    }

    public String getChushengdate() {
        return chushengdate;
    }

    public void setChushengdate(String chushengdate) {
        this.chushengdate = chushengdate;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getJzaddress() {
        return jzaddress;
    }

    public void setJzaddress(String jzaddress) {
        this.jzaddress = jzaddress;
    }

    public String getEmile() {
        return emile;
    }

    public void setEmile(String emile) {
        this.emile = emile;
    }

    public String getShoujihao() {
        return shoujihao;
    }

    public void setShoujihao(String shoujihao) {
        this.shoujihao = shoujihao;
    }

    public String getJiankadate() {
        return jiankadate;
    }

    public void setJiankadate(String jiankadate) {
        this.jiankadate = jiankadate;
    }

    public String getStatedate() {
        return statedate;
    }

    public void setStatedate(String statedate) {
        this.statedate = statedate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getKaihuguan() {
        return kaihuguan;
    }

    public void setKaihuguan(String kaihuguan) {
        this.kaihuguan = kaihuguan;
    }

    public String getStatetype() {
        return Statetype;
    }

    public void setStatetype(String statetype) {
        Statetype = statetype;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isFlage() {
        return flage;
    }

    public void setFlage(boolean flage) {
        this.flage = flage;
    }

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
}
