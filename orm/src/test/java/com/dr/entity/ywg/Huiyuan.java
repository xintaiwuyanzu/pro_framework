package com.dr.entity.ywg;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "l_ReaderChangePwd", module = "yuanwanggu", comment = "会员")
public class Huiyuan {
    /**
     * 主键id
     */
    @Id
    @Column(name = "nChangeID")
    private String Id;
    /**
     * 会员名字
     */
    @Column(name = "szName")
    private String Name;
    /**
     * 读者id
     */
    @Column(name = "szReaderID")
    private String ReaderId;
    /**
     * 原密码
     */
    @Column(name = "szOldPwd")
    private String OldPwd;
    /**
     * 新密码
     */
    @Column(name = "szNewPwd")
    private String NewPwd;
    /**
     * 更新时间
     */
    private String Updatedate;
    /**
     * IP地址
     */
    @Column(name = "szIPAddr")
    private String Ipadrr;
    /**
     *
     */
    private String DeviceName;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getReaderId() {
        return ReaderId;
    }

    public void setReaderId(String readerId) {
        ReaderId = readerId;
    }

    public String getOldPwd() {
        return OldPwd;
    }

    public void setOldPwd(String oldPwd) {
        OldPwd = oldPwd;
    }

    public String getNewPwd() {
        return NewPwd;
    }

    public void setNewPwd(String newPwd) {
        NewPwd = newPwd;
    }

    public String getUpdatedate() {
        return Updatedate;
    }

    public void setUpdatedate(String updatedate) {
        Updatedate = updatedate;
    }

    public String getIpadrr() {
        return Ipadrr;
    }

    public void setIpadrr(String ipadrr) {
        Ipadrr = ipadrr;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }
}
