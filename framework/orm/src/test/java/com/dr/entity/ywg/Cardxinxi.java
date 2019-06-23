package com.dr.entity.ywg;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "CRD_TRANS_Recd", module = "yuanwanggu", comment = "卡信息")
public class Cardxinxi {
    /**
     * 主键
     */
    @Id
    @Column(name = "nID")
    private String Id;
    /**
     * 身份证号
     */
    @Column(name = "szIdentifyCard")
    private String EntifyCard;
    /**
     * 办卡人姓名
     */
    @Column(name = "szName")
    private String Name;
    /**
     * 卡片类型id
     */
    @Column(name = "nCardTypeID")
    private int CardTypeId;
    /**
     * remak
     */
    @Column(name = "szRemark")
    private String Remak;
    /**
     * 开卡时间
     */
    @Column(name = "dtTransBegin")
    private String TransBegindate;
    /**
     * 办卡
     */
    @Column(name = "szCardDevice")
    private String CardDevice;
    /**
     * Step
     */
    @Column(name = "szStep")
    private String Step;
    /**
     * DeviceName
     */
    private String DeviceName;
    /**
     * 卡片状态
     */
    private String OperateType;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getEntifyCard() {
        return EntifyCard;
    }

    public void setEntifyCard(String entifyCard) {
        EntifyCard = entifyCard;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getCardTypeId() {
        return CardTypeId;
    }

    public void setCardTypeId(int cardTypeId) {
        CardTypeId = cardTypeId;
    }

    public String getRemak() {
        return Remak;
    }

    public void setRemak(String remak) {
        Remak = remak;
    }

    public String getTransBegindate() {
        return TransBegindate;
    }

    public void setTransBegindate(String transBegindate) {
        TransBegindate = transBegindate;
    }

    public String getCardDevice() {
        return CardDevice;
    }

    public void setCardDevice(String cardDevice) {
        CardDevice = cardDevice;
    }

    public String getStep() {
        return Step;
    }

    public void setStep(String step) {
        Step = step;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public String getOperateType() {
        return OperateType;
    }

    public void setOperateType(String operateType) {
        OperateType = operateType;
    }
}
