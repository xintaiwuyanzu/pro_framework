package com.dr.framework.sys.controller;

import com.dr.framework.common.controller.BaseServiceController;
import com.dr.framework.core.orm.module.EntityRelation;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.security.entity.Permission;
import com.dr.framework.sys.service.PermissionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.dr.framework.common.entity.OrderEntity.ORDER_COLUMN_NAME;

/**
 * 权限管理controller
 *
 * @author dr
 */
@RestController
@RequestMapping("${common.api-path:/api}/sysPermission")
public class SysPermissionController extends BaseServiceController<PermissionService, Permission> {
    @Override
    protected SqlQuery<Permission> buildPageQuery(HttpServletRequest request, Permission entity) {
        EntityRelation relation = service.getEntityRelation();
        SqlQuery<Permission> sqlQuery = SqlQuery.from(relation);
        sqlQuery.like(relation.getColumn("name"), entity.getName())
                .orderBy(
                        relation.getColumn(ORDER_COLUMN_NAME),
                        relation.getColumn(ORDER_COLUMN_NAME)
                );
        return sqlQuery;
    }
}
