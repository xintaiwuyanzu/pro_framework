package com.dr.framework.core.organise.service;

import com.dr.framework.common.entity.TreeNode;
import com.dr.framework.common.page.Page;
import com.dr.framework.core.organise.entity.Organise;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.organise.entity.PersonGroup;
import com.dr.framework.core.organise.query.OrganiseQuery;
import com.dr.framework.core.organise.query.PersonQuery;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * 组织机构相关的service
 *
 * @author dr
 */
public interface OrganisePersonService {
    /**
     * 根据机构id获取一直到根的父机构数据
     *
     * @param organiseId
     * @param organiseType 要查询的机构类型，为空则查询所有的机构类型
     * @return
     */
    List<Organise> getParentOrganiseList(String organiseId, String... organiseType);

    /**
     * 根据指定的机构id查询所有的子机构
     *
     * @param organiseId
     * @param organiseType 要查询的机构类型，为空则查询所有的机构类型
     * @return
     */
    List<Organise> getChildrenOrganiseList(String organiseId, String... organiseType);

    /**
     * 根据query查询机构数据
     *
     * @param organiseQuery
     * @return
     */
    List<Organise> getOrganiseList(OrganiseQuery organiseQuery);

    /**
     * 获取机构数量
     *
     * @param organiseQuery
     * @return
     */
    long getOrganiseCount(OrganiseQuery organiseQuery);

    /**
     * 根据
     *
     * @param organiseQuery
     * @param start
     * @param end
     * @return
     */
    Page<Organise> getOrganisePage(OrganiseQuery organiseQuery, int start, int end);

    /**
     * 获取一个机构
     *
     * @param organiseQuery
     * @return
     */
    default @Nullable
    Organise getOrganise(OrganiseQuery organiseQuery) {
        List<Organise> organises = getOrganiseList(organiseQuery);
        Assert.isTrue(organises.size() <= 1, "查询到多条数据");
        return organises.isEmpty() ? null : organises.get(0);
    }

    /**
     * 获取人员所属机构树
     *
     * @param personId
     * @return
     */
    default List<Organise> getPersonOrganises(@Nonnull String personId) {
        return getOrganiseList(new OrganiseQuery.Builder()
                .personIdEqual(personId)
                .build());
    }

    /**
     * 获取指定人员默认所属的机构
     *
     * @param personId
     * @return
     */
    default Organise getPersonDefaultOrganise(@Nonnull String personId) {
        return getOrganise(new OrganiseQuery.Builder()
                .defaultPersonIdEqual(personId)
                .build());
    }

    /**
     * 根据查询条件查询人员
     *
     * @param query
     * @return
     */
    List<Person> getPersonList(PersonQuery query);

    /**
     * 根据查询条件查询人员分页数据
     *
     * @param query
     * @param start
     * @param end
     * @return
     */
    Page<Person> getPersonPage(PersonQuery query, int start, int end);

    /**
     * 查询人员数量
     *
     * @param query
     * @return
     */
    long getPersonCount(PersonQuery query);

    /**
     * 查询指定的一条人员数据
     *
     * @param query
     * @return
     */
    default @Nullable
    Person getPerson(PersonQuery query) {
        List<Person> personList = getPersonList(query);
        Assert.isTrue(personList.size() < 2, "查询到多条数据");
        return personList.isEmpty() ? null : personList.get(0);
    }

    /**
     * 根据用户id查询用户
     *
     * @param id
     * @return
     */
    default @Nullable
    Person getPersonById(String id) {
        Assert.isTrue(StringUtils.hasText(id), "用户id不能为空");
        return getPerson(new PersonQuery.Builder().idEqual(id).build());
    }

    /**
     * 根据code查询用户，模糊查询
     *
     * @param userCode
     * @return
     */
    default @Nullable
    Person getPersonByUserCode(String userCode) {
        Assert.isTrue(StringUtils.hasText(userCode), "用户编码不能为空");
        return getPerson(new PersonQuery.Builder().userCodeLike(userCode).build());
    }

