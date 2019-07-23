package com.dr.framework.sys.service;

import com.dr.framework.common.page.Page;
import com.dr.framework.sys.entity.Organise;
import com.dr.framework.sys.entity.Person;
import com.dr.framework.sys.query.OrganiseQuery;
import com.dr.framework.sys.query.PersonQuery;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * 组织机构相关的service
 *
 * @author dr
 */
public interface SysOrganisePersonService {
    /**
     * 根据机构id获取一直到根的父机构数据
     *
     * @param organiseId
     * @return
     */
    List<Organise> getParentOrganiseList(String organiseId);

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

    default List<Person> getOrganiseDefaultPersons(String organiseId) {
        return getPersonList(new PersonQuery.Builder().defaultOrganiseIdEqual(organiseId).build());
    }

    /**
     * 添加机构
     *
     * @param organise
     */
    void addOrganise(Organise organise);

    //TODO 删除和修改机构
    //TODO 删除和修改人员
    default void addPerson(Person person, String organiseId) {
        addPerson(person, organiseId, false, null);
    }

    void addPerson(Person person, String organiseId, boolean registerLogin, String password);

}
