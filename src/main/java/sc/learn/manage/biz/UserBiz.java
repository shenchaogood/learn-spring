package sc.learn.manage.biz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sc.learn.common.biz.BaseBiz;
import sc.learn.common.pojo.ResponseResult;
import sc.learn.common.util.StringUtil;
import sc.learn.common.util.security.Coder;
import sc.learn.manage.mapper.UserMapper;
import sc.learn.manage.po.User;
import sc.learn.manage.po.UserExample;
import sc.learn.manage.vo.UserVo;

@Service
public class UserBiz extends BaseBiz<User,UserMapper>{
	
	@Autowired
	public UserBiz(UserMapper mapper) throws ClassNotFoundException {
		super(mapper);
	}

	public User login(UserVo user){
		UserExample example=new UserExample();
		example.createCriteria().andEmailEqualTo(user.getEmail()).andPasswordEqualTo(Coder.encryptBase64MD5(user.getPassword()));
		return mapper.selectByExample(example).get(0);
	}

	public ResponseResult checkAddParam(User user){
		if(user==null){
			return ResponseResult.createFail("参数为空");
		}else if(StringUtil.isBlank(user.getEmail())){
			return ResponseResult.createFail("邮箱不能为空");
		}else if(StringUtil.isBlank(user.getName())){
			return ResponseResult.createFail("用户名不能为空");
		}else if(StringUtil.isBlank(user.getPassword())){
			return ResponseResult.createFail("密码不能为空");
		}else{
			return ResponseResult.createSuccess();
		}
	}
	
/*	
	public ResponseResult add(User user) {
		ResponseResult ret=checkAddParam(user);
		if(!ret.isSuccess()){
			return ret;
		}
		if(mapper.insertSelective(user)>0){
			return ResponseResult.createSuccess();
		}else{
			return ResponseResult.createFail(Constants.RECORD_EXISTS);
		}
	}
	public DataTableResult<User> list(DataTableParam param) {
		UserExample example=new UserExample();
		long recordsTotal=mapper.countByExample(example);
		PageHelper.offsetPage(param.getStart(), param.getLength());
		Criteria criteria=example.createCriteria();mapper.deleteByExample(example);
		
		StringBuilder orderByClause=new StringBuilder();
		param.getOrder().stream().reduce(orderByClause, (left,order)->
			left.append(param.getColumns().get(order.getColumn()).getData()).append(" ").append(order.getDir())
		,(left,right)->left.append(right));
		example.setOrderByClause(orderByClause.toString());
		
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
		
		List<User> data=mapper.selectByExample(example);
		PageInfo<User> pageInfo = new PageInfo<User>(data);
        long recordsFiltered = pageInfo.getTotal();
		return DataTableResult.createDataTableResult(param.getDraw(), recordsTotal, recordsFiltered, data,"");
	}
*/	
}
