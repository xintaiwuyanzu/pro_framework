package com.dr.framework.core.process.bo;

import com.dr.framework.core.organise.entity.Person;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

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
    /**
     * 流程上定义的扩展属性
     */
    private List<ProPerty> proPerties;
    /**
     * 流程运行时的环境变量
     */
    private Map<String, Object> variables;

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

    public List<ProPerty> getProPerties() {
        return proPerties;
    }

    public void setProPerties(List<ProPerty> proPerties) {
        this.proPerties = proPerties;
    }

    public ProPerty getProPerty(String key) {
        if (!StringUtils.isEmpty(key)) {
            for (ProPerty proPerty : proPerties) {
                if (proPerty.getName().equals(key)) {
                    return proPerty;
                }
            }
        }

        return null;
    }


    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}
