package com.dr.framework.core.process.bo;

import com.dr.framework.core.organise.entity.Person;

import java.util.List;

/**
 * 流程定义对象
 */
public class ProcessObject {
    /**
     * 主键
     */
    private String id;
    /**
     * 编码
     */
    private String key;
    /**
     * 版本
     */
    private Integer version;

    /**
     * 名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;

    private List<Person> startUser;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Person> getStartUser() {
        return startUser;
    }

    public void setStartUser(List<Person> startUser) {
        this.startUser = startUser;
    }
}
