package com.dr.entity.ywg;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

import java.util.Date;

@Table(name = "PublishInfo", comment = "PublishInfo", module = "yuanwanggu")
public class PublishInfo {
    @Id
    @Column(name = "nPublishID", length = 10, scale = 10, jdbcType = 4, order = 1)
    private Integer nPublishID;
    @Column(name = "szPublishName", type = ColumnType.VARCHAR, length = 200, scale = 10, jdbcType = 12, order = 2)
    private String szPublishName;
    @Column(name = "nStaffID", length = 10, scale = 10, jdbcType = 4, order = 3)
    private int nStaffID;
    @Column(name = "szStaffName", type = ColumnType.VARCHAR, length = 50, scale = 10, jdbcType = 12, order = 4)
    private String szStaffName;
    @Column(name = "dtAddDate", type = ColumnType.DATE, length = 3, scale = 10, jdbcType = 93, order = 5)
    private Date dtAddDate;

    public Integer getNPublishID() {
        return nPublishID;
    }

    public void setNPublishID(Integer nPublishID) {
        this.nPublishID = nPublishID;
    }

    public String getSzPublishName() {
        return szPublishName;
    }

    public void setSzPublishName(String szPublishName) {
        this.szPublishName = szPublishName;
    }

    public int getNStaffID() {
        return nStaffID;
    }

    public void setNStaffID(int nStaffID) {
        this.nStaffID = nStaffID;
    }

    public String getSzStaffName() {
        return szStaffName;
    }

    public void setSzStaffName(String szStaffName) {
        this.szStaffName = szStaffName;
    }

    public Date getDtAddDate() {
        return dtAddDate;
    }

    public void setDtAddDate(Date dtAddDate) {
        this.dtAddDate = dtAddDate;
    }

}
