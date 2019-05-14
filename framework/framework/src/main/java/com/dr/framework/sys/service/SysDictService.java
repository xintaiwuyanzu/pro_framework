package com.dr.framework.sys.service;

import com.dr.framework.common.entity.TreeNode;
import com.dr.framework.common.service.CommonService;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.sys.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysDictService {

    @Autowired
    CommonService commonService;

    public List<TreeNode> dict(String type) {
        if (type.startsWith("organise")) {
            return organiseDict(type);
        }
        if (StringUtils.isEmpty(type)) {
            return new ArrayList<>();
        } else {
            if (!type.endsWith(".")) {
                type = type + ".";
            }
        }
        List<SysDict> sysDicts = commonService.selectList(SqlQuery.from(SysDict.class)
                .startingWith(SysDictInfo.KEYINFO, type)
                .equal(SysDictInfo.STATUS, 1)
                .orderBy(SysDictInfo.ORDERBY)
        );
        return sysDicts.stream()
                .map(sysDict -> {
                    String[] keyArr = sysDict.getKey().split("\\.");
                    String key = keyArr[keyArr.length - 1];
                    return new TreeNode(key, sysDict.getValue(), sysDict);
                })
                .collect(Collectors.toList());
    }

    private List<TreeNode> organiseDict(String type) {
        String[] types = type.split(".");
        String sysId = types.length > 1 ? types[types.length] : SubSystem.DEFAULT_SYSTEM_ID;
        SqlQuery<Organise> sqlQuery = SqlQuery.from(Organise.class).equal(OrganiseInfo.STATUS, 1).equal(OrganiseInfo.SYSID, sysId).orderBy(OrganiseInfo.ORDERBY);
        return commonService.selectList(sqlQuery).stream()
                .map(organise -> new TreeNode(organise.getOrganiseCode(), organise.getOrganiseName(), organise))
                .collect(Collectors.toList());
    }
}
