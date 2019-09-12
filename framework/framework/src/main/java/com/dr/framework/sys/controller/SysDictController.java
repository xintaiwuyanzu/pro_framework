package com.dr.framework.sys.controller;

import com.dr.framework.common.controller.BaseController;
import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.common.entity.ResultListEntity;
import com.dr.framework.common.entity.TreeNode;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.sys.entity.SysDict;
import com.dr.framework.sys.entity.SysDictInfo;
import com.dr.framework.sys.service.SysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 系统字典
 *
 * @author dr
 */
@RestController
@RequestMapping("${common.api-path:/api}/sysDict")
public class SysDictController extends BaseController<SysDict> {
    @Autowired
    SysDictService sysDictService;

    @Override
    protected void onBeforeInsert(HttpServletRequest request, SysDict entity) {
        super.onBeforeInsert(request, entity);
        if (entity != null && StringUtils.isEmpty(entity.getType()) && !StringUtils.isEmpty(entity.getKey())) {
            entity.setType(entity.getKey().split("\\.")[0]);
        }
    }

    @Override
    protected void onBeforePageQuery(HttpServletRequest request, SqlQuery<SysDict> sqlQuery, SysDict entity) {
        super.onBeforePageQuery(request, sqlQuery, entity);
        sqlQuery.orderBy(SysDictInfo.KEYINFO, SysDictInfo.ORDERBY);
        sqlQuery.equalIfNotNull(SysDictInfo.STATUS);
        sqlQuery.like(SysDictInfo.KEYINFO, SysDictInfo.VALUE, SysDictInfo.DESCRIPTION);
    }

    @RequestMapping("/validate")
    public ResultEntity validate(String key) {
        boolean exist = false;
        if (!StringUtils.isEmpty(key)) {
            exist = commonService.exists(SqlQuery.from(SysDict.class).equal(SysDictInfo.KEYINFO, key));
        }
        if (exist) {
            return ResultEntity.error("不能存在相同的编码字段！");
        } else {
            return ResultEntity.success();
        }
    }

    /**
     * 获取字典树
     *
     * @param type xxx.xxx.xx type应该是完整的父路径
     * @return
     */
    @RequestMapping("/dict")
    public ResultListEntity<TreeNode> dict(String type) {
        return ResultListEntity.success(sysDictService.dict(type));
    }
}
