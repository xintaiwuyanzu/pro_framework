package com.dr.framework.core.organise.query;

import com.dr.framework.common.query.IdQuery;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 人员查询工具类
 * 请使用{@link Builder}构建查询
 *
 * @author dr
 */
public class PersonQuery extends IdQuery {
    private String personName;
    private String nickName;
    private String email;
    private String phone;
    private String qq;
    private String idNo;
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
    private String userCode;
    private String duty;

    private String createPerson;

    private Long createDayStart;
    private Long createDayEnd;


    public static class Builder extends IdQuery.Builder<PersonQuery, Builder> {
        private PersonQuery query = new PersonQuery();

        private List<String> newList(String... strings) {
            return Arrays.asList(strings)
                    .stream()
                    .filter(s -> !StringUtils.isEmpty(s))
                    .collect(Collectors.toList());
        }


        public Builder nameLike(String name) {
            query.personName = name;
            return this;
        }

        public Builder userCodeLike(String userCode) {
            query.userCode = userCode;
            return this;
        }

        public Builder dutyLike(String duty) {
            query.duty = duty;
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
            List<String> strings = newList(nation);
            if (!strings.isEmpty()) {
                if (query.nation == null) {
                    query.nation = new ArrayList<>();
                }
                query.nation.addAll(strings);
            }
            return this;
        }

        public Builder loginIdEqual(String... loginId) {
            List<String> strings = newList(loginId);
            if (!strings.isEmpty()) {
                if (query.loginId == null) {
                    query.loginId = new ArrayList<>();
                }
                query.loginId.addAll(strings);
            }
            return this;
        }

        public Builder organiseIdEqual(Collection<String> orgIds) {
            if (orgIds != null && !orgIds.isEmpty()) {
                if (query.organiseId == null) {
                    query.organiseId = new ArrayList<>();
                }
                query.organiseId.addAll(orgIds);
            }
            return this;
        }


        public Builder organiseIdEqual(String... organiseId) {
            List<String> strings = newList(organiseId);
            if (!strings.isEmpty()) {
                if (query.organiseId == null) {
                    query.organiseId = new ArrayList<>();
                }
                query.organiseId.addAll(strings);
            }
            return this;
        }

        public Builder defaultOrganiseIdEqual(String... defaultOrganiseId) {
            List<String> strings = newList(defaultOrganiseId);
            if (!strings.isEmpty()) {
                if (query.defaultOrganiseId == null) {
                    query.defaultOrganiseId = new ArrayList<>();
                }
                query.defaultOrganiseId.addAll(strings);
            }
            return this;
        }

        public Builder birthdayBetween(long start, long end) {
            query.birthDayStart = start;
            query.birthDayEnd = end;
            return this;
        }

        public Builder createDayBetween(long start, long end) {
            query.createDayStart = start;
            query.createDayEnd = end;
            return this;
        }

        public Builder idNoLike(String idNo) {
            if (!StringUtils.isEmpty(idNo)) {
                query.setIdNo(idNo);
            }
            return this;
        }

        public Builder createPersonEqual(String createPerson) {
            if (!StringUtils.isEmpty(createPerson)) {
                query.setCreatePerson(createPerson);
            }
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
            List<String> strings = newList(type);
            if (!strings.isEmpty()) {
                if (query.personType == null) {
                    query.personType = new ArrayList<>();
                }
                query.personType.addAll(strings);
            }
            return this;
        }

        public Builder typeNotEqual(String... type) {
            List<String> strings = newList(type);
            if (!strings.isEmpty()) {
                if (query.personTypeNotIn == null) {
                    query.personTypeNotIn = new ArrayList<>();
                }
                query.personTypeNotIn.addAll(strings);
            }
            return this;
        }

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

        @Override
        public PersonQuery getQuery() {
            return query;
        }
    }

    private PersonQuery() {
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
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

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
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

    public Long getBirthDayStart() {
        return birthDayStart;
    }

    public void setBirthDayStart(long birthDayStart) {
        this.birthDayStart = birthDayStart;
    }

    public Long getBirthDayEnd() {
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

    public String getCreatePerson() {
        return createPerson;
    }

    public void setCreatePerson(String createPerson) {
        this.createPerson = createPerson;
    }

    public Long getCreateDayStart() {
        return createDayStart;
    }

    public void setCreateDayStart(Long createDayStart) {
        this.createDayStart = createDayStart;
    }

    public Long getCreateDayEnd() {
        return createDayEnd;
    }

    public void setCreateDayEnd(Long createDayEnd) {
        this.createDayEnd = createDayEnd;
    }
}
