package com.dr.framework.common.entity;

/**
 * 创建人信息
 *
 * @author dr
 */
public interface CreateInfoEntity extends IdEntity {
    long getCreateDate();

    void setCreateDate(long createDate);

    String getCreatePerson();

    void setCreatePerson(String createPerson);

    long getUpdateDate();

    void setUpdateDate(long updateDate);

    String getUpdatePerson();

    void setUpdatePerson(String updatePerson);
}
