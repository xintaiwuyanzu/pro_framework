package com.dr.framework.sys.controller;

import com.dr.framework.common.controller.BaseController;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.sys.entity.SysDict;
import com.dr.framework.sys.entity.SysDictInfo;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/sysDict")
public class SysDictController extends BaseController<SysDict> {
    @Override
    protected void onBeforeInsert(HttpServletRequest request, SysDict entity) {
        super.onBeforeInsert(request, entity);
        if (entity != null && StringUtils.isEmpty(entity.getType()) && !StringUtils.isEmpty(entity.getKey())) {
            entity.setType(entity.getKey().split("\\.")[0]);
        }
    }

    @Override
    protected void onBeforePageQuery(HttpServletRequest request, SqlQuery<SysDict> sqlQuery, SysDict entity) {
        super.onBeforePageQuery(request, sqlQuery, entity);
        sqlQuery.orderBy(SysDictInfo.ID);
        sqlQuery.equalIfNotNull(SysDictInfo.STATUS);
        sqlQuery.like(SysDictInfo.KEYINFO, SysDictInfo.VALUE, SysDictInfo.DESCRIPTION);
    }
}
