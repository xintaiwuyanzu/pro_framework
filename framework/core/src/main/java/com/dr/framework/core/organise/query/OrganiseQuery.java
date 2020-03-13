package com.dr.framework.core.organise.query;

import com.dr.framework.common.query.IdQuery;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 组织机构查询类，
 * 请使用{@link Builder}类构建
 *
 * @author dr
 */
public class OrganiseQuery extends IdQuery {
    /**
     * 所属系统
     */
    private String sysId;
    /**
     * 机构名称
     */
    private String organiseName;
    /**
     * 机构类型like
     */
    private String typeLike;
    /**
     * 机构类型notlike
     */
    private String typeNotLike;
    /**
     * 编码相同
     */
    private String codeEqual;
    /**
     * 机构类型
     */
    private List<String> organiseType;
    /**
     * 不包含的机构类型
     */
    private List<String> organiseTypeNotIn;
    /**
     * 包含的直接的人员id
     */
    private List<String> directPersonIds;
    /**
     * 人员属于机构树下面的一个
     */
    private List<String> personIds;
    /**
     * ======
     * 这里都是直接默认的parentid，只能查询一级数据
     * ======
     * <p>
     * 父id
     */
    private List<String> parentIds;

    /**
     * 这里都是直接默认的parentid，只能查询直接下属，不能跨级查询
     * 不包含的父id
     */
    private List<String> parentIdNotIn;
    /**
     * 这个是祖先id，能够查询多级数据
     */
    private List<String> treeParentId;

    /**
     * 状态等于
     */
    private List<String> status;
    /**
     * 状态不等于
     */
    private List<String> statusNotIn;
    /**
     * 创建人员等于
     */
    private List<String> createPersons;
    /**
     * 数据来源等于
     */
    private List<String> sourceRef;
    /**
     * 数据来源不等于
     */
    private List<String> sourceRefNotIn;

    /**
     * 创建日期起始
     */
    private Long createDateStart;
    /**
     * 创建日期结束
     */
    private Long createDateEnd;
    /**
     * 机构分组
     */
    private String groupId;


    private OrganiseQuery() {
    }

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public List<String> getSourceRef() {
        return sourceRef;
    }

    public void setSourceRef(List<String> sourceRef) {
        this.sourceRef = sourceRef;
    }

    public List<String> getSourceRefNotIn() {
        return sourceRefNotIn;
    }

    public void setSourceRefNotIn(List<String> sourceRefNotIn) {
        this.sourceRefNotIn = sourceRefNotIn;
    }

    public String getTypeLike() {
        return typeLike;
    }

    public void setTypeLike(String typeLike) {
        this.typeLike = typeLike;
    }

    public String getTypeNotLike() {
        return typeNotLike;
    }

