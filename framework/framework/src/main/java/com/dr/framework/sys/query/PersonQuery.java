package com.dr.framework.sys.query;

import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.sys.entity.*;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 人员查询工具类
 * 请使用{@link Builder}构建查询
 *
 * @author dr
 */
public class PersonQuery {
    private String id;
    private String personName;
    private String nickName;
    private String email;
    private String phone;
    private String qq;
    private String weiChatId;

    private List<String> nation;
    private List<String> loginId;
    private List<String> organiseId;
    private List<String> defaultOrganiseId;


    private Long birthDayStart;
    private Long birthDayEnd;

    private String typeLike;
    private String typeNotLike;
    private List<String> personType;
    private List<String> personTypeNotIn;
    private List<String> status;
    private List<String> statusNotIn;
    private List<String> sourceRef;
    private List<String> sourceRefNotIn;
    private String sysId;


    private PersonQuery() {
    }

    public SqlQuery<Person> toQuery() {
        SqlQuery<Person> query = SqlQuery.from(Person.class)
                .equal(PersonInfo.SYSID, sysId);
        query.equal(PersonInfo.ID, id);
        if (!StringUtils.isEmpty(personName)) {
            query.like(PersonInfo.USERNAME, personName);
        }
        if (!StringUtils.isEmpty(nickName)) {
            query.like(PersonInfo.NICKNAME, nickName);
        }
        if (!StringUtils.isEmpty(email)) {
            query.like(PersonInfo.EMAIL, email);
        }
        if (!StringUtils.isEmpty(phone)) {
            query.like(PersonInfo.PHONE, phone);
        }
        if (!StringUtils.isEmpty(qq)) {
            query.like(PersonInfo.QQ, qq);
        }
        if (!StringUtils.isEmpty(weiChatId)) {
            query.like(PersonInfo.WEICHATID, weiChatId);
        }
        if (!StringUtils.isEmpty(typeLike)) {
            query.like(PersonInfo.PERSONTYPE, typeLike);
        }
        if (!StringUtils.isEmpty(typeNotLike)) {
            query.notLike(PersonInfo.PERSONTYPE, typeNotLike);
        }
        if (nation != null && !nation.isEmpty()) {
            query.in(PersonInfo.NATION, nation);
        }
        if (personType != null && !personType.isEmpty()) {
            query.in(PersonInfo.PERSONTYPE, personType);
        }
        if (personTypeNotIn != null && !personTypeNotIn.isEmpty()) {
            query.notIn(PersonInfo.PERSONTYPE, personTypeNotIn);
        }
        if (status != null && !status.isEmpty()) {
            query.in(PersonInfo.STATUS, status);
        }
        if (statusNotIn != null && !statusNotIn.isEmpty()) {
            query.notIn(PersonInfo.STATUS, statusNotIn);
        }
        if (sourceRef != null && !sourceRef.isEmpty()) {
            query.in(PersonInfo.SOURCEREF, sourceRef);
        }
        if (sourceRefNotIn != null && !sourceRefNotIn.isEmpty()) {
            query.notIn(PersonInfo.SOURCEREF, sourceRefNotIn);
        }
        if (loginId != null && !loginId.isEmpty()) {
            query.in(PersonInfo.ID,
                    SqlQuery.from(UserLogin.class, false)
                            .column(UserLoginInfo.PERSONID)
                            .in(UserLoginInfo.LOGINID, loginId)
            );
        }
        if (organiseId != null && !organiseId.isEmpty()) {
            query.in(PersonInfo.ID,
                    SqlQuery.from(PersonOrganise.class, false)
                            .column(PersonOrganiseInfo.PERSONID)
                            .in(PersonOrganiseInfo.ORGANISEID, organiseId)
            );
        }
        if (defaultOrganiseId != null && !defaultOrganiseId.isEmpty()) {
            query.in(PersonInfo.ID,
                    SqlQuery.from(PersonOrganise.class, false)
                            .column(PersonOrganiseInfo.PERSONID)
                            .in(PersonOrganiseInfo.ORGANISEID, defaultOrganiseId)
                            .equal(PersonOrganiseInfo.ISDEFAULT, true)
            );
        }

        if (birthDayEnd != null && birthDayStart != null && birthDayStart >= birthDayEnd) {
            query.greaterThanEqual(PersonInfo.BIRTHDAY, birthDayStart)
                    .lessThanEqual(PersonInfo.BIRTHDAY, birthDayEnd);
        }

        return query;
    }

    public static class Builder {
        private PersonQuery query = new PersonQuery();

