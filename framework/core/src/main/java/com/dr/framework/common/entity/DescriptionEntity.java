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
     */
    void setName(String name);

    /**
     * 编码
     *
     * @return
     */
    String getCode();


    void setCode(String code);

    /**
     * 类型
     *
     * @return
     */
    String getType();

    void setType(String type);

    /**
     * @return
     */
    String getDescription();

    void setDescription(String description);

}