    /**
     * 根据code查询用户，精确查询
     *
     * @param userCode
     * @return
     */
    default @Nullable
    Person getPersonByUserCodeEqual(String userCode) {
        Assert.isTrue(!StringUtils.isEmpty(userCode), "用户编码不能为空");
        return getPerson(new PersonQuery.Builder().userCodeEqual(userCode).build());
    }

    /**
     * 根据身份证查询用户
     *
     * @param idNO
     * @return
     */
    default @Nullable
    Person getPersonByIdNO(String idNO) {
        Assert.isTrue(StringUtils.hasText(idNO), "身份证号不能为空");
        return getPerson(new PersonQuery.Builder().idNoLike(idNO).build());
    }


    /**
     * 根据机构查询机构直属人员
     *
     * @param organiseId
     * @return
     */
    default List<Person> getOrganiseDefaultPersons(String organiseId) {
        return getPersonList(new PersonQuery.Builder().defaultOrganiseIdEqual(organiseId).build());
    }

    /**
     * 添加机构
     *
     * @param organise
     */
    void addOrganise(Organise organise);

    default void addPerson(Person person) {
        addPerson(person, Organise.DEFAULT_ROOT_ID, false, null);
    }

    default void addPerson(Person person, String organiseId) {
        addPerson(person, organiseId, false, null);
    }

    /**
     * 添加人员
     *
     * @param person        人员基本信息
     * @param organiseId    需要绑定到哪个机构下面
     * @param registerLogin 是否注册登录账户
     * @param password      注册账户时的密码
     */
    void addPerson(Person person, String organiseId, boolean registerLogin, String password);


    /**========================
     *以下是用户组相关的代码
     ===========================*/
    /**
     * 添加用户组
     *
     * @param group
     */
    void addGroup(PersonGroup group, String... personIds);

    /**
     * 获取分组
     *
     * @param name
     * @param types
     * @return
     */
    List<PersonGroup> getGroups(String name, String... types);

    /**
     * 获取分组分页
     *
     * @param name
     * @param start
     * @param end
     * @param types
     * @return
     */
    Page<PersonGroup> getGroupPage(String name, int start, int end, String... types);

    /**
     * 添加用户到指定的用户组
     *
     * @param groupId
     * @param personIds
     */
    void addPersonToGroup(String groupId, String... personIds);

    /**
     * 获取指定用户组所有的用户
     *
     * @param groupId
     * @return
     */
    List<Person> groupPerson(String groupId);

    /**
     * 获取指定用户组用户分页
     *
     * @param groupId
     * @param start
     * @param end
     * @return
     */
    Page<Person> groupPersonPage(String groupId, int start, int end);

    /**
     * 修改机构
     *
     * @param organise
     * @return 影响数据条数
     */
    long updateOrganise(Organise organise);

    /**
     * 删除机构
     *
     * @param organiseId 机构id
     * @return 影响数据条数
     */
    long deleteOrganise(String organiseId);

    /**
     * 更新人员，不更新相关登录账户
     *
     * @param person
     * @return 影响数据条数
     */
    long updatePerson(Person person);

    /**
     * 更新人员
     *
     * @param person
     * @param updateLogin 是否更新相关登录账户
     * @return 影响数据条数
     */
    long updatePerson(Person person, boolean updateLogin);

    /**
     * 变更人员编码
     *
     * @param personId
     * @param userCode
     * @return
     */
    long changePersonUserCode(String personId, String userCode);

    /**
     * 变更人员所属机构
     *
     * @param personId
     * @param organiseId
     * @return
     */
    long changePersonOrganise(String personId, String organiseId);

    /**
     * 删除人员
     *
     * @param personId 人员id
     * @return 影响数据条数
     */
    long deletePerson(String personId);

    /**
     * 根据机构id获取人员树
     *
     * @param orgId
     * @return
     */
    List<TreeNode> personTreeByOrgId(String orgId);

    /**
     * 获取当前登录人所在机构下的人员
     *
     * @param organiseId
     * @return
     */
    List<Person> getOrganiseAllPersons(String organiseId);
}
