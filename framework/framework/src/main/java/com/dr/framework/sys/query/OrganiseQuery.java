package com.dr.framework.sys.query;

import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.sys.entity.*;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 组织机构查询类，
 * 请使用{@link Builder}类构建
 *
 * @author dr
 */
public class OrganiseQuery {
    /**
     * 所属系统
     */
    private String sysId;
    /**
     * 主键
     */
    private List<String> ids;
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
     * 父id
     */
    private List<String> parentIds;

    /**
     * 不包含的父id
     */
    private List<String> parentIdNotIn;

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

    public SqlQuery<Organise> toQuery() {
        SqlQuery<Organise> query = SqlQuery.from(Organise.class);
        query.equal(OrganiseInfo.SYSID, sysId);
        if (ids != null && !ids.isEmpty()) {
            query.in(OrganiseInfo.ID, ids);
        }
        if (!StringUtils.isEmpty(organiseName)) {
            query.like(OrganiseInfo.ORGANISENAME, organiseName);
        }
        if (!StringUtils.isEmpty(typeLike)) {
            query.like(OrganiseInfo.ORGANISETYPE, typeLike);
        }
        if (!StringUtils.isEmpty(typeNotLike)) {
            query.notLike(OrganiseInfo.ORGANISETYPE, typeNotLike);
        }
        if (organiseType != null && !organiseType.isEmpty()) {
            query.in(OrganiseInfo.ORGANISETYPE, organiseType);
        }
        if (organiseTypeNotIn != null && !organiseTypeNotIn.isEmpty()) {
            query.notIn(OrganiseInfo.ORGANISETYPE, organiseTypeNotIn);
        }
        if (directPersonIds != null && !directPersonIds.isEmpty()) {
            query.in(OrganiseInfo.ID,
                    SqlQuery.from(PersonOrganise.class, false)
                            .column(PersonOrganiseInfo.ORGANISEID)
                            .equal(PersonOrganiseInfo.ISDEFAULT, true)
                            .in(PersonOrganiseInfo.PERSONID, directPersonIds)
            );
        }
        if (personIds != null && !personIds.isEmpty()) {
            query.in(OrganiseInfo.ID,
                    SqlQuery.from(PersonOrganise.class, false)
                            .column(PersonOrganiseInfo.ORGANISEID)
                            .in(PersonOrganiseInfo.PERSONID, personIds)
            );
        }
        if (parentIds != null && !parentIds.isEmpty()) {
            query.in(OrganiseInfo.PARENTID, parentIds);
        }
        if (parentIdNotIn != null && !parentIdNotIn.isEmpty()) {
            query.notIn(OrganiseInfo.PARENTID, parentIdNotIn);
        }
        if (status != null && !status.isEmpty()) {
            query.in(OrganiseInfo.STATUS, status);
        }
        if (statusNotIn != null && !statusNotIn.isEmpty()) {
            query.notIn(OrganiseInfo.STATUS, statusNotIn);
        }
        if (createPersons != null && !createPersons.isEmpty()) {
            query.in(OrganiseInfo.CREATEPERSON, createPersons);
        }
        if (sourceRef != null && !sourceRef.isEmpty()) {
            query.in(OrganiseInfo.SOURCEREF, sourceRef);
        }
        if (sourceRefNotIn != null && !sourceRefNotIn.isEmpty()) {
            query.notIn(OrganiseInfo.SOURCEREF, sourceRefNotIn);
        }
        if (createDateStart != null && createDateEnd != null && createDateStart >= createDateEnd) {
            query.greaterThanEqual(OrganiseInfo.CREATEDATE, createDateStart)
                    .lessThanEqual(OrganiseInfo.CREATEDATE, createDateEnd);
        }
        return query;
    }

    private OrganiseQuery() {
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
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

    public static class Builder {
        private OrganiseQuery query = new OrganiseQuery();

        public Builder() {
            this(SubSystem.DEFAULT_SYSTEM_ID);
        }

        public Builder(String sysId) {
            query.setSysId(sysId);
        }

        /**
         * 主键等于
         *
         * @param ids
         * @return
         */
        public Builder idEqual(String... ids) {
            if (ids.length > 0) {
                if (query.ids == null) {
                    query.ids = new ArrayList<>();
                }
                query.ids.addAll(Arrays.asList(ids));
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
            if (types.length > 0) {
                if (query.organiseType == null) {
                    query.organiseType = new ArrayList<>();
                }
                query.organiseType.addAll(Arrays.asList(types));
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
            if (types.length > 0) {
                if (query.organiseTypeNotIn == null) {
                    query.organiseTypeNotIn = new ArrayList<>();
                }
                query.organiseTypeNotIn.addAll(Arrays.asList(types));
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
            if (personIds.length > 0) {
                if (query.directPersonIds == null) {
                    query.directPersonIds = new ArrayList<>();
                }
                query.directPersonIds.addAll(Arrays.asList(personIds));
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
            if (personIds.length > 0) {
                if (query.personIds == null) {
                    query.personIds = new ArrayList<>();
                }
                query.personIds.addAll(Arrays.asList(personIds));
            }
            return this;
        }

        /**
         * 父id等于
         *
         * @param parentIds
         * @return
         */
        public Builder parentIdEqual(String... parentIds) {
            if (parentIds.length > 0) {
                if (query.parentIds == null) {
                    query.parentIds = new ArrayList<>();
                }
                query.parentIds.addAll(Arrays.asList(parentIds));
            }
            return this;
        }


        /**
         * 父id不等于
         *
         * @param parentIds
         * @return
         */
        public Builder parentIdNotEqual(String... parentIds) {
            if (parentIds.length > 0) {
                if (query.parentIdNotIn == null) {
                    query.parentIdNotIn = new ArrayList<>();
                }
                query.parentIdNotIn.addAll(Arrays.asList(parentIds));
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
            if (status.length > 0) {
                if (query.status == null) {
                    query.status = new ArrayList<>();
                }
                query.status.addAll(Arrays.asList(status));
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
            if (status.length > 0) {
                if (query.statusNotIn == null) {
                    query.statusNotIn = new ArrayList<>();
                }
                query.statusNotIn.addAll(Arrays.asList(status));
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
            if (createPerson.length > 0) {
                if (query.createPersons == null) {
                    query.createPersons = new ArrayList<>();
                }
                query.createPersons.addAll(Arrays.asList(createPerson));
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
            if (refIds.length > 0) {
                if (query.sourceRef == null) {
                    query.sourceRef = new ArrayList<>();
                }
                query.sourceRef.addAll(Arrays.asList(refIds));
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
            if (refIds.length > 0) {
                if (query.sourceRefNotIn == null) {
                    query.sourceRefNotIn = new ArrayList<>();
                }
                query.sourceRefNotIn.addAll(Arrays.asList(refIds));
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


        public OrganiseQuery build() {
            return query;
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

    public long getCreateDateStart() {
        return createDateStart;
    }

    public void setCreateDateStart(long createDateStart) {
        this.createDateStart = createDateStart;
    }

    public long getCreateDateEnd() {
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
