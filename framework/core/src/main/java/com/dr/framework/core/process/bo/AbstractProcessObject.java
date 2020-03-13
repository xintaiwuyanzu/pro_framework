package com.dr.framework.core.process.bo;

import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 每个流程相关的实体类都有的属性
 *
 * @author dr
 */
public abstract class AbstractProcessObject {
    /**
     * 主键
     */
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;

    /**
     * 定义的初始变量信息
     */
    private List<ProPerty> proPerties;

    public ProPerty getProPerty(String key) {
        if (proPerties != null) {
            if (!StringUtils.isEmpty(key)) {
                for (ProPerty proPerty : getProPerties()) {
                    if (proPerty.getName().equals(key)) {
                        return proPerty;
                    }
                }
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<ProPerty> getProPerties() {
        return proPerties;
    }

    public void setProPerties(List<ProPerty> proPerties) {
        this.proPerties = proPerties;
    }
}

