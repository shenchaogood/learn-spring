package sc.learn.test.qlexpress;

import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.DynamicParamsUtil;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.Operator;
import com.ql.util.express.OperatorOfNumber;

public class QLExpressTest {
	
	@Test
	public void test1() throws Exception{
		ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("a",1);
        context.put("b",2);
        context.put("c",3);
        String express = "a+b*c";
        Object r = runner.execute(express, context, null, true, true);
        System.out.println(r);
	}
	
	@Test
	public void test2() throws Exception{
		ExpressRunner runner = new ExpressRunner();
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        String express = "n=10;sum=0;for(i=0;i<n;i++){sum=sum+i;}return sum;";
        Object r = runner.execute(express, context, null, true, true);
        System.out.println(r);
        express="a=1;b=2;max1=a>b?a:b;";
        r = runner.execute(express, context, null, true, true);
        System.out.println(r);
	}
	
	@Test
	public void test3() throws Exception{
		ExpressRunner runner = new ExpressRunner();
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        String express = "import sc.learn.test.qlexpress.OrderQuery;";//系统自动会import java.lang.*,import java.util.*;
        express+="query = new OrderQuery();";//创建class实例,会根据classLoader信息，自动补全类路径
        express+="query.setCreateDate(new Date());";//设置属性
        express+="query.buyer = \"张三\";";//调用属性,默认会转化为setBuyer("张三")
//        express+="result = bizOrderDAO.query(query);";//调用bean对象的方法
        express+="System.out.println(query.getBuyer());";////静态方法
		express+="function add(int a,int b){return a+b;};";//自定义方法与调用
		express+="function sub(int a,int b){return a-b;};";
		express+="a=10;";
		express+="return add(a,4) + sub(a,9);";
        Object r = runner.execute(express, context, null, true, true);
        System.out.println(r);
	}
	
	@Test
	public void test4() throws Exception{
		ExpressRunner runner = new ExpressRunner();
		runner.addOperatorWithAlias("如果", "if",null);
		runner.addOperatorWithAlias("则", "then",null);
		runner.addOperatorWithAlias("否则", "else",null);
		String express = "如果 1==2 则 2+2 否则 {20 + 20;}";
		Object r = runner.execute(express, null, null, true, true);
		System.out.println(r);
		//定义一个继承自com.ql.util.express.Operator的操作符
		class JoinOperator extends Operator{
			private static final long serialVersionUID = 1L;
			public Object executeInner(Object[] list) throws Exception {
				Object opdata1 = list[0];
				Object opdata2 = list[1];
				if(opdata1 instanceof java.util.List){
					((java.util.List)opdata1).add(opdata2);
					return opdata1;
				}else{
					java.util.List result = new java.util.ArrayList();
					result.add(opdata1);
					result.add(opdata2);
					return result;				
				}
			}
		}
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        runner.addOperator("join",new JoinOperator());
        r = runner.execute("1 join 2 join 3", context, null, false, false);
        System.out.println(r);//返回结果  [1, 2, 3]
        
        class GroupOperator extends Operator {
			private static final long serialVersionUID = 1L;
			public GroupOperator(String aName) {
        		this.name= aName;
        	}
        	public Object executeInner(Object[] list)throws Exception {
        		Object result = Integer.valueOf(0);
        		for (int i = 0; i < list.length; i++) {
        			result = OperatorOfNumber.add(result, list[i],false);//根据list[i]类型（string,number等）做加法
        		}
        		return result;
        	}
        }
        runner.addFunction("group", new GroupOperator("group"));
        r = runner.execute("group(1,2,3)", context, null, false, false);
        System.out.println(r);//返回结果  6
	}
	
