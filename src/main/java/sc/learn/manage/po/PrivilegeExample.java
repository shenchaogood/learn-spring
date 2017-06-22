package sc.learn.manage.po;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrivilegeExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PrivilegeExample() {
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

        public Criteria andFIdIsNull() {
            addCriterion("f_id is null");
            return (Criteria) this;
        }

        public Criteria andFIdIsNotNull() {
            addCriterion("f_id is not null");
            return (Criteria) this;
        }

        public Criteria andFIdEqualTo(Integer value) {
            addCriterion("f_id =", value, "fId");
            return (Criteria) this;
        }

        public Criteria andFIdNotEqualTo(Integer value) {
            addCriterion("f_id <>", value, "fId");
            return (Criteria) this;
        }

        public Criteria andFIdGreaterThan(Integer value) {
            addCriterion("f_id >", value, "fId");
            return (Criteria) this;
        }

        public Criteria andFIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("f_id >=", value, "fId");
            return (Criteria) this;
        }

        public Criteria andFIdLessThan(Integer value) {
            addCriterion("f_id <", value, "fId");
            return (Criteria) this;
        }

        public Criteria andFIdLessThanOrEqualTo(Integer value) {
            addCriterion("f_id <=", value, "fId");
            return (Criteria) this;
        }

        public Criteria andFIdIn(List<Integer> values) {
            addCriterion("f_id in", values, "fId");
            return (Criteria) this;
        }

        public Criteria andFIdNotIn(List<Integer> values) {
            addCriterion("f_id not in", values, "fId");
            return (Criteria) this;
        }

        public Criteria andFIdBetween(Integer value1, Integer value2) {
            addCriterion("f_id between", value1, value2, "fId");
            return (Criteria) this;
        }

        public Criteria andFIdNotBetween(Integer value1, Integer value2) {
            addCriterion("f_id not between", value1, value2, "fId");
            return (Criteria) this;
        }

        public Criteria andFNameIsNull() {
            addCriterion("f_name is null");
            return (Criteria) this;
        }

        public Criteria andFNameIsNotNull() {
            addCriterion("f_name is not null");
            return (Criteria) this;
        }

        public Criteria andFNameEqualTo(String value) {
            addCriterion("f_name =", value, "fName");
            return (Criteria) this;
        }

        public Criteria andFNameNotEqualTo(String value) {
            addCriterion("f_name <>", value, "fName");
            return (Criteria) this;
        }

        public Criteria andFNameGreaterThan(String value) {
            addCriterion("f_name >", value, "fName");
            return (Criteria) this;
        }

        public Criteria andFNameGreaterThanOrEqualTo(String value) {
            addCriterion("f_name >=", value, "fName");
            return (Criteria) this;
        }

        public Criteria andFNameLessThan(String value) {
            addCriterion("f_name <", value, "fName");
            return (Criteria) this;
        }

        public Criteria andFNameLessThanOrEqualTo(String value) {
            addCriterion("f_name <=", value, "fName");
            return (Criteria) this;
        }

        public Criteria andFNameLike(String value) {
            addCriterion("f_name like", value, "fName");
            return (Criteria) this;
        }

        public Criteria andFNameNotLike(String value) {
            addCriterion("f_name not like", value, "fName");
            return (Criteria) this;
        }

        public Criteria andFNameIn(List<String> values) {
            addCriterion("f_name in", values, "fName");
            return (Criteria) this;
        }

        public Criteria andFNameNotIn(List<String> values) {
            addCriterion("f_name not in", values, "fName");
            return (Criteria) this;
        }

        public Criteria andFNameBetween(String value1, String value2) {
            addCriterion("f_name between", value1, value2, "fName");
            return (Criteria) this;
        }

        public Criteria andFNameNotBetween(String value1, String value2) {
            addCriterion("f_name not between", value1, value2, "fName");
            return (Criteria) this;
        }

        public Criteria andFDescriptionIsNull() {
            addCriterion("f_description is null");
            return (Criteria) this;
        }

        public Criteria andFDescriptionIsNotNull() {
            addCriterion("f_description is not null");
            return (Criteria) this;
        }

        public Criteria andFDescriptionEqualTo(String value) {
            addCriterion("f_description =", value, "fDescription");
            return (Criteria) this;
        }

        public Criteria andFDescriptionNotEqualTo(String value) {
            addCriterion("f_description <>", value, "fDescription");
            return (Criteria) this;
        }

        public Criteria andFDescriptionGreaterThan(String value) {
            addCriterion("f_description >", value, "fDescription");
            return (Criteria) this;
        }

        public Criteria andFDescriptionGreaterThanOrEqualTo(String value) {
            addCriterion("f_description >=", value, "fDescription");
            return (Criteria) this;
        }

        public Criteria andFDescriptionLessThan(String value) {
            addCriterion("f_description <", value, "fDescription");
            return (Criteria) this;
        }

        public Criteria andFDescriptionLessThanOrEqualTo(String value) {
            addCriterion("f_description <=", value, "fDescription");
            return (Criteria) this;
        }

        public Criteria andFDescriptionLike(String value) {
            addCriterion("f_description like", value, "fDescription");
            return (Criteria) this;
        }

        public Criteria andFDescriptionNotLike(String value) {
            addCriterion("f_description not like", value, "fDescription");
            return (Criteria) this;
        }

        public Criteria andFDescriptionIn(List<String> values) {
            addCriterion("f_description in", values, "fDescription");
            return (Criteria) this;
        }

        public Criteria andFDescriptionNotIn(List<String> values) {
            addCriterion("f_description not in", values, "fDescription");
            return (Criteria) this;
        }

        public Criteria andFDescriptionBetween(String value1, String value2) {
            addCriterion("f_description between", value1, value2, "fDescription");
            return (Criteria) this;
        }

        public Criteria andFDescriptionNotBetween(String value1, String value2) {
            addCriterion("f_description not between", value1, value2, "fDescription");
            return (Criteria) this;
        }

        public Criteria andFUrlIsNull() {
            addCriterion("f_url is null");
            return (Criteria) this;
        }

        public Criteria andFUrlIsNotNull() {
            addCriterion("f_url is not null");
            return (Criteria) this;
        }

        public Criteria andFUrlEqualTo(String value) {
            addCriterion("f_url =", value, "fUrl");
            return (Criteria) this;
        }

        public Criteria andFUrlNotEqualTo(String value) {
            addCriterion("f_url <>", value, "fUrl");
            return (Criteria) this;
        }

        public Criteria andFUrlGreaterThan(String value) {
            addCriterion("f_url >", value, "fUrl");
            return (Criteria) this;
        }

        public Criteria andFUrlGreaterThanOrEqualTo(String value) {
            addCriterion("f_url >=", value, "fUrl");
            return (Criteria) this;
        }

        public Criteria andFUrlLessThan(String value) {
            addCriterion("f_url <", value, "fUrl");
            return (Criteria) this;
        }

        public Criteria andFUrlLessThanOrEqualTo(String value) {
            addCriterion("f_url <=", value, "fUrl");
            return (Criteria) this;
        }

        public Criteria andFUrlLike(String value) {
            addCriterion("f_url like", value, "fUrl");
            return (Criteria) this;
        }

        public Criteria andFUrlNotLike(String value) {
            addCriterion("f_url not like", value, "fUrl");
            return (Criteria) this;
        }

        public Criteria andFUrlIn(List<String> values) {
            addCriterion("f_url in", values, "fUrl");
            return (Criteria) this;
        }

        public Criteria andFUrlNotIn(List<String> values) {
            addCriterion("f_url not in", values, "fUrl");
            return (Criteria) this;
        }

        public Criteria andFUrlBetween(String value1, String value2) {
            addCriterion("f_url between", value1, value2, "fUrl");
            return (Criteria) this;
        }

        public Criteria andFUrlNotBetween(String value1, String value2) {
            addCriterion("f_url not between", value1, value2, "fUrl");
            return (Criteria) this;
        }

        public Criteria andFIconIsNull() {
            addCriterion("f_icon is null");
            return (Criteria) this;
        }

        public Criteria andFIconIsNotNull() {
            addCriterion("f_icon is not null");
            return (Criteria) this;
        }

        public Criteria andFIconEqualTo(String value) {
            addCriterion("f_icon =", value, "fIcon");
            return (Criteria) this;
        }

        public Criteria andFIconNotEqualTo(String value) {
            addCriterion("f_icon <>", value, "fIcon");
            return (Criteria) this;
        }

        public Criteria andFIconGreaterThan(String value) {
            addCriterion("f_icon >", value, "fIcon");
            return (Criteria) this;
        }

        public Criteria andFIconGreaterThanOrEqualTo(String value) {
            addCriterion("f_icon >=", value, "fIcon");
            return (Criteria) this;
        }

        public Criteria andFIconLessThan(String value) {
            addCriterion("f_icon <", value, "fIcon");
            return (Criteria) this;
        }

        public Criteria andFIconLessThanOrEqualTo(String value) {
            addCriterion("f_icon <=", value, "fIcon");
            return (Criteria) this;
        }

        public Criteria andFIconLike(String value) {
            addCriterion("f_icon like", value, "fIcon");
            return (Criteria) this;
        }

        public Criteria andFIconNotLike(String value) {
            addCriterion("f_icon not like", value, "fIcon");
            return (Criteria) this;
        }

        public Criteria andFIconIn(List<String> values) {
            addCriterion("f_icon in", values, "fIcon");
            return (Criteria) this;
        }

        public Criteria andFIconNotIn(List<String> values) {
            addCriterion("f_icon not in", values, "fIcon");
            return (Criteria) this;
        }

        public Criteria andFIconBetween(String value1, String value2) {
            addCriterion("f_icon between", value1, value2, "fIcon");
            return (Criteria) this;
        }

        public Criteria andFIconNotBetween(String value1, String value2) {
            addCriterion("f_icon not between", value1, value2, "fIcon");
            return (Criteria) this;
        }

        public Criteria andFLeafIsNull() {
            addCriterion("f_leaf is null");
            return (Criteria) this;
        }

        public Criteria andFLeafIsNotNull() {
            addCriterion("f_leaf is not null");
            return (Criteria) this;
        }

        public Criteria andFLeafEqualTo(Boolean value) {
            addCriterion("f_leaf =", value, "fLeaf");
            return (Criteria) this;
        }

        public Criteria andFLeafNotEqualTo(Boolean value) {
            addCriterion("f_leaf <>", value, "fLeaf");
            return (Criteria) this;
        }

        public Criteria andFLeafGreaterThan(Boolean value) {
            addCriterion("f_leaf >", value, "fLeaf");
            return (Criteria) this;
        }

        public Criteria andFLeafGreaterThanOrEqualTo(Boolean value) {
            addCriterion("f_leaf >=", value, "fLeaf");
            return (Criteria) this;
        }

        public Criteria andFLeafLessThan(Boolean value) {
            addCriterion("f_leaf <", value, "fLeaf");
            return (Criteria) this;
        }

        public Criteria andFLeafLessThanOrEqualTo(Boolean value) {
            addCriterion("f_leaf <=", value, "fLeaf");
            return (Criteria) this;
        }

        public Criteria andFLeafIn(List<Boolean> values) {
            addCriterion("f_leaf in", values, "fLeaf");
            return (Criteria) this;
        }

        public Criteria andFLeafNotIn(List<Boolean> values) {
            addCriterion("f_leaf not in", values, "fLeaf");
            return (Criteria) this;
        }

        public Criteria andFLeafBetween(Boolean value1, Boolean value2) {
            addCriterion("f_leaf between", value1, value2, "fLeaf");
            return (Criteria) this;
        }

        public Criteria andFLeafNotBetween(Boolean value1, Boolean value2) {
            addCriterion("f_leaf not between", value1, value2, "fLeaf");
            return (Criteria) this;
        }

        public Criteria andFParentIdIsNull() {
            addCriterion("f_parent_id is null");
            return (Criteria) this;
        }

        public Criteria andFParentIdIsNotNull() {
            addCriterion("f_parent_id is not null");
            return (Criteria) this;
        }

        public Criteria andFParentIdEqualTo(Integer value) {
            addCriterion("f_parent_id =", value, "fParentId");
            return (Criteria) this;
        }

        public Criteria andFParentIdNotEqualTo(Integer value) {
            addCriterion("f_parent_id <>", value, "fParentId");
            return (Criteria) this;
        }

        public Criteria andFParentIdGreaterThan(Integer value) {
            addCriterion("f_parent_id >", value, "fParentId");
            return (Criteria) this;
        }

        public Criteria andFParentIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("f_parent_id >=", value, "fParentId");
            return (Criteria) this;
        }

        public Criteria andFParentIdLessThan(Integer value) {
            addCriterion("f_parent_id <", value, "fParentId");
            return (Criteria) this;
        }

        public Criteria andFParentIdLessThanOrEqualTo(Integer value) {
            addCriterion("f_parent_id <=", value, "fParentId");
            return (Criteria) this;
        }

        public Criteria andFParentIdIn(List<Integer> values) {
            addCriterion("f_parent_id in", values, "fParentId");
            return (Criteria) this;
        }

        public Criteria andFParentIdNotIn(List<Integer> values) {
            addCriterion("f_parent_id not in", values, "fParentId");
            return (Criteria) this;
        }

        public Criteria andFParentIdBetween(Integer value1, Integer value2) {
            addCriterion("f_parent_id between", value1, value2, "fParentId");
            return (Criteria) this;
        }

        public Criteria andFParentIdNotBetween(Integer value1, Integer value2) {
            addCriterion("f_parent_id not between", value1, value2, "fParentId");
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