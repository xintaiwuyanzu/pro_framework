package com.dr.framework.common.entity;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.util.Constants;

/**
 * 通用日志
 *
 * @author dr
 */
@Table(
        name = Constants.SYS_TABLE_PREFIX + "COMMON_LOG"
        , module = Constants.COMMON_MODULE_NAME
        , comment = "通用日志"
)
public class CommonLog extends BaseCreateInfoEntity {
    @Column(length = 200, comment = "模块名称")
    private String moduleName;
    @Column(length = 100, comment = "数据源id")
    private String sourceId;
    @Column(length = 100, comment = "数据源表名称")
    private String sourceTable;

    @Column(length = 300, comment = "java类名称")
    private String className;
    @Column(name = "log_method", length = 100, comment = "方法名称")
    private String method;

    @Column(name = "log_msg", length = 500, comment = "日志描述")
    private String message;
    @Column(length = 2000, comment = "日志详细描述")
    private String detail;
    @Column(name = "log_status", length = 100, comment = "状态描述")
    private String status;
    @Column(length = 200, comment = "操作类型")
    private String logType;

    @Column(length = 500, comment = "冗余字段1")
    private String filed1;
    @Column(length = 1000, comment = "冗余字段2")
    private String filed2;
    @Column(length = 1000, comment = "冗余字段3")
    private String filed3;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }
}
