package com.dr.framework.common.controller;

import com.dr.framework.common.entity.CreateInfoEntity;
import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.common.page.Page;
import com.dr.framework.common.service.CommonService;
import com.dr.framework.core.orm.sql.TableInfo;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.sys.controller.LoginController;
import com.dr.framework.sys.entity.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 基础controller父类
 *
 * @param <T> 指定实体类
 * @author dr
 */
public class BaseController<T extends IdEntity> {
    Logger logger = LoggerFactory.getLogger(BaseController.class);
    @Autowired
    LoginController loginController;
    @Autowired
    protected CommonService commonService;

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
    public ResultEntity page(HttpServletRequest request, T entity, @RequestParam(defaultValue = "0") int pageIndex, @RequestParam(defaultValue = Page.DEFAULT_PAGE_SIZE + "") int pageSize, @RequestParam(defaultValue = "true") boolean page) {
        SqlQuery<T> sqlQuery = SqlQuery.from(entity, true);
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
        SqlQuery<T> sqlQuery = SqlQuery.from(entity);
        if (!StringUtils.isEmpty(entity.getId())) {
            TableInfo tableInfo = SqlQuery.getTableInfo(entity.getClass());
            sqlQuery.in(tableInfo.pk(), entity.getId());
        }
        onBeforeDelete(request, sqlQuery, entity);
        if (sqlQuery.hasWhere()) {
            return ResultEntity.success(commonService.delete(sqlQuery));
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

    protected Person getUserlogin(HttpServletRequest request) {
        ResultEntity<Person> p = loginController.personInfo(request);
        return p.isSuccess() ? p.getData() : null;
    }
}
