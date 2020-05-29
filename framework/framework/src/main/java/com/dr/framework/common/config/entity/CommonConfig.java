package com.dr.framework.common.config.entity;

import com.dr.framework.common.config.service.CommonConfigService;
import com.dr.framework.common.entity.BaseDescriptionEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.util.Constants;


/**
 * 通用配置表
 *
 * @author dr
 */
@Table(
        name = CommonConfigService.TableName,
        module = Constants.COMMON_MODULE_NAME,
        comment = "通用配置表"
)
public class CommonConfig extends BaseDescriptionEntity<String> {
    /**
     * =========================
     * 业务外键相关参数
     * =========================
     */
    @Column(comment = "业务外键", length = 100)
    private String refId;
    @Column(comment = "类型业务外键", length = 100)
    private String refType;

    /**
     * ==========================
     * 树结构相关属性
     * ==========================
     */
    @Column(comment = "父id", length = 100)
    private String parentId;
    @Column(comment = "根id", length = 100)
    private String rootId;

    /**
     * =========================
     * 值相关属性
     * =========================
     */
    @Column(name = "pValue", comment = "数据值", length = 500)
    private String value;
    @Column(comment = "第二项数据值", length = 500)
    private String value1;
    @Column(comment = "第三项数据值", length = 500)
    private String value2;
    @Column(comment = "第四项数据值", type = ColumnType.CLOB)
    private String value3;

    @Column(length = 10)
    private Long longValue;
    @Column(length = 10)
    private Long longValue1;
    @Column(length = 10)
    private Long longValue2;


    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public String getValue3() {
        return value3;
    }

    public void setValue3(String value3) {
        this.value3 = value3;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public Long getLongValue1() {
        return longValue1;
    }

    public void setLongValue1(Long longValue1) {
        this.longValue1 = longValue1;
    }

    public Long getLongValue2() {
        return longValue2;
    }

    public void setLongValue2(Long longValue2) {
        this.longValue2 = longValue2;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getRootId() {
        return rootId;
    }

    public void setRootId(String rootId) {
        this.rootId = rootId;
    }
}
