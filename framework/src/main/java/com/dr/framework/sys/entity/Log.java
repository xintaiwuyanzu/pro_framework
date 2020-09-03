package com.dr.framework.sys.entity;

import com.dr.framework.common.entity.BaseCreateInfoEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.util.Constants;


/**
 * 系统操作日志
 *
 * @author dr
 */
@Table(name = Constants.SYS_TABLE_PREFIX + "Log", module = Constants.SYS_MODULE_NAME, comment = "系统操作日H志")
public class Log extends BaseCreateInfoEntity {

    @Column(name = "person", comment = "操作人")
    private String person;
    @Column(name = "logtype", comment = "日志操作类型")
    private String logtype;
    @Column(name = "operated", comment = "被操作主体")
    private String perated;
    @Column(name = "remark", comment = "日志描述")
    private String remark;
    @Column(name = "money", comment = "涉及金额")
    private String money;
    @Column(name = "logresult", comment = "操作结果")
    private String logresult;
    @Column(length = 100, comment = "数据源表名称")
    private String sourceTable;
    @Column(length = 300, comment = "java类名称")
    private String className;
    @Column(name = "log_method", length = 100, comment = "方法名称")
    private String method;
    @Column(length = 500, comment = "冗余字段1")
    private String filed1;
    @Column(length = 1000, comment = "冗余字段2")
    private String filed2;
    @Column(length = 1000, comment = "冗余字段3")
    private String filed3;

    public Log(String person, String logtype, String perated, String remark, String money, String logresult, String sourceTable, String className, String method) {
        this.person = person;
        this.logtype = logtype;
        this.perated = perated;
        this.remark = remark;
        this.money = money;
        this.logresult = logresult;
        this.sourceTable = sourceTable;
        this.className = className;
        this.method = method;
    }

    public Log() {
    }

    public String getLogtype() {
        return logtype;
    }

    public void setLogtype(String logtype) {
        this.logtype = logtype;
    }

    public String getPerated() {
        return perated;
    }

    public void setPerated(String perated) {
        this.perated = perated;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getLogresult() {
        return logresult;
    }

    public void setLogresult(String logresult) {
        this.logresult = logresult;
    }

    public String getSourceTable() {
        return sourceTable;
    }

    public void setSourceTable(String sourceTable) {
        this.sourceTable = sourceTable;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getFiled1() {
        return filed1;
    }

    public void setFiled1(String filed1) {
        this.filed1 = filed1;
    }

    public String getFiled2() {
        return filed2;
    }

    public void setFiled2(String filed2) {
        this.filed2 = filed2;
    }

    public String getFiled3() {
        return filed3;
    }

    public void setFiled3(String filed3) {
        this.filed3 = filed3;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }
}
