package com.iss.core.generate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.transaction.annotation.Transactional;


@Transactional(rollbackFor = Exception.class)
public abstract class BaseServiceImpl<T extends BaseEntity, DTO extends T, E extends AbstractExample<T>, ID extends Serializable>
		implements IBaseService<T, DTO, E, ID> {

	public abstract SqlMapper<T, DTO, E, ID> getGeneratedSqlMapper();

	protected Class<T> entityClazz;
	protected Class<E> exampleClazz;

	@SuppressWarnings("unchecked")
	public BaseServiceImpl() {
		Class<?> thisClass = this.getClass();
		if (thisClass.getSuperclass() != BaseServiceImpl.class) {
			thisClass = thisClass.getSuperclass();
		}
		ParameterizedType type = (ParameterizedType) thisClass.getGenericSuperclass();
		entityClazz = (Class<T>) type.getActualTypeArguments()[0];
		exampleClazz = (Class<E>) type.getActualTypeArguments()[2];
	}

	@Deprecated
	public SearchBean[] convert2SearchBean(String param) {
		if (StringUtils.isBlank(param)) {
			return new SearchBean[] {};
		}
		String[] paramStrs = param.split(",");
		List<SearchBean> list = new ArrayList<SearchBean>();
		for (String paramStr : paramStrs) {
			String[] search = paramStr.split(":");
			SearchBean searchBean = null;
			if (search.length < 2) {
				continue;
			}
			if (search.length == 2) {
				String relation = search[1];
				if ("isNull".equals(relation)) {
					searchBean = new SearchBean(search[0], null, relation);
				} else if ("isNotNull".equals(relation)) {
					searchBean = new SearchBean(search[0], null, relation);
				} else if ("sql".equals(relation)) {
					searchBean = new SearchBean(null, search[0], search[1]);
				}
				list.add(searchBean);
			}
			if (search.length == 3) {
				String value = search[1];
				if (StringUtils.isBlank(value)) {
					continue;
				}
				if (search[2].trim().equals("in")) {
					List<String> inVals = new ArrayList<String>();
					inVals.addAll(Arrays.asList(value.split(";")));
					searchBean = new SearchBean(search[0], inVals, search[2]);
				} else if (search[2].trim().equals("notIn")) {
					List<String> inVals = new ArrayList<String>();
					inVals.addAll(Arrays.asList(value.split(";")));
					searchBean = new SearchBean(search[0], inVals, search[2]);
				} else if (search[2].trim().equals("between")) {
					String[] betweenVals = value.split(";");
					searchBean = new SearchBean(search[0], Pair.of(betweenVals[0], betweenVals[1]), search[2]);
				} else if (search[2].trim().equals("notBetween")) {
					String[] betweenVals = value.split(";");
					searchBean = new SearchBean(search[0], Pair.of(betweenVals[0], betweenVals[1]), search[2]);
				} else if (search[2].trim().equals("like")) {
					String likeVal = "%" + search[1].toString() + "%";
					searchBean = new SearchBean(search[0], likeVal, search[2]);
				} else if (search[2].trim().equals("notLike")) {
					String likeVal = "%" + search[1].toString() + "%";
					searchBean = new SearchBean(search[0], likeVal, search[2]);
				} else {
					searchBean = new SearchBean(search[0], search[1], search[2]);
				}
				list.add(searchBean);
			}
		}
		return list.toArray(new SearchBean[] {});
	}

	@Override
	public List<SearchBean[]> convert2OrSearchBean(String param) {
		if (StringUtils.isBlank(param)) {
			return new ArrayList<SearchBean[]>();
		}
		List<SearchBean[]> searchBeansAry = new ArrayList<SearchBean[]>();
		String[] paramStrsAry = param.split("\\|\\|");
		for (String paramStrs : paramStrsAry) {
			String[] paramStrAry = paramStrs.split(",");
			List<SearchBean> list = new ArrayList<SearchBean>();
			for (String paramStr : paramStrAry) {
				String[] search = paramStr.split(":");
				SearchBean searchBean = null;
				if (search.length < 2) {
					continue;
				}
				if (search.length == 2) {
					String relation = search[1];
					if ("isNull".equals(relation)) {
						searchBean = new SearchBean(search[0], null, relation);
					} else if ("isNotNull".equals(relation)) {
						searchBean = new SearchBean(search[0], null, relation);
					} else if ("sql".equals(relation)) {
						searchBean = new SearchBean(null, search[0], search[1]);
					}
					list.add(searchBean);
				}
				if (search.length == 3) {
					String value = search[1];
					if (StringUtils.isBlank(value)) {
						continue;
					}
					if (search[2].trim().equals("in")) {
						List<String> inVals = new ArrayList<String>();
						inVals.addAll(Arrays.asList(value.split(";")));
						searchBean = new SearchBean(search[0], inVals, search[2]);
					} else if (search[2].trim().equals("notIn")) {
						List<String> inVals = new ArrayList<String>();
						inVals.addAll(Arrays.asList(value.split(";")));
						searchBean = new SearchBean(search[0], inVals, search[2]);
					} else if (search[2].trim().equals("between")) {
						String[] betweenVals = value.split(";");
						searchBean = new SearchBean(search[0], Pair.of(betweenVals[0], betweenVals[1]), search[2]);
					} else if (search[2].trim().equals("notBetween")) {
						String[] betweenVals = value.split(";");
						searchBean = new SearchBean(search[0], Pair.of(betweenVals[0], betweenVals[1]), search[2]);
					} else if (search[2].trim().equals("like")) {
						String likeVal = "%" + search[1].toString() + "%";
						searchBean = new SearchBean(search[0], likeVal, search[2]);
					} else if (search[2].trim().equals("notLike")) {
						String likeVal = "%" + search[1].toString() + "%";
						searchBean = new SearchBean(search[0], likeVal, search[2]);
					} else {
						searchBean = new SearchBean(search[0], search[1], search[2]);
					}
					list.add(searchBean);
				}
			}
			searchBeansAry.add(list.toArray(new SearchBean[] {}));
		}
		return searchBeansAry;
	}

	@Deprecated
	public E convert2Example(String param) {
		return convertSearchBean2Example(convert2SearchBean(param));
	}

	@Override
	public E convert2OrExample(String param) {
		return convertSearchBean2OrExample(convert2OrSearchBean(param));
	}

	@Deprecated
	public E convertSearchBean2Example(SearchBean[] searchBeans) {
		try {
			if (searchBeans != null && searchBeans.length > 0) {
				E example = getExample();
				AbstractCriteria<T> criteria = example.createCriteria();
				for (SearchBean searchBean : searchBeans) {
					String relation = searchBean.getRelation();
					if (relation == null) {
						break;
					}
					transCriteria(searchBean, criteria);
				}
				return example;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getExample();
	}

	@Override
	public E convertSearchBean2OrExample(List<SearchBean[]> searchBeansAry) {
		try {
			E example = getExample();
			for (SearchBean[] searchBeans : searchBeansAry) {
				if (searchBeans != null && searchBeans.length > 0) {
					AbstractCriteria<T> criteria = example.or();
					for (SearchBean searchBean : searchBeans) {
						String relation = searchBean.getRelation();
						if (relation == null) {
							break;
						}
						transCriteria(searchBean, criteria);
					}
				}
			}
			return example;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getExample();
	}

	@Override
	public Page<DTO> pageFind(String orderByClause, int offset, int limit, SearchBean... searchBeans)
			throws BasicException {
		E example = getExample();
		AbstractCriteria<T> criteria = example.createCriteria();
		example.setLimit(limit);
		example.setOffset(offset);
		example.setOrderByClause(orderByClause);
		if (searchBeans != null) {
			for (SearchBean searchBean : searchBeans) {
				String relation = searchBean.getRelation();
				if (relation == null) {
					break;
				}
				transCriteria(searchBean, criteria);
			}
		}
		return new PageImpl<DTO>(selectByExample(example), null, countByExample(example));
	}

	@Override
	public Page<DTO> pageFind(String orderByClause, int offset, int limit, E example) throws BasicException {
		example.setLimit(limit);
		example.setOffset(offset);
		example.setOrderByClause(orderByClause);
		return new PageImpl<DTO>(selectByExample(example), null, countByExample(example));
	}

	@Override
	public Page<DTO> pageFind(int offset, int limit, SearchBean... searchBeans) throws BasicException {
		E example = getExample();
		AbstractCriteria<T> criteria = example.createCriteria();
		example.setLimit(limit);
		example.setOffset(offset);
		if (searchBeans != null) {
			for (SearchBean searchBean : searchBeans) {
				String relation = searchBean.getRelation();
				if (relation == null) {
					break;
				}
				transCriteria(searchBean, criteria);
			}
		}
		return new PageImpl<DTO>(selectByExample(example), null, countByExample(example));
	}

	@Override
	public Page<DTO> pageFind(int offset, int limit, E example) throws BasicException {
		example.setLimit(limit);
		example.setOffset(offset);
		return new PageImpl<DTO>(selectByExample(example), null, countByExample(example));
	}

	@Override
	public Page<DTO> pageFind(E example) throws BasicException {
		return new PageImpl<DTO>(selectByExample(example), null, countByExample(example));
	}

	@Override
	public List<DTO> find(E example) throws BasicException {
		return selectByExample(example);
	}

	@Override
	public DTO findUnique(E example) throws BasicException {
		List<DTO> dtos = selectByExample(example);
		if (dtos.size() == 0) {
			return null;
		}
		if (dtos.size() != 1) {
			throw new BasicException("find " + dtos.size() + " records in unique query");
		}
		return selectByExample(example).get(0);
	}

	@Override
	public long count(SearchBean... searchBeans) throws BasicException {
		if (searchBeans == null) {
			return 0;
		}
		E example = getExample();
		AbstractCriteria<T> criteria = example.createCriteria();
		if (searchBeans != null) {
			for (SearchBean searchBean : searchBeans) {
				String relation = searchBean.getRelation();
				if (relation == null) {
					break;
				}
				transCriteria(searchBean, criteria);
			}
		}
		return countByExample(example);
	}

	@Override
	public void transCriteria(SearchBean searchBean, AbstractCriteria<T> criteria) throws BasicException {
		String relation = searchBean.getRelation();
		if (relation.equals("sql")) {
			if (StringUtils.isNotBlank(searchBean.getName())) {
				throw new BasicException("when searchBean is [sql] type,property name should be blank");
			}
			criteria.AddCondition(BaseSqlOperator.SQL, null, searchBean.getValue().toString());
		} else if (relation.equals("=")) {
			criteria.AddCondition(BaseSqlOperator.EQUAL, searchBean.getName(), searchBean.getValue());
		} else if (relation.equals("<=")) {
			criteria.AddCondition(BaseSqlOperator.LESS_OR_EQUAL, searchBean.getName(), searchBean.getValue());
		} else if (relation.equals("<")) {
			criteria.AddCondition(BaseSqlOperator.LESS, searchBean.getName(), searchBean.getValue());
		} else if (relation.equals(">")) {
			criteria.AddCondition(BaseSqlOperator.GREATER, searchBean.getName(), searchBean.getValue());
		} else if (relation.equals(">=")) {
			criteria.AddCondition(BaseSqlOperator.GREATER_OR_EQUAL, searchBean.getName(), searchBean.getValue());
		} else if (relation.equals("!=")) {
			criteria.AddCondition(BaseSqlOperator.NOT_EQUAL, searchBean.getName(), searchBean.getValue());
		} else if (relation.equals("like")) {
			criteria.AddCondition(BaseSqlOperator.LIKE, searchBean.getName(), searchBean.getValue().toString());
		} else if (relation.equals("notLike")) {
			criteria.AddCondition(BaseSqlOperator.NOT_LIKE, searchBean.getName(), searchBean.getValue().toString());
		} else if (relation.equals("in")) {
			if (!(searchBean.getValue() instanceof List<?>)) {
				throw new BasicException("when searchBean is [in] type,value should be a List<?>");
			}
			criteria.AddCondition(BaseSqlOperator.IN, searchBean.getName(), searchBean.getValue());
		} else if (relation.equals("notIn")) {
			if (!(searchBean.getValue() instanceof List<?>)) {
				throw new BasicException("when searchBean is [in] type,value should be a List<?>");
			}
			criteria.AddCondition(BaseSqlOperator.NOT_IN, searchBean.getName(), searchBean.getValue());
		} else if (relation.equals("between")) {
			Object value = searchBean.getValue();
			if (!(value instanceof Pair<?, ?>)) {
				throw new BasicException("when searchBean is [in] type,value should be a Pair<?,?>");
			}
			String left = ((Pair<?, ?>) value).getLeft().toString();
			String right = ((Pair<?, ?>) value).getRight().toString();
			criteria.AddCondition(BaseSqlOperator.BETWEEN, searchBean.getName(), left, right);
		} else if (relation.equals("notBetween")) {
			Object value = searchBean.getValue();
			if (!(value instanceof Pair<?, ?>)) {
				throw new BasicException("when searchBean is [in] type,value should be a Pair<?,?>");
			}
			String left = ((Pair<?, ?>) value).getLeft().toString();
			String right = ((Pair<?, ?>) value).getRight().toString();
			criteria.AddCondition(BaseSqlOperator.NOT_BETWEEN, searchBean.getName(), left, right);
		} else if (relation.equals("isNull")) {
			criteria.AddCondition(BaseSqlOperator.IS_NULL, searchBean.getName());
		} else if (relation.equals("isNotNull")) {
			criteria.AddCondition(BaseSqlOperator.IS_NOT_NULL, searchBean.getName());
		}
	}

	public E getExample() {
		try {
			return exampleClazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public long countByExample(E example) throws BasicException {
		try {
			return getGeneratedSqlMapper().countByExample(example);
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof BasicException) {
				throw (BasicException) e;
			} else {
				throw new BasicException("发生系统异常！");
			}
		}
	}

	@Override
	public int deleteByExample(E example) throws BasicException {
		try {
			return getGeneratedSqlMapper().deleteByExample(example);
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof BasicException) {
				throw (BasicException) e;
			} else {
				throw new BasicException("发生系统异常！");
			}
		}
	}

	@Override
	public int deleteByPrimaryKey(ID id) throws BasicException {
		try {
			return getGeneratedSqlMapper().deleteByPrimaryKey(id);
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof BasicException) {
				throw (BasicException) e;
			} else {
				throw new BasicException("发生系统异常！");
			}
		}
	}

	@Override
	public int insert(T record) throws BasicException {
		try {
			getGeneratedSqlMapper().insert(record);
			return record.getId();
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof BasicException) {
				throw (BasicException) e;
			} else {
				throw new BasicException("发生系统异常！");
			}
		}
	}

	@Override
	public void batchInsert(List<T> records) throws BasicException {
		try {
			getGeneratedSqlMapper().batchInsert(records);
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof BasicException) {
				throw (BasicException) e;
			} else {
				throw new BasicException("发生系统异常！");
			}
		}
	}

	@Override
	public void batchUpdateByPrimaryKeyWithBLOBs(List<T> records) throws BasicException {
		try {
			getGeneratedSqlMapper().batchUpdateByPrimaryKeyWithBLOBs(records);
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof BasicException) {
				throw (BasicException) e;
			} else {
				throw new BasicException("发生系统异常！");
			}
		}
	}

	@Override
	public void batchUpdateByPrimaryKey(List<T> records) throws BasicException {
		try {
			getGeneratedSqlMapper().batchUpdateByPrimaryKey(records);
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof BasicException) {
				throw (BasicException) e;
			} else {
				throw new BasicException("发生系统异常！");
			}
		}
	}

	@Override
	public int insertSelective(T record) throws BasicException {
		try {
			getGeneratedSqlMapper().insertSelective(record);
			return record.getId();
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof BasicException) {
				throw (BasicException) e;
			} else {
				throw new BasicException("发生系统异常！");
			}
		}
	}

	@Override
	public List<DTO> selectByExample(E example) throws BasicException {
		try {
			return getGeneratedSqlMapper().selectByExample(example);
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof BasicException) {
				throw (BasicException) e;
			} else {
				throw new BasicException("发生系统异常！");
			}
		}
	}

	@Override
	public DTO selectByPrimaryKey(ID id) throws BasicException {
		try {
			return getGeneratedSqlMapper().selectByPrimaryKey(id);
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof BasicException) {
				throw (BasicException) e;
			} else {
				throw new BasicException("发生系统异常！");
			}
		}
	}

	@Override
	public int updateByExampleSelective(T record, E example) throws BasicException {
		try {
			return getGeneratedSqlMapper().updateByExampleSelective(record, example);
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof BasicException) {
				throw (BasicException) e;
			} else {
				throw new BasicException("发生系统异常！");
			}
		}
	}

	@Override
	public int updateByExample(T record, E example) throws BasicException {
		try {
			return getGeneratedSqlMapper().updateByExample(record, example);
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof BasicException) {
				throw (BasicException) e;
			} else {
				throw new BasicException("发生系统异常！");
			}
		}
	}

	@Override
	public int updateByPrimaryKeySelective(T record) throws BasicException {
		try {
			return getGeneratedSqlMapper().updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof BasicException) {
				throw (BasicException) e;
			} else {
				throw new BasicException("发生系统异常！");
			}
		}
	}

	@Override
	public int updateByPrimaryKey(T record) throws BasicException {
		try {
			return getGeneratedSqlMapper().updateByPrimaryKey(record);
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof BasicException) {
				throw (BasicException) e;
			} else {
				throw new BasicException("发生系统异常！");
			}
		}
	}

	@Override
	public List<DTO> selectByExampleWithBLOBs(E example) throws BasicException {
		try {
			return getGeneratedSqlMapper().selectByExampleWithBLOBs(example);
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof BasicException) {
				throw (BasicException) e;
			} else {
				throw new BasicException("发生系统异常！");
			}
		}
	}

	@Override
	public int updateByExampleWithBLOBs(T record, E example) throws BasicException {
		try {
			return getGeneratedSqlMapper().updateByExampleWithBLOBs(record, example);
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof BasicException) {
				throw (BasicException) e;
			} else {
				throw new BasicException("发生系统异常！");
			}
		}
	}

	@Override
	public int updateByPrimaryKeyWithBLOBs(T record) throws BasicException {
		try {
			return getGeneratedSqlMapper().updateByPrimaryKeyWithBLOBs(record);
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof BasicException) {
				throw (BasicException) e;
			} else {
				throw new BasicException("发生系统异常！");
			}
		}
	}

}
