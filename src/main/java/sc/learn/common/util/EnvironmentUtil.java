package sc.learn.common.util;

public class EnvironmentUtil {
	
	/**
	 * 本机环境
	 * @return 本机环境
	 */
	public static EnvironmentType getLocalEnviromentType(){
		return EnvironmentType.valueOf(ZkConfig.ZK_PROFILE);
	}
	
	/**
	 * 是生产环境吗
	 * @return true 是  false 否
	 */
	public static boolean isProductionEnvironment(){
		return getLocalEnviromentType().isProduction();
	}
	
	public static int getEnvironmentNumber(){
		return Integer.parseInt(ZkConfig.ZK_ENVIRONMENT.split("__")[1]);
	}
	
	public static String getEnvironmentName(){
		return ZkConfig.ZK_ENVIRONMENT.split("__")[0];
	}
	
}
