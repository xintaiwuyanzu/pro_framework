package com.dr.framework.sys.entity;

import com.dr.framework.common.entity.BaseStatusEntity;
import com.dr.framework.common.entity.SourceRefEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.util.Constants;

/**
 * 系统用户表
 *
 * @author dr
 */
@Table(name = Constants.SYS_TABLE_PREFIX + "person", comment = "用户", module = Constants.SYS_MODULE_NAME)
public class Person extends BaseStatusEntity<String> implements SourceRefEntity {
    @Column(name = "user_name", comment = "用户姓名", length = 500)
    private String userName;
    @Column(name = "nick_name", comment = "用户别名", length = 500)
    private String nickName;
    @Column(name = "remark", comment = "备注", length = 200)
    private String remark;
    @Column(name = "user_code", comment = "用户编号", length = 200)
    private String userCode;
    @Column(name = "id_no", comment = "身份证号", length = 20)
    private String idNo;
    /**
     * 出生日期
     * 格式YYYYMMDD
     * 可以从身份证中提取出来，
     * 范围查询的时候用到了
     */
    @Column(comment = "出生日期", length = 10)
    private long birthday;

    /**
     * 多个可以使用
     *
     * @see com.dr.framework.common.entity.IdEntity#MULTI_STR_SPLIT_CHAR
     * 分割
     */
    @Column(name = "email", comment = "邮箱", length = 200)
    private String email;
    /**
     * 多个可以使用
     *
     * @see com.dr.framework.common.entity.IdEntity#MULTI_STR_SPLIT_CHAR
     * 分割
     */
    @Column(name = "mobile", comment = "手机", length = 200)
    private String mobile;
    @Column(name = "phone", comment = "电话", length = 200)
    private String phone;
    /**
     * 可以从身份证中提取出来
     * 1是男性
     * 0是女性
     * -1是未知
     */
    @Column(name = "sex", comment = "性别")
    private int sex;
    @Column(name = "nation", comment = "民族", length = 10)
    private String nation;
    @Column(comment = "微信账号", length = 200)
    private String weiChatId;
    @Column(name = "qq", comment = "qq账号", length = 200)
    private String qq;
    /**
     * 多个可以使用
     *
     * @see com.dr.framework.common.entity.IdEntity#MULTI_STR_SPLIT_CHAR
     * 分割
     */
    @Column(name = "person_type", comment = "用户类型", length = 100)
    private String personType;

    @Column(name = "address", comment = "常驻地", length = 1000)
    private String address;
    @Column(name = "address_id", comment = "常住地关联id", length = 100)
    private String addressId;

    @Column(name = "avatar_file_id", comment = "头像图片ID", simple = "头像", length = 100)
    private String avatarFileId;

    @Column(name = "source_ref", comment = "数据来源", length = 100)
    private String sourceRef;

    @Column(name = "sys_id", comment = "所属子系统")
    private String sysId;

    @Column(comment = "启用日期", type = ColumnType.DATE)
    private long enableDate;
    @Column(comment = "启用人ID", simple = "创建人", length = 100)
    private String enablePerson;
    @Column(comment = "禁用日期", type = ColumnType.DATE)
    private long disableDate;
    @Column(comment = "禁用人ID", simple = "更新人", length = 100)
    private String disablePerson;

    @Override
    public String getSourceRef() {
        return sourceRef;
    }

    @Override
    public void setSourceRef(String sourceRef) {
        this.sourceRef = sourceRef;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getAvatarFileId() {
        return avatarFileId;
    }

    public void setAvatarFileId(String avatarFileId) {
        this.avatarFileId = avatarFileId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getWeiChatId() {
        return weiChatId;
    }

    public void setWeiChatId(String weiChatId) {
        this.weiChatId = weiChatId;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public long getEnableDate() {
        return enableDate;
    }

    public void setEnableDate(long enableDate) {
        this.enableDate = enableDate;
    }

    public String getEnablePerson() {
        return enablePerson;
    }

    public void setEnablePerson(String enablePerson) {
        this.enablePerson = enablePerson;
    }

    public long getDisableDate() {
        return disableDate;
    }

    public void setDisableDate(long disableDate) {
        this.disableDate = disableDate;
    }

    public String getDisablePerson() {
        return disablePerson;
    }

    public void setDisablePerson(String disablePerson) {
        this.disablePerson = disablePerson;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }
}
