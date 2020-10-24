package com.dr.framework.core.security.bo;

/**
 * 权限资源功能
 *
 * @author dr
 */
public class PermissionResourcePart {
    /**
     * 编码
     */
    private String code;
    /**
     * 中文名称
     */
    private String label;
    /**
     * 描述
     */
    private String description;

    public PermissionResourcePart(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
