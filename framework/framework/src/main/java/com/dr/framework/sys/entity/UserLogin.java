package com.dr.framework.sys.entity;

import com.dr.framework.common.entity.BaseStatusEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.util.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author dr
 */
@Table(name = Constants.SYS_TABLE_PREFIX + "user_login", comment = "登陆用户", module = Constants.SYS_MODULE_NAME)
public class UserLogin extends BaseStatusEntity<Integer> {
    @Column(name = "person_id", comment = "用户id", length = 100, joins = @Column.Join(table = Person.class, column = "id"))
    private String personId;
    @Column(name = "login_id", comment = "登陆账号", length = 100)
    private String loginId;
    @JsonIgnore
    @Column(name = "pass_word", comment = "密码", length = 100)
    private String password;
    @Column(name = "salt", comment = "加密盐", length = 100)
    private String salt;

    @Column(name = "sys_id", comment = "所属子系统")
    private String sysId;

    /**
     * 内部用户，外部用户等等
     */
    @Column(name = "user_type", comment = "用户类型", length = 100)
    private String userType;

    @Column(name = "last_login_ip", comment = "最后登陆IP", length = 20)
    private String lastLoginIp;
    @Column(name = "last_login_date", comment = "最后登陆时间", type = ColumnType.DATE)
    private long lastLoginDate;

    @Column(name = "last_change_pwd_date", comment = "最后修改密码时间", type = ColumnType.DATE)
    private long lastChangePwdDate;

    @Column(name = "freeze_reason", comment = "冻结原因", length = 1000)
    private String freezeReason;
    @Column(name = "freeze_date", comment = "冻结时间", type = ColumnType.DATE)
    private long freezeDate;

    @Column(name = "invalid_date", comment = "失效时间", type = ColumnType.DATE)
    private long invalidDate;

    @Column(name = "pwd_question1", comment = "密保问题1", length = 500)
    private String pwdQuestion1;
    @Column(name = "pwd_answer1", comment = "密保答案1", length = 1000)
    private String pwdAnswer1;
    @Column(name = "pwd_question2", comment = "密保问题2", length = 500)
    private String pwdQuestion2;
    @Column(name = "pwd_answer2", comment = "密保答案2", length = 1000)
    private String pwdAnswer2;
    @Column(name = "pwd_question3", comment = "密保问题3", length = 500)
    private String pwdQuestion3;
    @Column(name = "pwd_answer3", comment = "密保答案3", length = 1000)
    private String pwdAnswer3;

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public long getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(long lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public long getLastChangePwdDate() {
        return lastChangePwdDate;
    }

    public void setLastChangePwdDate(long lastChangePwdDate) {
        this.lastChangePwdDate = lastChangePwdDate;
    }

    public String getFreezeReason() {
        return freezeReason;
    }

    public void setFreezeReason(String freezeReason) {
        this.freezeReason = freezeReason;
    }

    public long getFreezeDate() {
        return freezeDate;
    }

    public void setFreezeDate(long freezeDate) {
        this.freezeDate = freezeDate;
    }

    public long getInvalidDate() {
        return invalidDate;
    }

    public void setInvalidDate(long invalidDate) {
        this.invalidDate = invalidDate;
    }

    public String getPwdQuestion1() {
        return pwdQuestion1;
    }

    public void setPwdQuestion1(String pwdQuestion1) {
        this.pwdQuestion1 = pwdQuestion1;
    }

    public String getPwdAnswer1() {
        return pwdAnswer1;
    }

    public void setPwdAnswer1(String pwdAnswer1) {
        this.pwdAnswer1 = pwdAnswer1;
    }

    public String getPwdQuestion2() {
        return pwdQuestion2;
    }

    public void setPwdQuestion2(String pwdQuestion2) {
        this.pwdQuestion2 = pwdQuestion2;
    }

    public String getPwdAnswer2() {
        return pwdAnswer2;
    }

    public void setPwdAnswer2(String pwdAnswer2) {
        this.pwdAnswer2 = pwdAnswer2;
    }

    public String getPwdQuestion3() {
        return pwdQuestion3;
    }

    public void setPwdQuestion3(String pwdQuestion3) {
        this.pwdQuestion3 = pwdQuestion3;
    }

    public String getPwdAnswer3() {
        return pwdAnswer3;
    }

    public void setPwdAnswer3(String pwdAnswer3) {
        this.pwdAnswer3 = pwdAnswer3;
    }

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }
}
