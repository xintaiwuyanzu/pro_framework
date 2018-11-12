package com.dr.framework.sys.controller;

import com.dr.framework.common.controller.BaseController;
import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.common.service.CommonService;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.sys.entity.Organise;
import com.dr.framework.sys.entity.SysMenuInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/organise")
public class SysOrganiseController extends BaseController<Organise> {

    @RequestMapping("/organiseTree")
    public ResultEntity menutree(boolean all, @RequestParam(defaultValue = "default") String sysId) {
        SqlQuery<Organise> sqlQuery = SqlQuery.from(Organise.class, true);
        if (!all) {
            sqlQuery.equal(SysMenuInfo.STATUS, "1");
        }
        List<Organise> organises = commonService.selectList(sqlQuery);
        return ResultEntity.success(CommonService.listToTree(organises, sysId, organise -> organise.getOrganiseName()));
    }
}
