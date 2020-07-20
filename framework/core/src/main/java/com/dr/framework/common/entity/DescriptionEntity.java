package com.dr.framework.common.entity;

/**
 * 带有常用描述信息的实体类
 *
 * @param <T>
 * @author dr
 */
public interface DescriptionEntity<T> extends StatusEntity<T> {
    /**
     * 获取名称
     *
     * @return
     */
    String getName();

    /**
     * 设置名称
     *
     * @param name
     */
    void setName(String name);

    /**
     * 编码
     *
     * @return
     */
    String getCode();

    /**
     * 设置编码
     *
     * @param code
     */
    void setCode(String code);

    /**
     * 类型
     *
     * @return
     */
    String getType();

    /**
     * 设置类型
     *
     * @param type
     */
    void setType(String type);

    /**
     * 获取描述信息
     *
     * @return
     */
    String getDescription();

    /**
     * 设置描述信息
     *
     * @param description
     */
    void setDescription(String description);

}
