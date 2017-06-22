package sc.learn.common.mapper;

import java.util.List;

import sc.learn.common.pojo.DataTableParam;

public interface BaseMapper<T> {
	int insert(T t);
	
	int update(T t);
	
	long selectCount(DataTableParam param);

	List<T> select(DataTableParam param);

}
