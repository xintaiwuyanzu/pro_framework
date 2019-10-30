package com.dr.framework.common.entity;

/**
 * 带有排序字段的实体类
 *
 * @author dr
 */
public interface OrderEntity extends CreateInfoEntity, Comparable<OrderEntity> {
    int getOrder();

    void setOrder(int order);

    @Override
    default int compareTo(OrderEntity o) {
        int comPareOrder = 0;
        if (o != null) {
            comPareOrder = o.getOrder();
        }
        return getOrder() - comPareOrder;
    }
}
