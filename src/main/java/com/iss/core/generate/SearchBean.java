package com.iss.core.generate;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 
 * 用来保存自定义查询的相关数据。
 * <p/>
 * 
 * @serial 1.0
 * @since Oct 29, 2014 9:00:04 PM
 */
public class SearchBean {

	/**
	 * 字段名称
	 */
	private String name;
	/**
	 * 字段值 ,当值是boolean时，不等于0则为true，等于0是false
	 */
	private Object value;
	/**
	 * 条件关系，包含：=, != ,<, >, <=, >=, like, notLike,in(1;2;3), notIn(1;2;3),isNull, isNotNull, 
	 * between(1;2),notBetween(1;2),sql
	 */
	private String relation;

	/**
	 * @param name
	 * @param realtion
	 * @param type
	 * @param value
	 */
	public SearchBean(String name, Object value, String relation) {
		this.name = name;
		this.relation = relation;
		this.value = value;
	}

	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE).append("name", name).append("value", value)
				.append("realtion", relation).toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}
}