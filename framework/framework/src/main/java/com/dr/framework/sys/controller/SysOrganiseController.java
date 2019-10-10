package com.dr.framework.sys.controller;

import com.dr.framework.common.controller.BaseController;
import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.common.service.CommonService;
import com.dr.framework.core.organise.entity.Organise;
import com.dr.framework.core.organise.service.OrganisePersonService;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 组织机构
 *
 * @author dr
 */
@RestController
@RequestMapping("${common.api-path:/api}/organise")
public class SysOrganiseController extends BaseController<Organise> {
    @Autowired
    OrganisePersonService organisePersonService;

    @RequestMapping("/organiseTree")
    public ResultEntity organiseTree(boolean all, @RequestParam(defaultValue = "default") String sysId) {
        SqlQuery<Organise> sqlQuery = SqlQuery.from(Organise.class, true);
        List<Organise> organises = commonService.selectList(sqlQuery);
        return ResultEntity.success(CommonService.listToTree(organises, sysId, Organise::getOrganiseName));
    }
}
