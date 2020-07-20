package com.dr.framework.common.entity;

/**
 * 创建人信息
 *
 * @author dr
 */
public interface CreateInfoEntity extends IdEntity {
    /**
     * 获取创建时间
     *
     * @return
     */
    Long getCreateDate();

    /**
     * 设置创建时间
     *
     * @param createDate
     */
    void setCreateDate(Long createDate);

    /**
     * 获取创建人ID
     *
     * @return
     */
    String getCreatePerson();

    /**
     * 设置创建人id
     *
     * @param createPerson
     */
    void setCreatePerson(String createPerson);

    /**
     * 获取更新日期
     *
     * @return
     */
    Long getUpdateDate();

    /**
     * 设置更新日期
     *
     * @param updateDate
     */
    void setUpdateDate(Long updateDate);

    /**
     * 获取更新人id
     *
     * @return
     */
    String getUpdatePerson();

    /**
     * 设置更新人id
     *
     * @param updatePerson
     */
    void setUpdatePerson(String updatePerson);
}
