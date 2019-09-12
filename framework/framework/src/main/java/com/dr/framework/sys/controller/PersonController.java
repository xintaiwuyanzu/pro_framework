package com.dr.framework.sys.controller;

import com.dr.framework.common.controller.BaseController;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.sys.entity.Person;
import com.dr.framework.sys.entity.PersonInfo;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("${common.api-path:/api}/person")
public class PersonController extends BaseController<Person> {
    @Override
    protected void onBeforePageQuery(HttpServletRequest request, SqlQuery<Person> sqlQuery, Person entity) {
        super.onBeforePageQuery(request, sqlQuery, entity);
        if (!StringUtils.isEmpty(entity.getUserName())) {
            sqlQuery.equal(PersonInfo.USERNAME, entity.getUserName());
        }
        if (!StringUtils.isEmpty(entity.getUserCode())) {
            sqlQuery.equal(PersonInfo.USERCODE, entity.getUserCode());
        }
        sqlQuery.orderBy(PersonInfo.ID);
    }
}
