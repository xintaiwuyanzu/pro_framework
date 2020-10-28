package com.dr.framework.sys.controller;

import com.dr.framework.common.controller.BaseServiceController;
import com.dr.framework.common.entity.OrderEntity;
import com.dr.framework.core.orm.module.EntityRelation;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.security.entity.SubSystem;
import com.dr.framework.sys.service.SubSysService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 子系统管理
 *
 * @author dr
 */
@RestController
@RequestMapping("${common.api-path:/api}/subsys")
public class SubSysController extends BaseServiceController<SubSysService, SubSystem> {

    @Override
    protected SqlQuery<SubSystem> buildPageQuery(HttpServletRequest request, SubSystem entity) {
        EntityRelation entityRelation = service.getEntityRelation();
        SqlQuery<SubSystem> sqlQuery = SqlQuery.from(entityRelation);
        sqlQuery.like(entityRelation.getColumn("sysName"), entity.getSysName())
                .orderBy(entityRelation.getColumn(OrderEntity.ORDER_COLUMN_NAME));
        return sqlQuery;
    }
}
