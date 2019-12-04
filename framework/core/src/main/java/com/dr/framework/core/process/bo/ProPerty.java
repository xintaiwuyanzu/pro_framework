package com.dr.framework.core.process.bo;

/**
 * 定义的扩展属性
 *
 * @author dr
 */
public class ProPerty {
    private String id;
    private String name;
    /**
     * TODO value可以使用表达式
     */
    private String value;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
