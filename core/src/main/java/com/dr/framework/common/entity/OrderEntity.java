package com.dr.framework.common.entity;

import javax.annotation.Nullable;

/**
 * 带有排序字段的实体类
 *
 * @author dr
 */
public interface OrderEntity extends CreateInfoEntity, Comparable<OrderEntity> {
    String ORDER_COLUMN_NAME = "order_info";

    /**
     * 获取排序
     *
     * @return 返回排序
     */
    Integer getOrder();

    /**
     * 设置排序
     *
     * @param order 排序，可以为空
     */
    void setOrder(Integer order);

    /**
     * 实现排序功能
     *
     * @param o 要对比的对象
     * @return
     */
    @Override
    default int compareTo(@Nullable OrderEntity o) {
        int comPareOrder = 0;
        if (o != null) {
            comPareOrder = o.getOrder();
        }
        return (getOrder() == null ? 0 : getOrder()) - comPareOrder;
    }
}
