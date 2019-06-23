package com.dr.entity;

import com.dr.framework.common.entity.BaseEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "Randomdoor", module = "tushu", comment = "随机数开门")
public class Randomdoor extends BaseEntity {
    /**
     * 读者证号
     */
    @Column(name = "readerId")
    private String readerId;

    /**
     * 随机验证码
     */
    @Column(name = "randomId")
    private String randomId;

    /**
     * 位置
     */
    @Column(name = "address")
    private String address;

    /**
     * 有效期开始时间
     */
    @Column(name = "statedate")
    private long statedate;

    /**
     * 有效期结束时间
     */
    @Column(name = "enddate")
    private long enddate;

    @Column(name = "opendate")
    private long opendate;

    /**
     * 状态
     */
    @Column(name = "statetype")
    private String statetype;

    /**
     * 姓名
     */
    @Column(name = "BEIZHU")
    private String beizhu;

    public String getReaderId() {
        return readerId;
    }

    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getStatedate() {
        return statedate;
    }

    public void setStatedate(long statedate) {
        this.statedate = statedate;
    }

    public long getEnddate() {
        return enddate;
    }

    public void setEnddate(long enddate) {
        this.enddate = enddate;
    }

    public String getStatetype() {
        return statetype;
    }

    public void setStatetype(String statetype) {
        this.statetype = statetype;
    }

    public String getBeizhu() {
        return beizhu;
    }

    public void setBeizhu(String beizhu) {
        this.beizhu = beizhu;
    }

    public String getRandomId() {
        return randomId;
    }

    public void setRandomId(String randomId) {
        this.randomId = randomId;
    }

    public long getOpendate() {
        return opendate;
    }

    public void setOpendate(long opendate) {
        this.opendate = opendate;
    }
}
