package com.dr.framework.sys.controller;

import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.common.service.CommonService;
import com.dr.framework.core.organise.entity.Organise;
import com.dr.framework.core.organise.query.OrganiseQuery;
import com.dr.framework.core.organise.service.OrganisePersonService;
import com.dr.framework.core.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
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
public class SysOrganiseController {
    @Autowired
    OrganisePersonService organisePersonService;

    /**
     * 添加机构
     *
     * @param entity
     * @return
     */
    @RequestMapping("/insert")
    public ResultEntity<Organise> insert(Organise entity) {
        organisePersonService.addOrganise(entity);
        return ResultEntity.success(entity);
    }

    /**
     * 机构详情
     *
     * @param id
     * @return
     */
    @RequestMapping("/detail")
    public ResultEntity<Organise> detail(String id) {
        Assert.notNull(id, "主键不能为空!");
        return ResultEntity.success(organisePersonService.getOrganise(new OrganiseQuery.Builder()
                .idEqual(id)
                .build()));
    }

    @RequestMapping("/update")
    public ResultEntity<Organise> update(Organise organise) {
        organisePersonService.updateOrganise(organise);
        return ResultEntity.success(organise);
    }

    /**
     * 删除数据
     *
     * @return
     */
    @RequestMapping("/delete")
    public ResultEntity<Boolean> delete(String id) {
        Assert.isTrue(!StringUtils.isEmpty(id), "机构id不能为空");
        organisePersonService.deleteOrganise(id);
        return ResultEntity.success("删除成功");
    }

    /**
     * 获取指定的机构树
     *
     * @param all
     * @return
     */
    @RequestMapping("/organiseTree")
    public ResultEntity organiseTree(boolean all,
                                     @RequestParam(defaultValue = Constants.DEFAULT) String groupId,
                                     @RequestParam(defaultValue = Organise.DEFAULT_ROOT_ID) String parentId

    ) {
        OrganiseQuery.Builder builder = new OrganiseQuery.Builder()
                .parentIdEqual(parentId)
                .groupIdEqual(groupId);
        if (!all) {
            builder.statusEqual(Organise.STATUS_ENABLE_STR);
        }
        List<Organise> organises = organisePersonService.getOrganiseList(builder.build());
        return ResultEntity.success(CommonService.listToTree(organises, parentId, Organise::getOrganiseName));
    }
}