	public static class BeanExample {
		public static String upper(String abc) {
			return abc.toUpperCase();
		}
		public boolean anyContains(String str, String searchStr) {
	        char[] s = str.toCharArray();
	        for (char c : s) {
	            if (searchStr.contains(c+"")) {
	                return true;
	            }
	        }
	        return false;
	    }
	}
	@Test
	public void test5() throws Exception{
		ExpressRunner runner = new ExpressRunner();
		runner.addFunctionOfClassMethod("取绝对值", Math.class.getName(), "abs",new String[] { "double" }, null);
		runner.addFunctionOfClassMethod("转换为大写", BeanExample.class.getName(),"upper", new String[] { "String" }, null);
		
		runner.addFunctionOfServiceMethod("打印", System.out, "println",new String[] { "String" }, null);
		runner.addFunctionOfServiceMethod("contains", new BeanExample(), "anyContains",new Class[] { String.class, String.class }, null);
		
		String exp = "取绝对值(-100);转换为大写(\"hello world\");打印(\"你好吗？\");contains(\"helloworld\",\"aeiou\");";
		Object r=runner.execute(exp, null, null, false, false);
		System.out.println(r);
	}
	
	@Test
	public void test6() throws Exception{
		ExpressRunner runner = new ExpressRunner();
		runner.addMacro("计算平均成绩", "(语文+数学+英语)/3.0");
		runner.addMacro("是否优秀", "计算平均成绩>90");
		IExpressContext<String, Object> context =new DefaultContext<String, Object>();
		context.put("语文", 88);
		context.put("数学", 99);
		context.put("英语", 95);
		Object result = runner.execute("是否优秀", context, null, false, false);
		System.out.println(result);
		//返回结果true
	}
	
	@Test
	public void test7() throws Exception{
		ExpressRunner runner = new ExpressRunner();
		runner.addMacro("计算平均成绩", "(语文+数学+英语)/3.0");
		runner.addMacro("是否优秀", "计算平均成绩>90");
		IExpressContext<String, Object> context =new DefaultContext<String, Object>();
		context.put("语文", 88);
		context.put("数学", 99);
		context.put("英语", 95);
		Object result = runner.execute("是否优秀", context, null, false, false);
		System.out.println(result);
		//返回结果true
	}
	
	@Test
	public void test8() throws Exception{
		String express = "int 平均分 = (语文+数学+英语+综合考试.科目2)/4.0;return 平均分";
		ExpressRunner runner = new ExpressRunner(true,true);
		String[] names = runner.getOutVarNames(express);
		for(String s:names){
			System.out.println("var : " + s);
		}
		//输出结果：
//		var : 数学
//		var : 综合考试
//		var : 英语
//		var : 语文
	}
	
	//等价于getTemplate(Object[] params)
    public Object getTemplate(Object... params) throws Exception{
        String result = "";
        for(Object obj:params){
            result = result+obj+",";
        }
        return result;
    }
	@Test
	public void test9() throws Exception{
		ExpressRunner runner = new ExpressRunner();
        IExpressContext<String,Object> expressContext = new DefaultContext<String,Object>();
        runner.addFunctionOfServiceMethod("getTemplate", this, "getTemplate", new Class[]{Object[].class}, null);
        //(1)默认的不定参数可以使用数组来代替
        Object r = runner.execute("getTemplate([11,'22',33L,true])", expressContext, null,false, false);
        System.out.println(r);
        //(2)像java一样,支持函数动态参数调用,需要打开以下全局开关,否则以下调用会失败
        DynamicParamsUtil.supportDynamicParams = true;
        r = runner.execute("getTemplate(11,'22',33L,true)", expressContext, null,false, false);
        System.out.println(r);
	}
	
	@Test
    public void test10() throws Exception {
        ExpressRunner runner = new ExpressRunner(false,false);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        String express = "abc = NewMap(1:1,2:2); return abc.get(1) + abc.get(2);";
        Object r = runner.execute(express, context, null, false, false);
        System.out.println(r);
        express = "abc = NewList(1,2,3); return abc.get(1)+abc.get(2)";
        r = runner.execute(express, context, null, false, false);
        System.out.println(r);
        express = "abc = [1,2,3]; return abc[1]+abc[2];";
        r = runner.execute(express, context, null, false, false);
        System.out.println(r);
    }

	
}
