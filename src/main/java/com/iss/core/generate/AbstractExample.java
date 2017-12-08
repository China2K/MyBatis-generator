package com.iss.core.generate;

import java.util.List;

public abstract class AbstractExample<T extends BaseEntity> {

	public abstract void setOrderByClause(String orderByClause);

	public abstract String getOrderByClause();

	public abstract void setDistinct(boolean distinct);

	public abstract boolean isDistinct();

	public abstract List<? extends AbstractCriteria<T>> getOredCriteria();

	public abstract AbstractCriteria<T> or();

	public abstract AbstractCriteria<T> createCriteria();

	public abstract void clear();

	public abstract void setLimit(Integer limit);

	public abstract Integer getLimit();

	public abstract void setOffset(Integer offset);

	public abstract Integer getOffset();

	public abstract void enableAllAssociation();

	public abstract void disableAllAssociation();
}
