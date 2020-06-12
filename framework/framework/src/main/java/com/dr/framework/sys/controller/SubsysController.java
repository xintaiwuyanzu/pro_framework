package com.dr.framework.sys.controller;

import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.common.page.Page;
import com.dr.framework.core.security.entity.SubSystem;
import com.dr.framework.core.security.query.SubSysQuery;
import com.dr.framework.core.security.service.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 子系统管理
 *
 * @author dr
 */
@RestController
@RequestMapping("${common.api-path:/api}/subsys")
public class SubsysController {

    @Autowired(required = false)
    SecurityManager securityManager;

    @RequestMapping("/insert")
    public ResultEntity<SubSystem> insert(SubSystem entity) {
        return ResultEntity.success(securityManager.addSubSys(entity));
    }

    @RequestMapping("/update")
    public ResultEntity<SubSystem> update(SubSystem entity) {
        return ResultEntity.success(securityManager.updateSubSys(entity));
    }

    @RequestMapping("/delete")
    public ResultEntity<Boolean> delete(SubSystem entity) {
        return ResultEntity.success(securityManager.deleteSubSys(entity.getId()));
    }

    @RequestMapping("/detail")
    public ResultEntity<SubSystem> detail(String id, SubSystem entity) {
        List<SubSystem> subSystems = securityManager.selectSubSysList(
                new SubSysQuery.Builder()
                        .idEqual(entity.getId())
                        .build()
        );
        if (subSystems != null && subSystems.size() == 1) {
            return ResultEntity.success(subSystems.get(0));
        } else {
            return ResultEntity.error("未查询到指定数据");
        }
    }


    @RequestMapping("/page")
    public ResultEntity page(SubSystem entity,
                             @RequestParam(defaultValue = "0") int pageIndex,
                             @RequestParam(defaultValue = Page.DEFAULT_PAGE_SIZE_STR) int pageSize,
                             @RequestParam(defaultValue = "true") boolean page) {
        SubSysQuery query = new SubSysQuery.Builder().idEqual(entity.getId()).build();
        if (page) {
            return ResultEntity.success(securityManager.selectSubSysPage(query, pageIndex * pageSize, (pageIndex + 1) * pageSize));
        } else {
            return ResultEntity.success(securityManager.selectSubSysList(query));
        }
    }
}
