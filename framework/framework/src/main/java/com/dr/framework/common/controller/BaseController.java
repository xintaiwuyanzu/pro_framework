package com.dr.framework.common.controller;

import com.dr.framework.common.entity.CreateInfoEntity;
import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.common.page.Page;
import com.dr.framework.common.service.CommonService;
import com.dr.framework.core.orm.sql.TableInfo;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.sys.entity.UserLogin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 基础controller父类
 *
 * @param <T> 指定实体类
 * @author dr
 */
public class BaseController<T extends IdEntity> {
    @Autowired
    protected CommonService commonService;
    Logger logger = LoggerFactory.getLogger(BaseController.class);

    @RequestMapping("/insert")
    public ResultEntity insert(HttpServletRequest request, T entity) {
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

                UserLogin userLogin = getUserlogin(request);
                if (userLogin != null) {
                    ((CreateInfoEntity) entity).setCreatePerson(userLogin.getPersonId());
                    ((CreateInfoEntity) entity).setUpdatePerson(userLogin.getPersonId());
                } else {
                    logger.warn("实体类{}实现了CreateInfoEntity接口，未获取到当前登录用户", entity.getClass().getSimpleName());
                }
            }
        }
    }

    @RequestMapping("/update")
    public ResultEntity update(HttpServletRequest request, T entity) {
        onBeforeUpdate(request, entity);
        commonService.update(entity);
        return ResultEntity.success(entity);
    }

    protected void onBeforeUpdate(HttpServletRequest request, T entity) {
        if (entity instanceof CreateInfoEntity) {
            UserLogin userLogin = getUserlogin(request);
            ((CreateInfoEntity) entity).setUpdateDate(System.currentTimeMillis());
            if (userLogin != null) {
                ((CreateInfoEntity) entity).setUpdatePerson(userLogin.getPersonId());
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
    public ResultEntity delete(HttpServletRequest request, T entity) {
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
    public ResultEntity detail(String id, T entity) {
        SqlQuery sqlQuery = SqlQuery.from(entity.getClass());
        T t = (T) commonService.selectOne(sqlQuery);
        if (t == null) {
            return ResultEntity.error("找不到指定记录！");
        } else {
            return ResultEntity.success(t);
        }
    }

    protected UserLogin getUserlogin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        UserLogin userLogin = null;
        if (session != null) {
            userLogin = (UserLogin) session.getAttribute("USERLOGIN");
        }
        return userLogin;
    }
}
