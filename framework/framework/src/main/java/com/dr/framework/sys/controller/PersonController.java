package com.dr.framework.sys.controller;

import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.common.page.Page;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.organise.query.PersonQuery;
import com.dr.framework.core.organise.service.OrganisePersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * 系统人员访问页面
 *
 * @author dr
 */
@RestController
@RequestMapping("${common.api-path:/api}/person")
public class PersonController {
    @Autowired
    OrganisePersonService organisePersonService;

    /**
     * 添加人员
     *
     * @param person
     * @param registerLogin
     * @param organiseId
     * @return
     */
    @RequestMapping("/insert")
    public ResultEntity insert(
            Person person,
            @RequestParam(defaultValue = "false") boolean registerLogin,
            String password,
            String organiseId) {
        if (StringUtils.isEmpty(person.getId()))
            person.setId(UUID.randomUUID().toString());
        organisePersonService.addPerson(person, organiseId, registerLogin, password);
        return ResultEntity.success(person);
    }

    @RequestMapping("/detail")
    public ResultEntity<Person> detail(String id) {
        return ResultEntity.success(organisePersonService.getPerson(new PersonQuery.Builder().idEqual(id).build()));
    }

    @RequestMapping("/delete")
    public ResultEntity<Person> delete(String id) {
        return ResultEntity.success(organisePersonService.deletePerson(id));
    }

    @RequestMapping("/update")
    public ResultEntity<Person> update(Person person) {
        organisePersonService.updatePerson(person);
        return ResultEntity.success("更新成功");
    }


    /**
     * 分页查询语句
     *
     * @param person
     * @param pageIndex
     * @param pageSize
     * @param page
     * @return
     */
    @RequestMapping("/page")
    public ResultEntity page(Person person,
                             @RequestParam(defaultValue = "0") int pageIndex,
                             @RequestParam(defaultValue = Page.DEFAULT_PAGE_SIZE + "") int pageSize,
                             @RequestParam(defaultValue = "true") boolean page) {
        PersonQuery personQuery = new PersonQuery.Builder()
                .nameLike(person.getUserName())
                .typeLike(person.getPersonType())
                .userCodeLike(person.getUserCode())
                .statusEqual(person.getStatus())
                .organiseIdEqual(person.getDefaultOrganiseId())
                .build();
        if (page) {
            return ResultEntity.success(
                    organisePersonService.getPersonPage(
                            personQuery
                            , pageSize * pageIndex
                            , (pageIndex + 1) * pageSize)
            );
        } else {
            return ResultEntity.success(
                    organisePersonService.getPersonList(personQuery)
            );
        }
    }

}
