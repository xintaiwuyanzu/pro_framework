package com.dr.framework.common.controller;

import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.common.page.Page;
import com.dr.framework.common.service.BaseService;
import com.dr.framework.common.service.DefaultDataBaseService;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.orm.module.EntityRelation;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 使用自定义service的通用controller
 *
 * @param <S>
 * @param <E>
 */
public abstract class BaseServiceController<S extends BaseService<E>, E extends IdEntity> {
    protected final Logger logger = LoggerFactory.getLogger(BaseServiceController.class);
    @Autowired
    protected S service;
    @Autowired
    protected DefaultDataBaseService dataBaseService;

    @RequestMapping("/insert")
    public ResultEntity<E> insert(HttpServletRequest request, E entity) {
        service.insert(entity);
        return ResultEntity.success(entity);
    }

    @RequestMapping("/update")
    public ResultEntity<E> update(HttpServletRequest request, E entity) {
        service.updateById(entity);
        return ResultEntity.success(entity);
    }

    @RequestMapping("/page")
    public ResultEntity page(HttpServletRequest request,
                             E entity,
                             @RequestParam(defaultValue = "0") int pageIndex,
                             @RequestParam(defaultValue = Page.DEFAULT_PAGE_SIZE_STR) int pageSize,
                             @RequestParam(defaultValue = "true") boolean page) {
        SqlQuery<E> sqlQuery = buildPageQuery(request, entity);
        Object result = page ?
                service.selectPage(sqlQuery, pageIndex, pageSize)
                :
                service.selectList(sqlQuery);
        return ResultEntity.success(result);
    }

    /**
     * 根据参数构建查询条件
     *
     * @param request
     * @param entity
     * @return
     */
    protected abstract SqlQuery<E> buildPageQuery(HttpServletRequest request, E entity);

    /**
     * 删除数据
     *
     * @param request
     * @param entity
     * @return
     */
    @RequestMapping("/delete")
    public ResultEntity<Boolean> delete(HttpServletRequest request, E entity) {
        SqlQuery<E> deleteQuery = buildDeleteQuery(request, entity);
        if (deleteQuery.hasWhere()) {
            return ResultEntity.success(service.delete(deleteQuery) > 0);
        } else {
            return ResultEntity.error("没有删除条件参数，不执行删除操作！");
        }
    }

    /**
     * 根据查询条件构造删除语句
     *
     * @param request
     * @param entity
     * @return
     */
    protected SqlQuery<E> buildDeleteQuery(HttpServletRequest request, E entity) {
        SqlQuery<? extends IdEntity> sqlQuery = SqlQuery.from(entity.getClass());
        EntityRelation relation = dataBaseService.getTableInfo(entity.getClass());
        Assert.isTrue(!StringUtils.isEmpty(entity.getId()), "删除条件不能为空!");
        sqlQuery.in(relation.getColumn(IdEntity.ID_COLUMN_NAME), entity.getId().split(IdEntity.MULTI_STR_SPLIT_CHAR));
        return (SqlQuery<E>) sqlQuery;
    }

    /**
     * 显示详情
     *
     * @param request
     * @param entity
     * @param id
     * @return
     */
    @RequestMapping("/detail")
    public ResultEntity<E> detail(HttpServletRequest request, E entity, String id) {
        SqlQuery<E> sqlQuery = buildDetailQuery(request, entity, id);
        if (sqlQuery != null && sqlQuery.hasWhere()) {
            return ResultEntity.success(service.selectOne(sqlQuery));
        }
        return ResultEntity.error("找不到指定记录！");

    }

    /**
     * 构造详情查询条件
     *
     * @param request
     * @param entity
     * @param id
     * @return
     */
    protected SqlQuery<E> buildDetailQuery(HttpServletRequest request, E entity, String id) {
        if (!StringUtils.isEmpty(id)) {
            return (SqlQuery<E>) SqlQuery.from(entity.getClass())
                    .equal(
                            dataBaseService.getTableInfo(entity.getClass())
                                    .getColumn(IdEntity.ID_COLUMN_NAME),
                            id
                    )
                    ;
        }
        return null;
    }

    //增删改查
    protected Person getUserlogin(HttpServletRequest request) {
        return BaseController.getUserLogin(request);
    }

}
