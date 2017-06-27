package sc.learn.common.util;

import org.apache.commons.lang3.StringUtils;

public class EnvironmentUtil {
	private EnvironmentType environmentType;
	
	private EnvironmentUtil(){
		String profile=ZkConfig.PROFILE;
		if(StringUtils.isBlank(profile)){
			profile=System.getProperty(ZkConfig.PROFILE_KEY);
		}
		if(StringUtils.isBlank(profile)){
			profile=System.getenv(ZkConfig.PROFILE_KEY);
		}
		if(StringUtils.isBlank(profile)){
			profile=EnvironmentType.PRODUCTION.toString();
		}
		environmentType=EnvironmentType.valueOf(profile.toUpperCase());
	}
	
	private static final EnvironmentUtil SINGLE=new EnvironmentUtil();
	
	/**
	 * 本机环境
	 * @return 本机环境
	 */
	public static EnvironmentType getLocalEnviromentType(){
		return SINGLE.environmentType;
	}
	
	/**
	 * 是生产环境吗
	 * @return true 是  false 否
	 */
	public static boolean isProductionEnvironment(){
		return SINGLE.environmentType.isProduction();
	}
	
}
