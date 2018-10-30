package com.dr.framework.common.dao;

import com.dr.framework.common.page.Page;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.session.RowBounds;

import java.io.Serializable;
import java.util.List;

/**
 * mapper基础父类,可以直接使用
 *
 * @author dr
 */
@Mapper
public interface CommonMapper {

    /**
     * =============================
     * 增
     * ==============================
     */
    /**
     * 添加一条数据，属性值不为空的时候才添加数据
     *
     * @param entity
     * @return
     */
    @Insert("insert into ${table} ${valuesTest}")
    <E> long insert(E entity);

    /**
     * 根据参数插入所有数据
     *
     * @param entity
     * @return
     */
    @Insert("insert into ${table} ${values}")
    <E> long insertIgnoreNull(E entity);
    /**
     * =============================
     * 改
     * ==============================
     */
    /**
     * 根据id更新数据
     *
     * @param entity
     * @return
     */
    @Update({"<default>update ${table} ${set} where ${pk}=#{id}</default>", "<sqlserver>update A ${set} from ${table} where ${pk}=#{id}</sqlserver>"})
    <E> long updateById(E entity);

    /**
     * 根据sqlquery中的where语句更新数据
     *
     * @param sqlQuery
     * @return
     */
    @Update({"<default>update", SqlQuery.TABLE, "${set}", SqlQuery.WHERE, "</default><sqlserver>update A", "${set}", SqlQuery.FROM, SqlQuery.WHERE, "</sqlserver>"})
    <E> long updateByQuery(SqlQuery<E> sqlQuery);

    @Update({"<default>update ${table} ${settest} where ${pk} =#{id}</default>", "<sqlserver>update A ${settest} from ${table} where ${pk} =#{id}</sqlserver>"})
    <E> long updateIgnoreNullById(E entity);

    @Update({"<default>update", SqlQuery.TABLE, "${settest}", SqlQuery.WHERE, "</default><sqlserver>update A ${settest}", SqlQuery.FROM, SqlQuery.WHERE, "</sqlserver>"})
    <E> long updateIgnoreNullByQuery(SqlQuery<E> sqlQuery);

    /**
     * =============================
     * 查
     * ==============================
     */
    @Select("select ${columns} from  ${table} where ${pk} =#{id}")
    <E> E selectById(Class<E> entityClass, Serializable id);

    @Select({"select ", SqlQuery.COLUMNS, SqlQuery.FROM, SqlQuery.WHERE})
    <E> E selectOneByQuery(SqlQuery<E> sqlQuery);

    @Select("select ${columns} from ${table}")
    <E> List<E> selectAll(Class<E> entityClass);

    @Select({"select", SqlQuery.COLUMNS, SqlQuery.FROM, SqlQuery.WHERE})
    <E> List<E> selectByQuery(SqlQuery<E> sqlQuery);

    @Select({"select", SqlQuery.COLUMNS, SqlQuery.FROM, SqlQuery.WHERE})
    <E> List<E> selectLimitByQuery(SqlQuery<E> sqlQuery, RowBounds rowBounds);

    default <E> List<E> selectLimitByQuery(SqlQuery<E> sqlQuery, int start, int end) {
        return selectLimitByQuery(sqlQuery, new RowBounds(start, end - start));
    }

    default <E> Page<E> selectPageByQuery(SqlQuery<E> sqlQuery, int start, int end) {
        long count = countByQuery(sqlQuery);
        List<E> data = selectLimitByQuery(sqlQuery, start, end);
        Page<E> page = new Page(start, end - start, count);
        page.setData(data);
        return page;
    }

    @Select("select ${columns} from ${table} where ${pk} ${in#coll}")
    <E> List<E> selectBatchIds(Class<E> entityClass, @Param("coll") Serializable... idList);

    @Select("select count(${pk}) from ${table}")
    boolean exists(Class entityClass, Serializable id);

    @Select({"select count({pk}) from ", SqlQuery.FROM, "where", SqlQuery.WHERE})
    boolean existsByQuery(SqlQuery query);

    /**
     * 查询所有的数据条数
     *
     * @return
     */
    @Select("select count(${pk}) from ${table}")
    long count(Class entityClass);

    @Select({"select count(${pk}) from ${table}", SqlQuery.WHERE_NO_ORERY_BY})
    long countByQuery(SqlQuery sqlQuery);

    /**
     * =============================
     * 删
     * ==============================
     */
    /**
     * 根据主建删除数据
     *
     * @param id
     * @return
     */
    @Delete("delete from ${table} where ${pk}= #{id}")
    long deleteById(Class entityClass, Serializable... id);

    /**
     * 根据指定的id删除数据
     *
     * @param idList
     * @return
     */
    @Delete("delete from ${table} where ${pk} ${in#coll}")
    long deleteBatchIds(Class entityClass, @Param("coll") Serializable... idList);

    @Delete({"delete from ${table}", SqlQuery.WHERE})
    long deleteByQuery(SqlQuery query);
}
