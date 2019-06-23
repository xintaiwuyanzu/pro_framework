package com.dr.entity.ywg;

import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

import java.util.List;

/**
 * @deprecated 这张表不对，员工表在ilas数据库中ilasdic
 */
@Deprecated
@Table(name = "duStaff", comment = "工作人员登录账号信息", module = "yuanwanggu")
public class Sysperson implements IdEntity {
    @Id
    @Column(name = "nStaffID", length = 10, scale = 10, jdbcType = 4, order = 1)
    private int nStaffID;
    @Column(name = "szName", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 2)
    private String szName;
    @Column(name = "szUserName", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 3)
    private String szUserName;
    @Column(name = "szPassword", type = ColumnType.VARCHAR, length = 100, scale = 10, jdbcType = 12, order = 4)
    private String szPassword;
    @Column(name = "bIsValid", type = ColumnType.BOOLEAN, length = 1, scale = 10, jdbcType = -7, order = 5)
    private boolean bIsValid;
    @Column(name = "bSex", type = ColumnType.BOOLEAN, length = 1, scale = 10, jdbcType = -7, order = 6)
    private boolean bSex;
    @Column(name = "szTelephone", type = ColumnType.VARCHAR, length = 20, scale = 10, jdbcType = 12, order = 7)
    private String szTelephone;
    @Column(name = "szAddress", type = ColumnType.VARCHAR, length = 100, scale = 10, jdbcType = 12, order = 8)
    private String szAddress;
    @Column(name = "bIsAdmin", type = ColumnType.BOOLEAN, length = 1, scale = 10, jdbcType = -7, order = 9)
    private boolean bIsAdmin;
    @Column(name = "nDeptID", length = 10, scale = 10, jdbcType = 4, order = 10)
    private int nDeptID;
    @Column(name = "szFinger", type = ColumnType.VARCHAR, length = 16, scale = 10, jdbcType = -1, order = 11)
    private String szFinger;
    @Column(name = "szMobile", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 12)
    private String szMobile;
    @Column(name = "szEMail", type = ColumnType.VARCHAR, length = 100, scale = 10, jdbcType = 12, order = 13)
    private String szEMail;

    /**
     * 解析ilasdic为sysperson
     * <p>
     * y李育桐zLYT  p890280
     *
     * @param dicList
     * @return
     */
    public static Sysperson parseDicList(List<String> dicList) {
        Sysperson sysperson = new Sysperson();
        for (String string : dicList) {
            if (string.startsWith("y")) {
                sysperson.setSzUserName(string.substring(1));
            } else if (string.startsWith("z")) {
                sysperson.setSzName(string.substring(1));
            } else if (string.startsWith("p")) {
                sysperson.setSzPassword(string.substring(1));
            }
        }
        return sysperson;
    }


    public int getNStaffID() {
        return nStaffID;
    }

    public void setNStaffID(int nStaffID) {
        this.nStaffID = nStaffID;
    }

    public String getSzName() {
        return szName;
    }

    public void setSzName(String szName) {
        this.szName = szName;
    }

    public String getSzUserName() {
        return szUserName;
    }

    public void setSzUserName(String szUserName) {
        this.szUserName = szUserName;
    }

    public String getSzPassword() {
        return szPassword;
    }

    public void setSzPassword(String szPassword) {
        this.szPassword = szPassword;
    }

    public boolean isBIsValid() {
        return bIsValid;
    }

    public void setBIsValid(boolean bIsValid) {
        this.bIsValid = bIsValid;
    }

    public boolean isBSex() {
        return bSex;
    }

    public void setBSex(boolean bSex) {
        this.bSex = bSex;
    }

    public String getSzTelephone() {
        return szTelephone;
    }

    public void setSzTelephone(String szTelephone) {
        this.szTelephone = szTelephone;
    }

    public String getSzAddress() {
        return szAddress;
    }

    public void setSzAddress(String szAddress) {
        this.szAddress = szAddress;
    }

    public boolean isBIsAdmin() {
        return bIsAdmin;
    }

    public void setBIsAdmin(boolean bIsAdmin) {
        this.bIsAdmin = bIsAdmin;
    }

    public int getNDeptID() {
        return nDeptID;
    }

    public void setNDeptID(int nDeptID) {
        this.nDeptID = nDeptID;
    }

    public String getSzFinger() {
        return szFinger;
    }

    public void setSzFinger(String szFinger) {
        this.szFinger = szFinger;
    }

    public String getSzMobile() {
        return szMobile;
    }

    public void setSzMobile(String szMobile) {
        this.szMobile = szMobile;
    }

    public String getSzEMail() {
        return szEMail;
    }

    public void setSzEMail(String szEMail) {
        this.szEMail = szEMail;
    }

    @Override
    public String getId() {
        return String.valueOf(nStaffID);
    }

    @Override
    public void setId(String s) {
        this.nStaffID = Integer.parseInt(s);
    }
}
