package com.dr.framework.core.process.bo;

import com.dr.framework.core.organise.entity.Person;

import java.util.List;

/**
 * 流程定义基本信息
 *
 * @author dr
 */
public class ProcessDefinition extends AbstractProcessObject {
    /**
     * 类型
     */
    private String type;
    /**
     * 版本
     */
    private Integer version;
    /**
     * 定义的启动人员
     */
    private List<Person> startUser;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Person> getStartUser() {
        return startUser;
    }

    public void setStartUser(List<Person> startUser) {
        this.startUser = startUser;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
