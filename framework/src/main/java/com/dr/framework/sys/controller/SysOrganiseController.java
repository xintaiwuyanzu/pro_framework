package com.dr.framework.sys.controller;

import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.common.entity.ResultListEntity;
import com.dr.framework.common.entity.TreeNode;
import com.dr.framework.common.service.CommonService;
import com.dr.framework.core.organise.entity.Organise;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.organise.query.OrganiseQuery;
import com.dr.framework.core.organise.service.OrganisePersonService;
import com.dr.framework.core.util.Constants;
import com.dr.framework.core.web.annotations.Current;
import com.dr.framework.sys.service.DefaultSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.dr.framework.common.entity.ResultEntity.success;

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
    @Autowired
    DefaultSecurityManager securityManager;

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
        Assert.isTrue(StringUtils.hasText(id), "机构id不能为空");
        return ResultEntity.success(organisePersonService.deleteOrganise(id) > 0);
    }

    /**
     * 获取指定的机构树
     *
     * @param all
     * @return
     */
    @RequestMapping("/organiseTree")
    public ResultEntity<List<TreeNode>> organiseTree(boolean all,
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

    /**
     * 查询所有部门
     *
     * @return
     */
    @PostMapping(value = "/getAllDepartment")
    public ResultEntity<List<Organise>> getAllDepartment() {
        OrganiseQuery organiseQuery = new OrganiseQuery.Builder().build();
        List<Organise> list = organisePersonService.getOrganiseList(organiseQuery);
        return success(list);
    }

    /**
     * 获取当前登录人所在机构下的人员
     *
     * @param organise
     * @return
     */
    @RequestMapping("/getPersonsByOrganise")
    public ResultEntity<List<Person>> getPersonsByOrganise(@Current Organise organise) {
        if (StringUtils.hasText(organise.getId())) {
            return success(organisePersonService.getOrganiseDefaultPersons(organise.getId()));
        } else {
            return success(Collections.EMPTY_LIST);
        }
    }


    /**
     * 根据角色id获取人员
     *
     * @param roleId
     * @return
     */
    @RequestMapping("/getPersonsByRoleId")
    public ResultEntity<List<Person>> getPersonsByRoleId(String roleId) {
        Assert.hasText(roleId, "角色ID不能为空");
        return success(securityManager.roleUsers(roleId));
    }

    /**
     * 获取机构人员树
     *
     * @return
     */
    @RequestMapping("/getOrganisePersonTree")
    public ResultListEntity<TreeNode> getOrganisePersonTree() {
        List<TreeNode> treeNodeList = new ArrayList<>();
        List<Organise> organiseList = this.organisePersonService.getOrganiseList((new OrganiseQuery.Builder()).build());
        for (Organise organise : organiseList) {
            TreeNode treeNode = new TreeNode(organise.getId(), organise.getOrganiseName(), organise, organisePersonService.personTreeByOrgId(organise.getId()));
            treeNodeList.add(treeNode);
        }
        return ResultListEntity.success(treeNodeList);
    }

    @RequestMapping("/getCurrentOrganise")
    public ResultEntity<Organise> getCurrentOrganise(@Current Organise organise) {
        return ResultEntity.success(organise);
    }
}
