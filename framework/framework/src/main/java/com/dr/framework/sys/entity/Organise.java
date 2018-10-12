package com.dr.framework.sys.entity;

import com.dr.framework.common.entity.BaseTreeEntity;
import com.dr.framework.common.entity.SourceRefEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "sys_organise", comment = "组织机构", module = "sys")
public class Organise extends BaseTreeEntity<String> implements SourceRefEntity {
    @Column(name = "organise_name", comment = "机构名称", length = 100)
    private String organiseName;
    @Column(name = "organise_old_name", comment = "原机构名称", length = 100)
    private String organiseOldName;
    @Column(name = "organise_type", comment = "机构类型", length = 50)
    private String organiseType;
    @Column(name = "organise_code", comment = "机构代码", length = 50)
    private String organiseCode;
    @Column(name = "phone", comment = "联系电话", length = 100)
    private String phone;
    @Column(name = "mobile", comment = "联系手机", length = 100)
    private String mobile;
    @Column(name = "concat_name", comment = "联系人名称", length = 100)
    private String concatName;
    @Column(name = "address", comment = "办公地点", length = 200)
    private String address;
    @Column(name = "address_id", comment = "地点id", length = 50)
    private String addressId;
    @Column(name = "source_ref", comment = "数据来源", length = 100)
    private String sourceRef;

    public String getOrganiseName() {
        return organiseName;
    }

    public void setOrganiseName(String organiseName) {
        this.organiseName = organiseName;
    }

    public String getOrganiseOldName() {
        return organiseOldName;
    }

    public void setOrganiseOldName(String organiseOldName) {
        this.organiseOldName = organiseOldName;
    }

    public String getOrganiseType() {
        return organiseType;
    }

    public void setOrganiseType(String organiseType) {
        this.organiseType = organiseType;
    }

    public String getOrganiseCode() {
        return organiseCode;
    }

    public void setOrganiseCode(String organiseCode) {
        this.organiseCode = organiseCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getConcatName() {
        return concatName;
    }

    public void setConcatName(String concatName) {
        this.concatName = concatName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    @Override
    public String getSourceRef() {
        return sourceRef;
    }

    @Override
    public void setSourceRef(String sourceRef) {
        this.sourceRef = sourceRef;
    }
}
