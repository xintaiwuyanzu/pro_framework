package com.dr.framework.core.security.bo;

import com.dr.framework.core.security.service.ResourceProvider;

/**
 * 用来给前端显示使用
 *
 * @author dr
 */
public class ResourceProviderInfo {

    private String type, name, description;

    public ResourceProviderInfo(ResourceProvider resourceProvider) {
        setType(resourceProvider.getType());
        setDescription(resourceProvider.getDescription());
        setName(resourceProvider.getName());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
