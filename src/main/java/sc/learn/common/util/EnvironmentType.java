package sc.learn.common.util;

public enum EnvironmentType {
	
	/**
	 * 生产
	 */
	PRODUCTION(true),
	/**
	 * 测试
	 */
	TEST(false),
	/**
	 * 开发
	 */
	DEV(false);
	
	
	private boolean production;

    EnvironmentType(boolean production) {
        this.production = production;
    }

    public boolean isProduction() {
        return this.production;
    }
}
