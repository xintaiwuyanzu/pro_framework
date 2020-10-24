package com.dr.framework.core.security.bo;

import com.dr.framework.core.security.service.ResourceProvider;

/**
 * 表示本身是资源
 * 可以是菜单，可以是按钮，可以是其他东西
 * 会被{@link ResourceProvider}收集使用
 *
 * @author dr
 */
public interface PermissionResource {
    /**
     * 获取主键
     *
     * @return
     */
    String getId();

    /**
     * 获取资源编码
     *
     * @return
     */
    String getCode();

    /**
     * 获取名称
     *
     * @return
     */
    String getName();

    /**
     * 获取描述
     *
     * @return
     */
    default String getDescription() {
        return "";
    }

    /**
     * 获取父Id
     *
     * @return
     */
    String getParentId();

    /**
     * 获取排序
     *
     * @return
     */
    Integer getOrder();
}
