package com.iss.core.generate;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface SqlMapper<T extends BaseEntity, DTO extends BaseEntity, E extends AbstractExample<T>, ID extends Serializable> {
	long countByExample(E example);

	int deleteByExample(E example);

	int deleteByPrimaryKey(ID id);

	int insert(T record);

	void batchInsert(List<T> record);

	int insertSelective(T record);

	List<DTO> selectByExample(E example);

	DTO selectByPrimaryKey(ID id);

	List<DTO> selectByExampleWithBLOBs(E example);

	int updateByExampleSelective(@Param("record") T record, @Param("example") E example);

	int updateByExample(@Param("record") T record, @Param("example") E example);

	int updateByPrimaryKeySelective(T record);

	int updateByPrimaryKey(T record);

	int updateByExampleWithBLOBs(@Param("record") T record, @Param("example") E example);

	int updateByPrimaryKeyWithBLOBs(T record);

	void batchUpdateByPrimaryKeyWithBLOBs(@Param("records") List<T> records);

	void batchUpdateByPrimaryKey(@Param("records") List<T> records);

}
