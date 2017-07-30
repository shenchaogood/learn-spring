package sc.learn.common.biz;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import sc.learn.common.pojo.DataTableParam;
import sc.learn.common.pojo.DataTableResult;
import sc.learn.common.pojo.ResponseResult;
import sc.learn.manage.util.Constants;


@Transactional(rollbackFor=Exception.class)
public abstract class BaseBiz<PO,MAPPER> {
	
	protected final Logger LOGGER=LoggerFactory.getLogger(getClass());
	
	protected MAPPER mapper;
	private Class<?> mapperClass;
	private Class<?> mapperExampleClass;
	
	public BaseBiz(MAPPER mapper) throws ClassNotFoundException {
		this.mapper = mapper;
		mapperClass=mapper.getClass();
		String mapperClassName=mapperClass.getInterfaces()[0].getName();
		String mapperExampleName=StringUtils.removeEnd(mapperClassName.replace(".mapper.", ".po."), "Mapper")+"Example";
		mapperExampleClass=Class.forName(mapperExampleName);
	}
	
	protected ResponseResult<PO> checkAddParam(PO po){
		return ResponseResult.createSuccess();
	}
	
	protected ResponseResult<PO> checkUpdateParam(PO po){
		return ResponseResult.createSuccess();
	}
	
	protected ResponseResult<PO> checkDeleteParam(Serializable... ids){
		return ResponseResult.createSuccess();
	}
	
	@SuppressWarnings("unchecked")
	public ResponseResult<PO> findById(Long id){
		try {
			return ResponseResult.<PO>createSuccess().setData((PO)mapperClass.getMethod("selectByPrimaryKey",Long.class).invoke(mapper, id));
		} catch (Exception e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
			return ResponseResult.createFail(e.getLocalizedMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public ResponseResult<PO> findById(Long id,boolean lock){
		try {
			if(lock){
				Object mapperExample=mapperExampleClass.newInstance();
				Object criteria=mapperExampleClass.getMethod("createCriteria").invoke(mapperExample);
				criteria.getClass().getMethod("andIdIn",List.class).invoke(criteria,Arrays.asList(id));
				mapperExampleClass.getMethod("setOrderByClause", String.class).invoke(mapperExample, "f_id asc for update");
				List<PO> list=(List<PO>)mapperClass.getMethod("selectByExample", mapperExampleClass).invoke(mapper, mapperExample);
				return ResponseResult.<PO>createSuccess().setData(list.size()>0?list.get(0):null);
			}else{
				return ResponseResult.<PO>createSuccess().setData((PO)mapperClass.getMethod("selectByPrimaryKey",Long.class).invoke(mapper, id));
			}
		} catch (Exception e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
			return ResponseResult.createFail(e.getLocalizedMessage());
		}
	}
	
	public ResponseResult<PO> add(PO po) {
		ResponseResult<PO> ret=checkAddParam(po);
		if(!ret.isSuccess()){
			return ret;
		}
		
		int effectRow;
		try {
			effectRow = (Integer)mapperClass.getMethod("insertSelective", po.getClass()).invoke(mapper, po);
			return effectRow>0?ResponseResult.<PO>createSuccess().setData(po):ResponseResult.createFail(Constants.RECORD_EXISTS);
		} catch (Exception e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
			return ResponseResult.createFail(e.getMessage());
		}
	}
	
	public ResponseResult<PO> update(PO po) {
		ResponseResult<PO> ret=checkUpdateParam(po);
		if(!ret.isSuccess()){
			return ret;
		}
		try {
			int effectRow = (Integer)mapperClass.getMethod("updateByPrimaryKeySelective", po.getClass()).invoke(mapper, po);
			return effectRow>0?ResponseResult.<PO>createSuccess().setData(po):ResponseResult.createFail(Constants.RECORD_EXISTS);
		} catch (Exception e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
			return ResponseResult.createFail(e.getMessage());
		}
	}
	
	public ResponseResult<PO> delete(Serializable... ids){
		ResponseResult<PO> ret=checkDeleteParam(ids);
		if(!ret.isSuccess()){
			return ret;
		}
		try{
			Object mapperExample=mapperExampleClass.newInstance();
			Object criteria=mapperExampleClass.getMethod("createCriteria").invoke(mapperExample);
			criteria.getClass().getMethod("andIdIn",List.class).invoke(criteria,Arrays.asList(ids));
			int effectRow = (Integer)mapperClass.getMethod("deleteByExample", mapperExampleClass).invoke(mapper, mapperExample);
			return effectRow>0?ResponseResult.createSuccess(Constants.SUCCESS_DESC+":删除记录"+effectRow+"条"):ResponseResult.createFail(Constants.FAIL_DESC+":删除记录0条");
		}catch(Exception e){
			LOGGER.error(ExceptionUtils.getStackTrace(e));
			return ResponseResult.createFail(e.getLocalizedMessage());
		}
	}

	@Transactional(readOnly=true)
	public DataTableResult<PO> list(DataTableParam param){
		try{
			Object mapperExample=mapperExampleClass.newInstance();
			long recordsTotal=(Long)mapperClass.getMethod("countByExample", mapperExample.getClass()).invoke(mapper, mapperExample);
			PageHelper.offsetPage(param.getStart(), param.getLength());
			Object criteria=mapperExampleClass.getMethod("createCriteria").invoke(mapperExample);
			StringBuilder orderByClause=param.getOrder().stream().reduce(new StringBuilder(), (left,order)->
				left.append(param.getColumns().get(order.getColumn()).getData()).append(" ").append(order.getDir())
				,(left,right)->left.append(right));
			mapperExampleClass.getMethod("setOrderByClause", String.class).invoke(mapperExample, StringUtils.defaultIfBlank(orderByClause.toString(), null));
			Object globalSearchValue=param.getSearch().getValue();
			param.getColumns().forEach((column)->{
				if(column.isSearchable()){
					Object searchValue=column.getSearch().getValue();
					if(Objects.nonNull(searchValue)){
						try {
							Method method = criteria.getClass().getMethod("and"+StringUtils.capitalize(column.getData())+"EqualTo",searchValue.getClass());
							method.invoke(criteria,searchValue);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if(Objects.nonNull(globalSearchValue)){
						try {
							if(column.isSearchable()){
								Method method = criteria.getClass().getMethod("and"+StringUtils.capitalize(column.getName())+"Like",String.class);
								method.invoke(criteria,globalSearchValue.toString());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
			
			@SuppressWarnings("unchecked")
			Page<PO> data=(Page<PO>)mapperClass.getMethod("selectByExample", mapperExampleClass).invoke(mapper, mapperExample);
//			PageInfo<PO> pageInfo = new PageInfo<PO>(data);
//	        long recordsFiltered = pageInfo.getTotal();
			return DataTableResult.createDataTableResult(param.getDraw(), recordsTotal, data.getTotal(), data,"");
		}catch (Exception e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
			return DataTableResult.createDataTableResult(param.getDraw(), 0, 0, new ArrayList<PO>(0),e.getLocalizedMessage());
		}
	}

}
