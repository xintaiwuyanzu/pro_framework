package com.dr.framework.sys.service;

import com.dr.framework.common.entity.TreeNode;
import com.dr.framework.common.service.CommonService;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.sys.entity.SysDict;
import com.dr.framework.sys.entity.SysDictInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统字典模块
 *
 * @author dr
 */
@Service
public class SysDictService {
    @Autowired
    CommonService commonService;

    public List<TreeNode> dict(String type) {
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

}
