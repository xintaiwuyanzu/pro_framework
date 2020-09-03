package com.dr.framework.common.entity;

import java.util.List;

/**
 * 树状结构，用来封装返回数据
 */
public class TreeNode implements Comparable<TreeNode> {
    /**
     * 主建
     */
    private String id;
    /**
     * 父节点id
     */
    private String parentId;
    /**
     * 名称
     */
    private String label;
    /**
     * 描述
     */
    private String description;
    /**
     * 层级
     */
    private int level;
    /**
     * 是否是叶子，前台用来判断是否异步加载子节点数据
     */
    private boolean isLeaf;
    /**
     * 用来封装数据
     */
    private Object data;
    /**
     * 孩子节点数据
     */
    private List<TreeNode> children;

    /**
     * 排序
     */
    private int order;


    public TreeNode(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public TreeNode(String id, String label, String description) {
        this.id = id;
        this.label = label;
        this.description = description;
    }

    public TreeNode(String id, String label, Object data) {
        this.id = id;
        this.label = label;
        this.data = data;
    }

    public TreeNode(String id, String label, String description, Object data) {
        this.id = id;
        this.label = label;
        this.description = description;
        this.data = data;
    }

    public TreeNode(String id, String label, Object data, List<TreeNode> children) {
        this.id = id;
        this.label = label;
        this.data = data;
        this.children = children;
    }

    public TreeNode(String id, String label, List<TreeNode> children) {
        this.id = id;
        this.label = label;
        this.children = children;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int compareTo(TreeNode o) {
        int orderCompare = this.order - o.getOrder();
        if (orderCompare == 0) {
            return id.compareTo(o.getId());
        } else {
            return orderCompare;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
