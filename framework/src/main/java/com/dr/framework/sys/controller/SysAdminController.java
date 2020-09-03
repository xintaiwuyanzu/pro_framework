package com.dr.framework.sys.controller;


import com.dr.framework.common.controller.BaseController;
import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.organise.query.PersonQuery;
import com.dr.framework.core.organise.service.OrganisePersonService;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.web.annotations.Current;
import com.dr.framework.sys.entity.Log;
import com.dr.framework.sys.service.SysAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

/**
 * 管理员Controller
 *
 * @author dr
 */
@RestController
@RequestMapping("/api/sysadmin")
public class SysAdminController extends BaseController<Person> {

    @Autowired
    LogController logAdmin;
    @Autowired
    OrganisePersonService organisePersonService;
    @Autowired
    SysAdminService adminService;

    /**
     * 管理员修改密码
     *
     * @param oldPwd
     * @param newPwd
     * @return
     */
    @PostMapping("/changePassword")
    public ResultEntity<String> changePassword(@Current Person person, HttpServletRequest request, String oldPwd, String newPwd) {
        adminService.changePassword(person.getId(), oldPwd, newPwd);
        ResultEntity res = ResultEntity.success();
        Log log = new Log(person.getUserCode(), "管理员修改密码", person.getUserCode(), person.getUserName() + "修改密码", "-", res.getCode(), Person.PERSON_TABLE_NAME, "Person", "changePassword");
        log.setCreateDate(System.currentTimeMillis());
        log.setCreatePerson(person.getUserCode());
        log.setId(UUID.randomUUID().toString());
        logAdmin.insert(request, log);
        return res;
    }

    /**
     * 查询管理员name
     *
     * @param adminId
     * @return
     */
    @PostMapping("/getAdminById")
    public ResultEntity getAdminById(String adminId) {
        return ResultEntity.success(adminService.getAdminById(adminId));
    }

    /**
     * 根据机构id获取默认人员
     *
     * @param sysId
     * @return
     */
    @PostMapping("/getPerson")
    public ResultEntity getPerson(String sysId) {
        List<Person> list = adminService.getPersonByLibId(sysId);
        return ResultEntity.success(list);
    }

    /**
     * 新增管理员
     *
     * @param person
     * @param admin
     * @return
     */
    @PostMapping("/addAdmin")
    public ResultEntity addAdmin(@Current Person person, HttpServletRequest request, Person admin, String password, String libId) {
        adminService.addAdmin(person.getId(), admin, password, libId);
        ResultEntity res = ResultEntity.success();
        Log log = new Log(person.getUserCode(), "新增管理员", admin.getUserCode(), person.getUserName() + "新增管理员:" + admin.getUserName(), "-", res.getCode(), SqlQuery.getTableInfo(Person.class).table(), "Person", "addAdmin");
        log.setCreateDate(System.currentTimeMillis());
        log.setCreatePerson(person.getUserCode());
        log.setId(UUID.randomUUID().toString());
        logAdmin.insert(request, log);
        return res;
    }

    /**
     * 更新管理员
     *
     * @param admin
     */
    @PostMapping("/updateAdmin")
    public ResultEntity updateAdmin(@Current Person person, HttpServletRequest request, Person admin) {
        adminService.updateAdmin(person.getId(), admin);
        return ResultEntity.success();
    }

    @PostMapping("/getPersonOne")
    public ResultEntity getPersonOne(@Current Person person) {
        Person person1 = organisePersonService.getPerson(new PersonQuery.Builder().idEqual(person.getId()).build());
        return ResultEntity.success(person1);
    }

}
