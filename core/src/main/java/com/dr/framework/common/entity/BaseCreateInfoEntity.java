package com.dr.framework.common.entity;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;

/**
 * 基本信息类
 *
 * @author dr
 */
public class BaseCreateInfoEntity extends BaseEntity implements CreateInfoEntity {
    @Column(comment = "创建日期", type = ColumnType.DATE, order = 2)
    private Long createDate;
    @Column(comment = "创建人ID", simple = "创建人", length = 100, order = 3)
    private String createPerson;
    @Column(comment = "更新日期", type = ColumnType.DATE, order = 4)
    private Long updateDate;
    @Column(comment = "更新人ID", simple = "更新人", length = 100, order = 5)
    private String updatePerson;

    @Override
    public Long getCreateDate() {
        return createDate;
    }

    @Override
    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    @Override
    public String getCreatePerson() {
        return createPerson;
    }

    @Override
    public void setCreatePerson(String createPerson) {
        this.createPerson = createPerson;
    }

    @Override
    public Long getUpdateDate() {
        return updateDate;
    }

    @Override
    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String getUpdatePerson() {
        return updatePerson;
    }

    @Override
    public void setUpdatePerson(String updatePerson) {
        this.updatePerson = updatePerson;
    }
}
