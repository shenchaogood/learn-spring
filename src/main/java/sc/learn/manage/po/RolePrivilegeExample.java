package sc.learn.manage.po;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RolePrivilegeExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public RolePrivilegeExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andFRoleIdIsNull() {
            addCriterion("f_role_id is null");
            return (Criteria) this;
        }

        public Criteria andFRoleIdIsNotNull() {
            addCriterion("f_role_id is not null");
            return (Criteria) this;
        }

        public Criteria andFRoleIdEqualTo(Integer value) {
            addCriterion("f_role_id =", value, "fRoleId");
            return (Criteria) this;
        }

        public Criteria andFRoleIdNotEqualTo(Integer value) {
            addCriterion("f_role_id <>", value, "fRoleId");
            return (Criteria) this;
        }

        public Criteria andFRoleIdGreaterThan(Integer value) {
            addCriterion("f_role_id >", value, "fRoleId");
            return (Criteria) this;
        }

        public Criteria andFRoleIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("f_role_id >=", value, "fRoleId");
            return (Criteria) this;
        }

        public Criteria andFRoleIdLessThan(Integer value) {
            addCriterion("f_role_id <", value, "fRoleId");
            return (Criteria) this;
        }

        public Criteria andFRoleIdLessThanOrEqualTo(Integer value) {
            addCriterion("f_role_id <=", value, "fRoleId");
            return (Criteria) this;
        }

        public Criteria andFRoleIdIn(List<Integer> values) {
            addCriterion("f_role_id in", values, "fRoleId");
            return (Criteria) this;
        }

        public Criteria andFRoleIdNotIn(List<Integer> values) {
            addCriterion("f_role_id not in", values, "fRoleId");
            return (Criteria) this;
        }

        public Criteria andFRoleIdBetween(Integer value1, Integer value2) {
            addCriterion("f_role_id between", value1, value2, "fRoleId");
            return (Criteria) this;
        }

        public Criteria andFRoleIdNotBetween(Integer value1, Integer value2) {
            addCriterion("f_role_id not between", value1, value2, "fRoleId");
            return (Criteria) this;
        }

        public Criteria andFPrivilegeIdIsNull() {
            addCriterion("f_privilege_id is null");
            return (Criteria) this;
        }

        public Criteria andFPrivilegeIdIsNotNull() {
            addCriterion("f_privilege_id is not null");
            return (Criteria) this;
        }

        public Criteria andFPrivilegeIdEqualTo(Integer value) {
            addCriterion("f_privilege_id =", value, "fPrivilegeId");
            return (Criteria) this;
        }

        public Criteria andFPrivilegeIdNotEqualTo(Integer value) {
            addCriterion("f_privilege_id <>", value, "fPrivilegeId");
            return (Criteria) this;
        }

        public Criteria andFPrivilegeIdGreaterThan(Integer value) {
            addCriterion("f_privilege_id >", value, "fPrivilegeId");
            return (Criteria) this;
        }

        public Criteria andFPrivilegeIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("f_privilege_id >=", value, "fPrivilegeId");
            return (Criteria) this;
        }

        public Criteria andFPrivilegeIdLessThan(Integer value) {
            addCriterion("f_privilege_id <", value, "fPrivilegeId");
            return (Criteria) this;
        }

        public Criteria andFPrivilegeIdLessThanOrEqualTo(Integer value) {
            addCriterion("f_privilege_id <=", value, "fPrivilegeId");
            return (Criteria) this;
        }

        public Criteria andFPrivilegeIdIn(List<Integer> values) {
            addCriterion("f_privilege_id in", values, "fPrivilegeId");
            return (Criteria) this;
        }

        public Criteria andFPrivilegeIdNotIn(List<Integer> values) {
            addCriterion("f_privilege_id not in", values, "fPrivilegeId");
            return (Criteria) this;
        }

        public Criteria andFPrivilegeIdBetween(Integer value1, Integer value2) {
            addCriterion("f_privilege_id between", value1, value2, "fPrivilegeId");
            return (Criteria) this;
        }

        public Criteria andFPrivilegeIdNotBetween(Integer value1, Integer value2) {
            addCriterion("f_privilege_id not between", value1, value2, "fPrivilegeId");
            return (Criteria) this;
        }

        public Criteria andFCreateTimeIsNull() {
            addCriterion("f_create_time is null");
            return (Criteria) this;
        }

        public Criteria andFCreateTimeIsNotNull() {
            addCriterion("f_create_time is not null");
            return (Criteria) this;
        }

        public Criteria andFCreateTimeEqualTo(Date value) {
            addCriterion("f_create_time =", value, "fCreateTime");
            return (Criteria) this;
        }

        public Criteria andFCreateTimeNotEqualTo(Date value) {
            addCriterion("f_create_time <>", value, "fCreateTime");
            return (Criteria) this;
        }

        public Criteria andFCreateTimeGreaterThan(Date value) {
            addCriterion("f_create_time >", value, "fCreateTime");
            return (Criteria) this;
        }

        public Criteria andFCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("f_create_time >=", value, "fCreateTime");
            return (Criteria) this;
        }

        public Criteria andFCreateTimeLessThan(Date value) {
            addCriterion("f_create_time <", value, "fCreateTime");
            return (Criteria) this;
        }

        public Criteria andFCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("f_create_time <=", value, "fCreateTime");
            return (Criteria) this;
        }

        public Criteria andFCreateTimeIn(List<Date> values) {
            addCriterion("f_create_time in", values, "fCreateTime");
            return (Criteria) this;
        }

        public Criteria andFCreateTimeNotIn(List<Date> values) {
            addCriterion("f_create_time not in", values, "fCreateTime");
            return (Criteria) this;
        }

        public Criteria andFCreateTimeBetween(Date value1, Date value2) {
            addCriterion("f_create_time between", value1, value2, "fCreateTime");
            return (Criteria) this;
        }

        public Criteria andFCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("f_create_time not between", value1, value2, "fCreateTime");
            return (Criteria) this;
        }

        public Criteria andFUpdateTimeIsNull() {
            addCriterion("f_update_time is null");
            return (Criteria) this;
        }

        public Criteria andFUpdateTimeIsNotNull() {
            addCriterion("f_update_time is not null");
            return (Criteria) this;
        }

        public Criteria andFUpdateTimeEqualTo(Date value) {
            addCriterion("f_update_time =", value, "fUpdateTime");
            return (Criteria) this;
        }

        public Criteria andFUpdateTimeNotEqualTo(Date value) {
            addCriterion("f_update_time <>", value, "fUpdateTime");
            return (Criteria) this;
        }

        public Criteria andFUpdateTimeGreaterThan(Date value) {
            addCriterion("f_update_time >", value, "fUpdateTime");
            return (Criteria) this;
        }

        public Criteria andFUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("f_update_time >=", value, "fUpdateTime");
            return (Criteria) this;
        }

        public Criteria andFUpdateTimeLessThan(Date value) {
            addCriterion("f_update_time <", value, "fUpdateTime");
            return (Criteria) this;
        }

        public Criteria andFUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("f_update_time <=", value, "fUpdateTime");
            return (Criteria) this;
        }

        public Criteria andFUpdateTimeIn(List<Date> values) {
            addCriterion("f_update_time in", values, "fUpdateTime");
            return (Criteria) this;
        }

        public Criteria andFUpdateTimeNotIn(List<Date> values) {
            addCriterion("f_update_time not in", values, "fUpdateTime");
            return (Criteria) this;
        }

        public Criteria andFUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("f_update_time between", value1, value2, "fUpdateTime");
            return (Criteria) this;
        }

        public Criteria andFUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("f_update_time not between", value1, value2, "fUpdateTime");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}