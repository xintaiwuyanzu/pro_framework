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
    @Insert("insert into !!{table} !!{valuesTest}")
    <E> long insert(E entity);

    @Insert("insert into !!{table} !!{valuesTest}")
    <E> long insertByQuery(SqlQuery<E> entity);

    /**
     * 主建自增长
     *
     * @param entity
     * @param <E>
     * @return
     */
    @Insert("insert into !!{table} !!{valuesTest}")
    @Options(useGeneratedKeys = true)
    @Deprecated
    <E> long insertAutoGenKey(E entity);

    /**
     * 根据参数插入所有数据
     *
     * @param entity
     * @return
     */
    @Insert("insert into !!{table} !!{values}")
    <E> long insertIgnoreNull(E entity);

    /**
     * 主建自增长
     *
     * @param entity
     * @param <E>
     * @return
     */
    @Insert("insert into !!{table} !!{values}")
    @Options(useGeneratedKeys = true)
    @Deprecated
    <E> long insertIgnoreNullAutoGenKey(E entity);
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
    @Update({
            "<default>update !!{table} !!{set} where !!{pk}=!!{id}</default>"
            , "<sqlserver>update A !!{set} from !!{table} where !!{pk}=!!{id}</sqlserver>"
            , "<postgre>update A !!{setNo} from !!{table} where !!{pk}=!!{id}</postgre>"
    })
    <E> long updateById(E entity);

    /**
     * 根据sqlquery中的where语句更新数据
     *
     * @param sqlQuery
     * @return
     */
    @Update({
            "<default>update", SqlQuery.TABLE, "!!{setByQuery}", SqlQuery.WHERE, "</default>"
            , "<sqlserver>update A", "!!{setByQuery}", SqlQuery.FROM, SqlQuery.WHERE, "</sqlserver>"
            , "<postgre>update A", "!!{setNoAliasByQuery}", SqlQuery.FROM, SqlQuery.WHERE, "</postgre>"
    })
    <E> long updateByQuery(SqlQuery<E> sqlQuery);

    @Update({
            "<default>update !!{table} !!{settest} where !!{pk} =!!{id}</default>"
            , "<sqlserver>update A !!{settest} from !!{table} where !!{pk} =!!{id}</sqlserver>"
            , "<postgre>update A !!{setNotest} from !!{table} where !!{pk} =!!{id}</postgre>"
    })
    <E> long updateIgnoreNullById(E entity);

    @Update({
            "<default>update", SqlQuery.TABLE, "!!{settest}", SqlQuery.WHERE, "</default>"
            , "<sqlserver>update A !!{settest}", SqlQuery.FROM, SqlQuery.WHERE, "</sqlserver>"
            , "<postgre>update A !!{setNotest}", SqlQuery.FROM, SqlQuery.WHERE, "</postgre>"
    })
    <E> long updateIgnoreNullByQuery(SqlQuery<E> sqlQuery);

    /**
     * =============================
     * 查
     * ==============================
     */
    @Select("select !!{columns} from  !!{table} where !!{pk} =#{id}")
    <E> E selectById(Class<E> entityClass, @Param("id") Serializable id);

    @Select({"select ", SqlQuery.COLUMNS, SqlQuery.FROM, SqlQuery.WHERE})
    <E> E selectOneByQuery(SqlQuery<E> sqlQuery);

    @Select({
            "<default> select ", SqlQuery.COLUMNS, SqlQuery.FROM, SqlQuery.WHERE, " for update</default>",
            "<sqlserver> select ", SqlQuery.COLUMNS, SqlQuery.FROM, SqlQuery.WHERE, " WITH (TABLOCKX)</sqlserver>"
    })
    <E> E selectOneByQueryForUpdate(SqlQuery<E> sqlQuery);

    @Select("select !!{columns} from !!{table}")
    <E> List<E> selectAll(Class<E> entityClass);

    @Select({"select", SqlQuery.COLUMNS, SqlQuery.FROM, SqlQuery.WHERE})
    <E> List<E> selectByQuery(SqlQuery<E> sqlQuery);

    @Select({
            "<default> select ", SqlQuery.COLUMNS, SqlQuery.FROM, SqlQuery.WHERE, " for update</default>",
            "<sqlserver> select ", SqlQuery.COLUMNS, SqlQuery.FROM, SqlQuery.WHERE, " WITH (TABLOCKX)</sqlserver>"
    })
    <E> List<E> selectByQueryForUpdate(SqlQuery<E> sqlQuery);

    @Select({"select", SqlQuery.COLUMNS, SqlQuery.FROM, SqlQuery.WHERE})
    <E> List<E> selectLimitByQuery(SqlQuery<E> sqlQuery, RowBounds rowBounds);

    default <E> List<E> selectLimitByQuery(SqlQuery<E> sqlQuery, int start, int end) {
        return selectLimitByQuery(sqlQuery, new RowBounds(start, end - start));
    }

    default <E> Page<E> selectPageByQuery(SqlQuery<E> sqlQuery, int start, int end) {
        return new Page<>(start,
                end - start,
                countByQuery(sqlQuery),
                () -> selectLimitByQuery(sqlQuery, start, end));
    }

    @Select("select !!{columns} from !!{table} where !!{pk} !!{in#coll}")
    <E> List<E> selectBatchIds(Class<E> entityClass, @Param("coll") Serializable... idList);

    @Select("select count(!!{pk}) from !!{table} where !!{pk}=#{id}")
    boolean exists(Class entityClass, @Param("id") Serializable id);

    default boolean existsByQuery(SqlQuery query) {
        return countByQuery(query) > 0;
    }

    /**
     * 查询所有的数据条数
     * count(*)会统计值为 NULL 的行，而 count(列名)不会统计此列为 NULL 值的行。
     *
     * @return
     */
    @Select("select count(*) from !!{table}")
    long count(Class entityClass);

    @Select({"select count(*)", SqlQuery.FROM, SqlQuery.WHERE_NO_ORERY_BY})
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
    @Delete({
            "<default>delete from !!{table} where !!{pk}= #{id}</default>"
            , "<sqlserver>delete A from !!{table} where !!{pk}= #{id}</sqlserver>"
            , "<mysql>delete A from !!{table} where !!{pk}= #{id}</mysql>"
    })
    long deleteById(Class entityClass, @Param("id") Serializable id);

    /**
     * 这种方式传参有问题
     *
     * @param entityClass
     * @param id
     * @return
     * @deprecated {@link #deleteBatchIds(Class, Serializable...)}
     */
    @Deprecated
    default long deleteById(Class entityClass, @Param("id") Serializable... id) {
        return deleteBatchIds(entityClass, id);
    }

    /**
     * 根据指定的id删除数据
     *
     * @param idList
     * @return
     */
    @Delete({
            "<default>delete from !!{table} where !!{pk} !!{in#coll}</default>"
            , "<sqlserver>delete A from !!{table} where !!{pk} !!{in#coll}</sqlserver>"
            , "<mysql>delete A from !!{table} where !!{pk} !!{in#coll}</mysql>"
    })
    long deleteBatchIds(Class entityClass, @Param("coll") Serializable... idList);

    /**
     * 每个数据库的删除语法不相同
     *
     * @param query
     * @return
     */
    @Delete({
            "<default>delete from !!{table}", SqlQuery.WHERE, "</default>"
            , "<sqlserver>delete A from !!{table}", SqlQuery.WHERE, "</sqlserver>"
            , "<mysql>delete A from !!{table}", SqlQuery.WHERE, "</mysql>"
    })
    long deleteByQuery(SqlQuery query);
}
