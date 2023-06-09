package com.dr.framework.common.service;

import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.common.page.Page;
import com.dr.framework.core.orm.sql.support.SqlQuery;

import java.util.List;

/**
 * 通用service
 *
 * @param <T> 实体类泛型
 * @author dr
 */
public interface BaseService<T extends IdEntity> {
    /**
     * 添加数据
     *
     * @param entity
     * @return 更改数据条数
     */
    long insert(T entity);

    /**
     * 根据id更新数据
     *
     * @param entity
     * @return 更改数据条数
     */
    long updateById(T entity);

    /**
     * 根据sqlquery更新数据
     *
     * @param sqlQuery
     * @return
     */
    long updateBySqlQuery(SqlQuery<T> sqlQuery);

    /**
     * 根据sqlquery查询一条数据
     *
     * @param sqlQuery
     * @return
     */
    T selectOne(SqlQuery<T> sqlQuery);

    /**
     * 根据主键查询数据
     *
     * @param id
     * @return
     */
    T selectById(String id);

    /**
     * 查询列表
     *
     * @param sqlQuery
     * @return
     */
    List<T> selectList(SqlQuery<T> sqlQuery);

    /**
     * 查询分页
     *
     * @param sqlQuery
     * @param pageIndex
     * @param pageSize
     * @return
     */
    Page<T> selectPage(SqlQuery<T> sqlQuery, int pageIndex, int pageSize);

    /**
     * 统计方法
     *
     * @param sqlQuery
     * @return
     */
    long count(SqlQuery<T> sqlQuery);

    /**
     * 指定的id是否存在
     *
     * @param id
     * @return
     */
    boolean exists(String id);

    /**
     * 删除数据
     *
     * @param sqlQuery
     * @return
     */
    long delete(SqlQuery<T> sqlQuery);

    /**
     * 根据主键删除数据
     *
     * @param ids
     * @return
     */
    long deleteById(String... ids);

}
