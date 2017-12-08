package com.iss.core.generate;

public enum BaseSqlOperator {

	SQL("sql"), IS_NULL("is null"), IS_NOT_NULL("is not null"), // one_line
	EQUAL("="), NOT_EQUAL("<>"), GREATER(">"), GREATER_OR_EQUAL(">="), // one_line
	LESS("<"), LESS_OR_EQUAL("<="), IN("in"), NOT_IN("not in"), // one_line
	BETWEEN("between"), NOT_BETWEEN("not between"), LIKE("like"), NOT_LIKE("not like");

	private String operator;

	private BaseSqlOperator(String operator) {
		this.operator = operator;
	}

	public String getOperator() {
		return operator;
	}

	public boolean isNoValue() {
		boolean truth = false;
		if (this == IS_NULL) {
			truth = true;
		} else if (this == IS_NOT_NULL) {
			truth = true;
		} else if (this == SQL) {
			truth = true;
		}
		return truth;
	}

	public boolean isSingleValue() {
		boolean truth = true;
		if (isNoValue()) {
			truth = false;
		} else if (isBetweenValue()) {
			truth = false;
		} else if (isArrayValue()) {
			truth = false;
		}
		return truth;
	}

	public boolean isStringValue() {
		boolean truth = false;
		if (this == LIKE) {
			truth = true;
		} else if (this == NOT_LIKE) {
			truth = true;
		}
		return truth;
	}

	public boolean isBetweenValue() {
		boolean truth = false;
		if (this == BETWEEN) {
			truth = true;
		} else if (this == NOT_BETWEEN) {
			truth = true;
		}
		return truth;
	}

	public boolean isArrayValue() {
		boolean truth = false;
		if (this == IN) {
			truth = true;
		} else if (this == NOT_IN) {
			truth = true;
		}
		return truth;
	}

}
