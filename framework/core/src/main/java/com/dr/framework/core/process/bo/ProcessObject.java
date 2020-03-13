package com.dr.framework.core.process.bo;

import java.util.Map;

/**
 * 流程定义对象
 *
 * @author dr
 */
public class ProcessObject extends ProcessDefinition {
    /**
     * 流程创建时间
     */
    private long createDate;
    /**
     * 流程结束时间
     */
    private long endDate;
    /**
     * 流程创建人
     */
    private String createPerson;
    /**
     * 流程创建人名称
     */
    private String createPersonName;
    /**
     * 流程定义ID
     */
    private String processDefineId;
    /**
     * 流程是否暂停
     */
    private boolean suspend;

    /**
     * 流程运行时的环境变量
     */
    private Map<String, Object> variables;

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public String getProcessDefineId() {
        return processDefineId;
    }

    public void setProcessDefineId(String processDefineId) {
        this.processDefineId = processDefineId;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getCreatePerson() {
        return createPerson;
    }

    public void setCreatePerson(String createPerson) {
        this.createPerson = createPerson;
    }

    public String getCreatePersonName() {
        return createPersonName;
    }

    public void setCreatePersonName(String createPersonName) {
        this.createPersonName = createPersonName;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public boolean isSuspend() {
        return suspend;
    }

    public void setSuspend(boolean suspend) {
        this.suspend = suspend;
    }
}
