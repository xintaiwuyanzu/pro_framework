package com.dr.entity;

import com.dr.framework.common.entity.BaseEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "YUYUEJIESHU", module = "tushu", comment = "预约借书")
public class YuyueJieshu extends BaseEntity {
    /**
     * 用户id
     */
    @Column(name = "USERID")
    private String userId;

    /**
     * 用户姓名
     */
    @Column(name = "USERNAME")
    private String userName;

    /**
     * 用户读者证号
     */
    @Column(name = "USERNO")
    private String userNo;
    /**
     * 书id
     */
    @Column(name = "COLLECTIONID")
    private String collectionId;
    /**
     * 书名称
     */
    @Column(name = "COLLECTIONNAME")
    private String collectionName;

    /**
     * 索书号
     */
    @Column(name = "BOOKIDNO")
    private String bookidNo;

    /**
     * 书条码号
     */
    @Column(name = "BOOKBARCODE")
    private String bookBarcode;

    /**
     * 书所在馆Id
     */
    @Column(name = "LOCATIONID")
    private String locationId;

    /**
     * 书所在馆名称
     */
    @Column(name = "LOCATIONNAME")
    private String locationName;

    /**
     * 书所在馆具体位置
     */
    @Column(name = "BOOKADDRESS")
    private String bookAddress;

    /**
     * 预约时间
     */
    @Column(name = "CREATETIME")
    private long createTime;

    /**
     * 预约到期时间
     */
    @Column(name = "ENDTIME")
    private long endTime;

    /**
     * 0:预约中 1:预约到期（到期未取书） 2:已取书 3:已取消(手动操作取消)
     */
    @Column(name = "typestate")
    private String typestate;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getTypestate() {
        return typestate;
    }

    public void setTypestate(String typestate) {
        this.typestate = typestate;
    }

    public String getBookidNo() {
        return bookidNo;
    }

    public void setBookidNo(String bookidNo) {
        this.bookidNo = bookidNo;
    }

    public String getBookBarcode() {
        return bookBarcode;
    }

    public void setBookBarcode(String bookBarcode) {
        this.bookBarcode = bookBarcode;
    }

    public String getBookAddress() {
        return bookAddress;
    }

    public void setBookAddress(String bookAddress) {
        this.bookAddress = bookAddress;
    }
}
