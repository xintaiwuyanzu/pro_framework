package com.dr.framework.common.entity;

public interface OrderEntity extends CreateInfoEntity, Comparable<OrderEntity> {
    public int getOrder();

    public void setOrder(int order);

    @Override
    default int compareTo(OrderEntity o) {
        int comPareOrder = 0;
        if (o != null) {
            comPareOrder = o.getOrder();
        }
        return getOrder() - comPareOrder;
    }
}
