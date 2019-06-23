package com.dr.entity;

import com.dr.framework.common.entity.BaseEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "Equipment", module = "tushu", comment = "机器位置")
public class Equipment extends BaseEntity {
    /**
     * 位置信息
     */
    @Column(name = "position1")
    private String position;
    /**
     * 编号
     */
    @Column(name = "number1")
    private String number;
    /**
     * 工控机
     */
    @Column(name = "IPC")
    private String IPC;
    /**
     * IP
     */
    @Column(name = "IP")
    private String IP;
    /**
     * MAC
     */
    @Column(name = "MAC")
    private String MAC;
    /**
     * 工控机方位
     */
    @Column(name = "IPCazimuth")
    private String IPCazimuth;
    /**
     * RFID地址
     */
    @Column(name = "RFID")
    private String RFID;
    /**
     * webapp地址
     */
    @Column(name = "webapp")
    private String webapp;
    @Column(name = "Guan")
    private String Guan;
    @Column(name = "statype")
    private String statype;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getIPC() {
        return IPC;
    }

    public void setIPC(String IPC) {
        this.IPC = IPC;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public String getIPCazimuth() {
        return IPCazimuth;
    }

    public void setIPCazimuth(String IPCazimuth) {
        this.IPCazimuth = IPCazimuth;
    }

    public String getRFID() {
        return RFID;
    }

    public void setRFID(String RFID) {
        this.RFID = RFID;
    }

    public String getWebapp() {
        return webapp;
    }

    public void setWebapp(String webapp) {
        this.webapp = webapp;
    }

    public String getGuan() {
        return Guan;
    }

    public void setGuan(String guan) {
        Guan = guan;
    }

    public String getStatype() {
        return statype;
    }

    public void setStatype(String statype) {
        this.statype = statype;
    }
}
