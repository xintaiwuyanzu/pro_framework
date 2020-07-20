package com.dr.framework.common.entity;

/**
 * 带有排序字段的实体类
 *
 * @author dr
 */
public interface OrderEntity extends CreateInfoEntity, Comparable<OrderEntity> {
    /**
     * 获取排序
     *
     * @return
     */
    Integer getOrder();

    /**
     * 设置排序
     *
     * @param order
     */
    void setOrder(Integer order);

    /**
     * 实现排序功能
     *
     * @param o
     * @return
     */
    @Override
    default int compareTo(OrderEntity o) {
        int comPareOrder = 0;
        if (o != null) {
            comPareOrder = o.getOrder();
        }
        return getOrder() - comPareOrder;
    }
}