    public void setTypeNotLike(String typeNotLike) {
        this.typeNotLike = typeNotLike;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<String> getTreeParentId() {
        return treeParentId;
    }

    public void setTreeParentId(List<String> treeParentId) {
        this.treeParentId = treeParentId;
    }

    public String getCodeEqual() {
        return codeEqual;
    }

    public static class Builder extends IdQuery.Builder<OrganiseQuery, Builder> {
        private OrganiseQuery query = new OrganiseQuery();

        public Builder() {
            this("default");
        }

        public Builder(String sysId) {
            query.setSysId(sysId);
        }

        private List<String> newList(String... strings) {
            return Arrays.stream(strings)
                    .filter(s -> !StringUtils.isEmpty(s))
                    .collect(Collectors.toList());
        }

        @Override
        public OrganiseQuery getQuery() {
            return query;
        }

        /**
         * 分组id
         *
         * @param id
         * @return
         */
        public Builder groupIdEqual(String id) {
            if (!StringUtils.isEmpty(id)) {
                query.setGroupId(id);
            }
            return this;
        }

        /**
         * 机构名称
         *
         * @param name
         * @return
         */
        public Builder organiseNameLike(String name) {
            query.organiseName = name;
            return this;
        }

        /**
         * 类型等于
         *
         * @param types
         * @return
         */
        public Builder typeEqual(String... types) {
            List<String> strings = newList(types);
            if (!strings.isEmpty()) {
                if (query.organiseType == null) {
                    query.organiseType = new ArrayList<>();
                }
                query.organiseType.addAll(strings);
            }
            return this;
        }

        /**
         * 类型不等于
         *
         * @param types
         * @return
         */
        public Builder typeNotEqual(String... types) {
            List<String> strings = newList(types);
            if (!strings.isEmpty()) {
                if (query.organiseTypeNotIn == null) {
                    query.organiseTypeNotIn = new ArrayList<>();
                }
                query.organiseTypeNotIn.addAll(strings);
            }
            return this;
        }

        /**
         * 直接管理某些人员
         *
         * @param personIds
         * @return
         */
        public Builder defaultPersonIdEqual(String... personIds) {
            List<String> strings = newList(personIds);
            if (!strings.isEmpty()) {
                if (query.directPersonIds == null) {
                    query.directPersonIds = new ArrayList<>();
                }
                query.directPersonIds.addAll(strings);
            }
            return this;
        }

        /**
         * 管理某些人员
         * 可能该机构不是指定人员的默认机构，而是该人员所属机构的根或者祖先机构
         *
         * @param personIds
         * @return
         */
        public Builder personIdEqual(String... personIds) {
            List<String> strings = newList(personIds);
            if (!strings.isEmpty()) {
                if (query.personIds == null) {
                    query.personIds = new ArrayList<>();
                }
                query.personIds.addAll(strings);
            }
            return this;
        }

        /**
         * 父id等于
         *
         * @param parentIds
         * @return
         */
        public Builder defaultParentIdEqual(String... parentIds) {
            List<String> strings = newList(parentIds);
            if (!strings.isEmpty()) {
                if (query.parentIds == null) {
                    query.parentIds = new ArrayList<>();
                }
                query.parentIds.addAll(strings);
            }
            return this;
        }

        /**
         * 父id不等于
         *
         * @param parentIds
         * @return
         */
        public Builder defaultParentIdNotEqual(String... parentIds) {
            List<String> strings = newList(parentIds);
            if (!strings.isEmpty()) {
                if (query.parentIdNotIn == null) {
                    query.parentIdNotIn = new ArrayList<>();
                }
                query.parentIdNotIn.addAll(strings);
            }
            return this;
        }

        /**
         * 父祖先id等于
         *
         * @param parentIds
         * @return
         */
        public Builder parentIdEqual(String... parentIds) {
            List<String> strings = newList(parentIds);
            if (!strings.isEmpty()) {
                if (query.treeParentId == null) {
                    query.treeParentId = new ArrayList<>();
                }
                query.treeParentId.addAll(strings);
            }
            return this;
        }

        /**
         * 状态等于
         *
         * @param status
         * @return
         */
        public Builder statusEqual(String... status) {
            List<String> strings = newList(status);
            if (!strings.isEmpty()) {
                if (query.status == null) {
                    query.status = new ArrayList<>();
                }
                query.status.addAll(strings);
            }
            return this;
        }

        /**
         * 状态不等于
         *
         * @param status
         * @return
         */
        public Builder statusNotEqual(String... status) {
            List<String> strings = newList(status);
            if (!strings.isEmpty()) {
                if (query.statusNotIn == null) {
                    query.statusNotIn = new ArrayList<>();
                }
                query.statusNotIn.addAll(strings);
            }
            return this;
        }

        /**
         * 创建人员等于
         *
         * @param createPerson
         * @return
         */
        public Builder createPersonEqual(String... createPerson) {
            List<String> strings = newList(createPerson);
            if (!strings.isEmpty()) {
                if (query.createPersons == null) {
                    query.createPersons = new ArrayList<>();
                }
                query.createPersons.addAll(strings);
            }
            return this;
        }

        /**
         * 根据创建日期查询
         *
         * @param start
         * @param end
         * @return
         */
        public Builder createDatebetween(long start, long end) {
            query.setCreateDateEnd(end);
            query.setCreateDateStart(start);
            return this;
        }

        /**
         * 数据来源等于
         *
         * @param refIds
         * @return
         */
        public Builder sourceRefEqual(String... refIds) {
            List<String> strings = newList(refIds);
            if (!strings.isEmpty()) {
                if (query.sourceRef == null) {
                    query.sourceRef = new ArrayList<>();
                }
                query.sourceRef.addAll(strings);
            }
            return this;
        }

        /**
         * 数据来源不等于
         *
         * @param refIds
         * @return
         */
        public Builder sourceRefNotEqual(String... refIds) {
            List<String> strings = newList(refIds);
            if (!strings.isEmpty()) {
                if (query.sourceRefNotIn == null) {
                    query.sourceRefNotIn = new ArrayList<>();
                }
                query.sourceRefNotIn.addAll(strings);
            }
            return this;
        }

        /**
         * 类型like
         *
         * @param type
         * @return
         */
        public Builder typeLike(String type) {
            query.typeLike = type;
            return this;
        }

        /**
         * 类型notlike
         *
         * @param type
         * @return
         */
        public Builder typeNotLike(String type) {
            query.typeNotLike = type;
            return this;
        }

        public Builder codeEqual(String code) {
            query.codeEqual = code;
            return this;
        }

    }


    public String getOrganiseName() {
        return organiseName;
    }

    public void setOrganiseName(String organiseName) {
        this.organiseName = organiseName;
    }

    public List<String> getOrganiseType() {
        return organiseType;
    }

    public void setOrganiseType(List<String> organiseType) {
        this.organiseType = organiseType;
    }

    public List<String> getOrganiseTypeNotIn() {
        return organiseTypeNotIn;
    }

    public void setOrganiseTypeNotIn(List<String> organiseTypeNotIn) {
        this.organiseTypeNotIn = organiseTypeNotIn;
    }


    public List<String> getParentIdNotIn() {
        return parentIdNotIn;
    }

    public void setParentIdNotIn(List<String> parentIdNotIn) {
        this.parentIdNotIn = parentIdNotIn;
    }

    public List<String> getStatus() {
        return status;
    }

    public void setStatus(List<String> status) {
        this.status = status;
    }

    public List<String> getStatusNotIn() {
        return statusNotIn;
    }

    public void setStatusNotIn(List<String> statusNotIn) {
        this.statusNotIn = statusNotIn;
    }

    public List<String> getCreatePersons() {
        return createPersons;
    }

    public void setCreatePersons(List<String> createPersons) {
        this.createPersons = createPersons;
    }

    public Long getCreateDateStart() {
        return createDateStart;
    }

    public void setCreateDateStart(long createDateStart) {
        this.createDateStart = createDateStart;
    }

    public Long getCreateDateEnd() {
        return createDateEnd;
    }

    public void setCreateDateEnd(long createDateEnd) {
        this.createDateEnd = createDateEnd;
    }

    public List<String> getParentIds() {
        return parentIds;
    }

    public void setParentIds(List<String> parentIds) {
        this.parentIds = parentIds;
    }

    public List<String> getDirectPersonIds() {
        return directPersonIds;
    }

    public void setDirectPersonIds(List<String> directPersonIds) {
        this.directPersonIds = directPersonIds;
    }

    public List<String> getPersonIds() {
        return personIds;
    }

    public void setPersonIds(List<String> personIds) {
        this.personIds = personIds;
    }
}
