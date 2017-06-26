package sc.learn.common.biz;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import sc.learn.common.pojo.DataTableParam;
import sc.learn.common.pojo.DataTableResult;
import sc.learn.common.pojo.ResponseResult;
import sc.learn.common.util.ExceptionUtil;
import sc.learn.common.util.StringUtil;
import sc.learn.manage.util.Constants;

@Transactional(rollbackFor=Exception.class)
public abstract class BaseBiz<PO,MAPPER> {
	
	protected final Logger logger=LoggerFactory.getLogger(getClass());
	
	protected MAPPER mapper;
	private Class<?> mapperClass;
	private Class<?> mapperExampleClass;
	
	public BaseBiz(MAPPER mapper) throws ClassNotFoundException {
		this.mapper = mapper;
		mapperClass=mapper.getClass();
		String mapperClassName=mapperClass.getInterfaces()[0].getName();
		String mapperExampleName=StringUtil.removeEnd(mapperClassName.replace(".mapper.", ".po."), "Mapper")+"Example";
		mapperExampleClass=Class.forName(mapperExampleName);
	}
	
	protected ResponseResult checkAddParam(PO po){
		return ResponseResult.createSuccess();
	}
	
	protected ResponseResult checkUpdateParam(PO po){
		return ResponseResult.createSuccess();
	}
	
	protected ResponseResult checkDeleteParam(Serializable... ids){
		return ResponseResult.createSuccess();
	}
	
	public ResponseResult add(PO po) {
		ResponseResult ret=checkAddParam(po);
		if(!ret.isSuccess()){
			return ret;
		}
		
		int effectRow;
		try {
			effectRow = (Integer)mapperClass.getMethod("insertSelective", po.getClass()).invoke(mapper, po);
			return effectRow>0?ResponseResult.createSuccess():ResponseResult.createFail(Constants.RECORD_EXISTS);
		} catch (Exception e) {
			ExceptionUtil.rethrow(e, RuntimeException.class);
			return ResponseResult.createFail(e.getMessage());
		}
	}
	
	public ResponseResult update(PO po) {
		ResponseResult ret=checkUpdateParam(po);
		if(!ret.isSuccess()){
			return ret;
		}
		try {
			int effectRow = (Integer)mapperClass.getMethod("updateByExampleSelective", po.getClass()).invoke(mapper, po);
			return effectRow>0?ResponseResult.createSuccess():ResponseResult.createFail(Constants.RECORD_EXISTS);
		} catch (Exception e) {
			ExceptionUtil.rethrow(e, RuntimeException.class);
			return ResponseResult.createFail(e.getMessage());
		}
	}
	
	public ResponseResult delete(Serializable... ids){
		ResponseResult ret=checkDeleteParam(ids);
		if(!ret.isSuccess()){
			return ret;
		}
		try{
			Object mapperExample=mapperExampleClass.newInstance();
			Object criteria=mapperExampleClass.getMethod("createCriteria",Void.class).invoke(mapperExample);
			criteria.getClass().getMethod("andIdIn",List.class).invoke(criteria,Arrays.asList(ids));
			int effectRow = (Integer)mapperClass.getMethod("deleteByExample", mapperExampleClass).invoke(mapper, mapperExample);
			return effectRow>0?ResponseResult.createSuccess(Constants.SUCCESS_DESC+":删除记录"+effectRow+"条"):ResponseResult.createFail(Constants.FAIL_DESC+":删除记录0条");
		}catch(Exception e){
			ExceptionUtil.rethrow(e, RuntimeException.class);
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
			mapperExampleClass.getMethod("setOrderByClause", String.class).invoke(mapperExample, StringUtil.defaultIfBlank(orderByClause, null));
			String searchValue=param.getSearch().getValue();
			param.getColumns().forEach((column)->{
				if(column.isSearchable()){
					if(StringUtil.isNotBlank(column.getSearch().getValue())){
						try {
							Method method = criteria.getClass().getMethod("and"+StringUtil.capitalize(column.getName())+"EqualTo");
							method.invoke(criteria,ConvertUtils.convert(column.getData(), method.getParameterTypes()[0]));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if(StringUtil.isNotBlank(searchValue)){
						try {
							Method method = criteria.getClass().getMethod("and"+StringUtil.capitalize(column.getName())+"Like");
							method.invoke(criteria,ConvertUtils.convert(searchValue, method.getParameterTypes()[0]));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
			
			@SuppressWarnings("unchecked")
			List<PO> data=(List<PO>)mapperClass.getMethod("selectByExample", mapperExampleClass).invoke(mapper, mapperExample);
			PageInfo<PO> pageInfo = new PageInfo<PO>(data);
	        long recordsFiltered = pageInfo.getTotal();
			return DataTableResult.createDataTableResult(param.getDraw(), recordsTotal, recordsFiltered, data,"");
		}catch (Exception e) {
			ExceptionUtil.rethrow(e, RuntimeException.class);
			return DataTableResult.createDataTableResult(param.getDraw(), 0, 0, new ArrayList<PO>(0),e.getLocalizedMessage());
		}
	}

}
