package com.dr.framework.common.controller;

import com.dr.framework.common.entity.CreateInfoEntity;
import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.common.page.Page;
import com.dr.framework.common.service.CommonService;
import com.dr.framework.common.service.DefaultDataBaseService;
import com.dr.framework.core.organise.entity.Organise;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.orm.module.EntityRelation;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.security.SecurityHolder;
import com.dr.framework.core.security.bo.ClientInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 基础controller父类
 *
 * @param <T> 指定实体类
 * @author dr
 */
public class BaseController<T extends IdEntity> {
    Logger logger = LoggerFactory.getLogger(BaseController.class);
    @Autowired
    protected CommonService commonService;
    @Autowired
    protected DefaultDataBaseService dataBaseService;

    public static Person getUserLogin(HttpServletRequest request) {
        RequestAttributes attributes = new ServletRequestAttributes(request);
        SecurityHolder securityHolder = SecurityHolder.get();
        return securityHolder == null ? null : securityHolder.currentPerson();
    }

    public static Organise getOrganise(HttpServletRequest request) {
        RequestAttributes attributes = new ServletRequestAttributes(request);
        SecurityHolder securityHolder = SecurityHolder.get();
        return securityHolder == null ? null : securityHolder.currentOrganise();
    }

    public static ClientInfo getClientInfo(HttpServletRequest request) {
        RequestAttributes attributes = new ServletRequestAttributes(request);
        SecurityHolder securityHolder = SecurityHolder.get();
        return securityHolder == null ? null : securityHolder.getClientInfo();
    }

    @RequestMapping("/insert")
    public ResultEntity<T> insert(HttpServletRequest request, T entity) {
        onBeforeInsert(request, entity);
        commonService.insert(entity);
        return ResultEntity.success(entity);
    }

    /**
     * 插入数据前拦截
     *
     * @param request
     * @param entity
     */
    protected void onBeforeInsert(HttpServletRequest request, T entity) {
        if (entity instanceof CreateInfoEntity) {
            if (StringUtils.isEmpty(((CreateInfoEntity) entity).getCreatePerson())) {
                ((CreateInfoEntity) entity).setCreateDate(System.currentTimeMillis());
                ((CreateInfoEntity) entity).setUpdateDate(System.currentTimeMillis());
                Person person = getUserlogin(request);
                if (person != null) {
                    ((CreateInfoEntity) entity).setCreatePerson(person.getId());
                    ((CreateInfoEntity) entity).setUpdatePerson(person.getId());
                } else {
                    logger.warn("实体类{}实现了CreateInfoEntity接口，未获取到当前登录用户", entity.getClass().getSimpleName());
                }
            }
        }
    }

    @RequestMapping("/update")
    public ResultEntity<T> update(HttpServletRequest request, T entity) {
        onBeforeUpdate(request, entity);
        commonService.update(entity);
        return ResultEntity.success(entity);
    }

    protected void onBeforeUpdate(HttpServletRequest request, T entity) {
        if (entity instanceof CreateInfoEntity) {
            Person person = getUserlogin(request);
            ((CreateInfoEntity) entity).setUpdateDate(System.currentTimeMillis());
            if (person != null) {
                ((CreateInfoEntity) entity).setUpdatePerson(person.getId());
            } else {
                logger.warn("实体类{}实现了CreateInfoEntity接口，未获取到当前登录用户", entity.getClass().getSimpleName());
            }
        }
    }

    @RequestMapping("/page")
    public ResultEntity page(HttpServletRequest request,
                             T entity,
                             @RequestParam(defaultValue = "0") int pageIndex,
                             @RequestParam(defaultValue = Page.DEFAULT_PAGE_SIZE_STR) int pageSize,
                             @RequestParam(defaultValue = "true") boolean page) {
        SqlQuery<T> sqlQuery = SqlQuery.from(dataBaseService.getTableInfo(entity.getClass()), true);
        onBeforePageQuery(request, sqlQuery, entity);
        Object result;
        if (page) {
            result = commonService.selectPage(sqlQuery, pageIndex, pageSize);
        } else {
            result = commonService.selectList(sqlQuery);
        }
        return ResultEntity.success(result);
    }

    /**
     * 拦截分页查询参数
     *
     * @param request
     * @param sqlQuery
     * @param entity
     */
    protected void onBeforePageQuery(HttpServletRequest request, SqlQuery<T> sqlQuery, T entity) {
    }

    /**
     * 删除数据
     *
     * @param request
     * @param entity
     * @return
     */
    @RequestMapping("/delete")
    public ResultEntity<Boolean> delete(HttpServletRequest request, T entity) {
        EntityRelation entityRelation = dataBaseService.getTableInfo(entity.getClass());
        SqlQuery<T> sqlQuery = SqlQuery.from(entityRelation);
        if (!StringUtils.isEmpty(entity.getId())) {
            List<String> pks = entityRelation.primaryKeyColumns();
            Assert.isTrue(pks.size() == 1, ()
                    -> String.format("实体类【%s】的主键数量为：%s，【%s】，请手动处理删除逻辑！",
                    entity.getClass(),
                    pks.size(),
                    String.join(",", pks)
            ));
            sqlQuery.in(entityRelation.getColumn(pks.get(0)), entity.getId());
        }
        onBeforeDelete(request, sqlQuery, entity);
        if (sqlQuery.hasWhere()) {
            return ResultEntity.success(commonService.delete(sqlQuery) > 0);
        } else {
            return ResultEntity.error("没有删除条件参数，不执行删除操作！");
        }
    }

    /**
     * @param request
     * @param sqlQuery
     * @param entity
     */
    protected void onBeforeDelete(HttpServletRequest request, SqlQuery<T> sqlQuery, T entity) {

    }

    @RequestMapping("/detail")
    public ResultEntity<T> detail(String id, T entity) {
        if (!StringUtils.isEmpty(id)) {
            T t = (T) commonService.findById(entity.getClass(), id);
            if (t != null) {
                return ResultEntity.success(t);
            }
        }
        return ResultEntity.error("找不到指定记录！");

    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    protected Person getUserlogin(HttpServletRequest request) {
        return getUserlogin(request);
    }
}