        public Builder() {
            this(SubSystem.DEFAULT_SYSTEM_ID);
        }

        public Builder(String sysd) {
            query.sysId = sysd;
        }

        public Builder idEqual(String id) {
            query.id = id;
            return this;
        }

        public Builder nameLike(String name) {
            query.personName = name;
            return this;
        }

        public Builder nickNameLike(String nickName) {
            query.nickName = nickName;
            return this;
        }

        public Builder emailLike(String email) {
            query.email = email;
            return this;
        }

        public Builder phoneLike(String phone) {
            query.phone = phone;
            return this;
        }

        public Builder qqLike(String qq) {
            query.qq = qq;
            return this;
        }

        public Builder weiChatIdLike(String wx) {
            query.weiChatId = wx;
            return this;
        }

        public Builder nationEqual(String... nation) {
            if (nation.length > 0) {
                if (query.nation == null) {
                    query.nation = new ArrayList<>();
                }
                query.nation.addAll(Arrays.asList(nation));
            }
            return this;
        }

        public Builder loginIdEqual(String... loginId) {
            if (loginId.length > 0) {
                if (query.loginId == null) {
                    query.loginId = new ArrayList<>();
                }
                query.loginId.addAll(Arrays.asList(loginId));
            }
            return this;
        }

        public Builder organiseIdEqual(String... organiseId) {
            if (organiseId.length > 0) {
                if (query.organiseId == null) {
                    query.organiseId = new ArrayList<>();
                }
                query.organiseId.addAll(Arrays.asList(organiseId));
            }
            return this;
        }

        public Builder defaultOrganiseIdEqual(String... defaultOrganiseId) {
            if (defaultOrganiseId.length > 0) {
                if (query.defaultOrganiseId == null) {
                    query.defaultOrganiseId = new ArrayList<>();
                }
                query.defaultOrganiseId.addAll(Arrays.asList(defaultOrganiseId));
            }
            return this;
        }

        public Builder birthdayBetween(long start, long end) {
            query.birthDayStart = start;
            query.birthDayEnd = end;
            return this;
        }

        public Builder typeLike(String type) {
            query.typeLike = type;
            return this;
        }

        public Builder typeNotLike(String type) {
            query.typeNotLike = type;
            return this;
        }

        public Builder typeEqual(String... type) {
            if (type.length > 0) {
                if (query.personType == null) {
                    query.personType = new ArrayList<>();
                }
                query.personType.addAll(Arrays.asList(type));
            }
            return this;
        }

        public Builder typeNotEqual(String... type) {
            if (type.length > 0) {
                if (query.personTypeNotIn == null) {
                    query.personTypeNotIn = new ArrayList<>();
                }
                query.personTypeNotIn.addAll(Arrays.asList(type));
            }
            return this;
        }

        public Builder statusEqual(String... status) {
            if (status.length > 0) {
                if (query.status == null) {
                    query.status = new ArrayList<>();
                }
                query.status.addAll(Arrays.asList(status));
            }
            return this;
        }

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

        public PersonQuery build() {
            return query;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWeiChatId() {
        return weiChatId;
    }

    public void setWeiChatId(String weiChatId) {
        this.weiChatId = weiChatId;
    }

    public List<String> getNation() {
        return nation;
    }

    public void setNation(List<String> nation) {
        this.nation = nation;
    }

    public List<String> getLoginId() {
        return loginId;
    }

    public void setLoginId(List<String> loginId) {
        this.loginId = loginId;
    }

    public List<String> getOrganiseId() {
        return organiseId;
    }

    public void setOrganiseId(List<String> organiseId) {
        this.organiseId = organiseId;
    }

    public List<String> getDefaultOrganiseId() {
        return defaultOrganiseId;
    }

    public void setDefaultOrganiseId(List<String> defaultOrganiseId) {
        this.defaultOrganiseId = defaultOrganiseId;
    }

    public long getBirthDayStart() {
        return birthDayStart;
    }

    public void setBirthDayStart(long birthDayStart) {
        this.birthDayStart = birthDayStart;
    }

    public long getBirthDayEnd() {
        return birthDayEnd;
    }

    public void setBirthDayEnd(long birthDayEnd) {
        this.birthDayEnd = birthDayEnd;
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

    public List<String> getPersonType() {
        return personType;
    }

    public void setPersonType(List<String> personType) {
        this.personType = personType;
    }

    public List<String> getPersonTypeNotIn() {
        return personTypeNotIn;
    }

    public void setPersonTypeNotIn(List<String> personTypeNotIn) {
        this.personTypeNotIn = personTypeNotIn;
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

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }
}
