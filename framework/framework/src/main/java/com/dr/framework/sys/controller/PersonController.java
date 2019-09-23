package com.dr.framework.sys.controller;

import com.dr.framework.common.controller.BaseController;
import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.organise.query.PersonQuery;
import com.dr.framework.core.organise.service.SysOrganisePersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 系统人员访问页面
 *
 * @author dr
 */
@RestController
@RequestMapping("${common.api-path:/api}/person")
public class PersonController extends BaseController<Person> {
    @Autowired
    SysOrganisePersonService sysOrganisePersonService;

    /**
     * 分页查询语句
     *
     * @param request
     * @param entity
     * @param pageIndex
     * @param pageSize
     * @param page
     * @return
     */
    @Override
    public ResultEntity page(HttpServletRequest request, Person entity, int pageIndex, int pageSize, boolean page) {
        PersonQuery personQuery = new PersonQuery.Builder()
                .nameLike(entity.getUserName())
                .build();
        if (page) {
            return ResultEntity.success(
                    sysOrganisePersonService.getPersonPage(
                            personQuery
                            , pageSize * pageIndex
                            , (pageIndex + 1) * pageSize)
            );
        } else {
            return ResultEntity.success(
                    sysOrganisePersonService.getPersonList(personQuery)
            );
        }
    }
}
