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
	
	private static EnvironmentUtil single=new EnvironmentUtil();
	
	
	public static EnvironmentType getLocalEnviromentType(){
		return single.environmentType;
	}
	
	public static boolean isProductionEnvironment(){
		return single.environmentType.isProduction();
	}
	
}
