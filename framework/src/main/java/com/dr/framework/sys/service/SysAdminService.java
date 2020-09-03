package com.dr.framework.sys.service;

import com.dr.framework.common.dao.CommonMapper;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.organise.query.PersonQuery;
import com.dr.framework.core.organise.service.LoginService;
import com.dr.framework.core.organise.service.OrganisePersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class SysAdminService {

    @Autowired
    LoginService loginService;
    @Autowired
    CommonMapper commonMapper;
    @Autowired
    OrganisePersonService organisePersonService;

    /**
     * 管理员修改密码
     */
    public void changePassword(String personId, String oldPwd, String newPwd) {
        Assert.isTrue(!StringUtils.isEmpty(personId), "管理员记录号不能为空！");
        Assert.isTrue(!StringUtils.isEmpty(oldPwd), "管理员密码不能为空！");
        Assert.isTrue(!StringUtils.isEmpty(newPwd), "新密码不能为空！");
        Person person = organisePersonService.getPerson(new PersonQuery.Builder().idEqual(personId).build());
        Assert.notNull(person, "该管理员不存在");
        loginService.login(person.getUserCode(), oldPwd);
        loginService.changePassword(personId, newPwd);
    }

    /**
     * 查询管理员
     *
     * @param adminId
     * @return
     */
    public Person getAdminById(String adminId) {
        return organisePersonService.getPerson(new PersonQuery.Builder().
                userCodeLike(adminId).
                build()
        );
    }

    /**
     * 根据机构id获取默认人员
     *
     * @param sysId
     * @return
     */
    public List<Person> getPersonByLibId(String sysId) {
        return organisePersonService.getOrganiseDefaultPersons(sysId);
    }

    /**
     * 新增管理员
     *
     * @param personId 创建人id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void addAdmin(String personId, Person admin, String password, String libId) {
        admin.setId(UUID.randomUUID().toString());
        admin.setPersonType("admin");
        admin.setCreateDate(System.currentTimeMillis());
        admin.setCreatePerson(personId);
        organisePersonService.addPerson(admin, libId, true, password);
    }

    /**
     * 更新管理员
     * TODO 这里的userCode不能修改
     *
     * @param personId
     * @param admin
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAdmin(String personId, Person admin) {
        organisePersonService.updatePerson(admin);
        admin.setUpdatePerson(personId);
        admin.setUpdateDate(System.currentTimeMillis());
        commonMapper.updateIgnoreNullById(admin);
    }

}
