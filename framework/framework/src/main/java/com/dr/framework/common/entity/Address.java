package com.dr.framework.common.entity;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "common_address", comment = "常用地址", module = "common")
public class Address extends BaseEntity {
    //TODO 这里要内置几个常用的坐标系变量

    @Column(name = "latitude", comment = "经度", precision = 18, scale = 15)
    private long latitude;
    @Column(name = "longitude", comment = "维度", precision = 18, scale = 15)
    private long longitude;
    @Column(name = "add_name", comment = "地址名称", length = 200)
    private String name;
    @Column(name = "add_description", comment = "地址备注", length = 1000)
    private String description;
    @Column(name = "coordinate_type", comment = "坐标系", length = 100)
    private String coordinateType;

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoordinateType() {
        return coordinateType;
    }

    public void setCoordinateType(String coordinateType) {
        this.coordinateType = coordinateType;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }
}
