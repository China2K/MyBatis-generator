package com.iss.core.generate;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;

public interface IBaseService<T extends BaseEntity, DTO extends T, E extends AbstractExample<T>, ID extends Serializable> {

	Page<DTO> pageFind(String orderByClause, int offset, int limit, SearchBean... AndRelation_SearchBeans)
			throws BasicException;

	Page<DTO> pageFind(int offset, int limit, SearchBean... AndRelation_SearchBeans) throws BasicException;

	@Deprecated
	SearchBean[] convert2SearchBean(String param);

	E convertSearchBean2OrExample(List<SearchBean[]> searchBeansAry);

	@Deprecated
	E convert2Example(String param);

	List<SearchBean[]> convert2OrSearchBean(String param);

	@Deprecated
	E convertSearchBean2Example(SearchBean[] searchBeans);

	E convert2OrExample(String param);

	List<DTO> find(E example) throws BasicException;

	DTO findUnique(E example) throws BasicException;

	Page<DTO> pageFind(E example) throws BasicException;

	long count(SearchBean... AndRelation_SearchBeans) throws BasicException;

	E getExample();

	long countByExample(E example) throws BasicException;

	int deleteByExample(E example) throws BasicException;

	int deleteByPrimaryKey(ID id) throws BasicException;

	int insert(T record) throws BasicException;

	void batchInsert(List<T> records) throws BasicException;

	int insertSelective(T record) throws BasicException;

	List<DTO> selectByExample(E example) throws BasicException;

	DTO selectByPrimaryKey(ID id) throws BasicException;

	int updateByExampleSelective(T record, E example) throws BasicException;

	int updateByExample(T record, E example) throws BasicException;

	int updateByPrimaryKeySelective(T record) throws BasicException;

	int updateByPrimaryKey(T record) throws BasicException;

	List<DTO> selectByExampleWithBLOBs(E example) throws BasicException;

	int updateByExampleWithBLOBs(T record, E example) throws BasicException;

	int updateByPrimaryKeyWithBLOBs(T record) throws BasicException;

	void batchUpdateByPrimaryKeyWithBLOBs(List<T> records) throws BasicException;

	void batchUpdateByPrimaryKey(List<T> records) throws BasicException;

	void transCriteria(SearchBean searchBean, AbstractCriteria<T> criteria) throws BasicException;

	Page<DTO> pageFind(int offset, int limit, E example) throws BasicException;

	Page<DTO> pageFind(String orderByClause, int offset, int limit, E example) throws BasicException;

}