package com.dr.framework.core.organise.entity;

import com.dr.framework.common.entity.BaseTreeEntity;
import com.dr.framework.common.entity.SourceRefEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.core.util.Constants;

/**
 * 组织机构
 *
 * @author dr
 */
@Table(name = Organise.ORGANISE_TABLE_NAME
        , comment = "组织机构"
        , module = Constants.SYS_MODULE_NAME
        , genInfo = false)
public class Organise extends BaseTreeEntity<String> implements SourceRefEntity {
    public static final String DEFAULT_ROOT_ID = "root";
    public static final String ORGANISE_TABLE_NAME = Constants.SYS_TABLE_PREFIX + "organise";
    /**
     * ============
     * 默认机构类型支持四种
     * <p>
     * 组织是最上级的机构类型，下级可以是其他三种任何类型
     * <p>
     * 机构是中间的机构类型，下级可以是其他三种任何类型，支持树状结构
     * <p>
     * 部门是最下级的机构类型，下级可以是部门或者虚拟机构
     * <p>
     * 虚拟机构是一个概念性的东西，可以在任何一个级别
     * <p>
     * 上述描述只是业务上的默认规范，代码中不做强关联和校验。
     * 开发人员需要理解并熟记上述默认规范，为开发时做默认指导。
     * ============
     */
    public static final String ORGANISE_TYPE_组织 = "10";
    public static final String ORGANISE_TYPE_机构 = "20";
    public static final String ORGANISE_TYPE_部门 = "30";
    public static final String ORGANISE_TYPE_虚拟 = "90";


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
    @Column(name = "summary", comment = "简介", length = 4000)
    private String summary;
    @Column(name = "source_ref", comment = "数据来源", length = 100)
    private String sourceRef;
    @Column(name = "latitude", comment = "经度", precision = 18, scale = 15)
    private double latitude;
    @Column(name = "longitude", comment = "纬度", precision = 18, scale = 15)
    private double longitude;
    @Column(name = "coordinate_type", comment = "坐标系", length = 100)
    private String coordinateType;

    /**
     * 系统逻辑支持多组组织结构树
     * ，每组组织结构树按照groupid进行区分
     * ，默认的groupid为default
     */
    @Column(name = "group_id", comment = "所属分组", length = 50)
    private String groupId;

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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCoordinateType() {
        return coordinateType;
    }

    public void setCoordinateType(String coordinateType) {
        this.coordinateType = coordinateType;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
