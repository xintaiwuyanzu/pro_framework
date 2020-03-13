package com.dr.framework.core.process.bo;

/**
 * 流程环节添加的评价
 *
 * @author dr
 */
public class Comment {
    /**
     * 主键
     */
    private String id;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 创建人名称
     */
    private String createUserName;
    /**
     * 创建时间
     */
    private long createDate;
    /**
     * 环节实例Id
     */
    private String taskId;
    /**
     * 流程实例Id
     */
    private String processInstanceId;
    /**
     * 消息
     */
    private String message;

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
