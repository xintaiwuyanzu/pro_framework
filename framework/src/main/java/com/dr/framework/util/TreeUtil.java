package com.dr.framework.util;

import com.dr.framework.common.entity.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 转换list为tree树
 */
public class TreeUtil {

    public static <T> List<TreeNode> listToTree(List<T> objectList, Function<T, TreeNode> treeNodeFunction, String rootId) {
        List<TreeNode> treeNodes = objectList.stream().map(treeNodeFunction).collect(Collectors.toList());
        Map<String, List<TreeNode>> stringListMap = new HashMap<>();
        for (TreeNode treeNode : treeNodes) {
            List<TreeNode> treeNodeList = stringListMap.computeIfAbsent(treeNode.getParentId(), k -> new ArrayList<>());
            treeNodeList.add(treeNode);
        }
        return mapToTree(stringListMap, rootId);
    }

    private static List<TreeNode> mapToTree(Map<String, List<TreeNode>> stringListMap, String rootId) {
        List<TreeNode> treeNodes = stringListMap.get(rootId);
        if (treeNodes != null) {
            treeNodes.forEach(treeNode -> {
                List<TreeNode> treeNodes1 = mapToTree(stringListMap, treeNode.getId());
                if (treeNodes1 != null) {
                    treeNode.setChildren(treeNodes1);
                }
            });
        }
        return treeNodes;
    }
}
