package com.dr.framework.sys.controller;

import com.dr.framework.common.controller.BaseController;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.sys.entity.Person;
import com.dr.framework.sys.entity.PersonInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/person")
public class PersonController extends BaseController<Person> {
    @Override
    protected void onBeforePageQuery(HttpServletRequest request, SqlQuery<Person> sqlQuery, Person entity) {
        super.onBeforePageQuery(request, sqlQuery, entity);
        sqlQuery.orderBy(PersonInfo.ID);
    }
}
