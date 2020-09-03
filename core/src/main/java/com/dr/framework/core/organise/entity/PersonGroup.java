package com.dr.framework.core.organise.entity;

import com.dr.framework.common.entity.BaseStatusEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.core.util.Constants;

/**
 * 人员组基本信息
 *
 * @author dr
 */
@Table(name = Constants.SYS_TABLE_PREFIX + "person_group"
        , comment = "人员组关联表"
        , module = Constants.SYS_MODULE_NAME
        , genInfo = false)
public class PersonGroup extends BaseStatusEntity<String> {
    @Column(name = "group_name", comment = "组名称", length = 500)
    private String name;
    @Column(name = "group_detail", comment = "组详情", length = 2000)
    private String detail;
    @Column(name = "group_type", comment = "组类型", length = 50)
    private String type;
    @Column(name = "group_ref_id", comment = "组外键", length = 50)
    private String refId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }
}
