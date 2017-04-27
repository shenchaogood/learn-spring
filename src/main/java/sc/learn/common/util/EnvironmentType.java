package sc.learn.common.util;

public enum EnvironmentType {
	
	PRODUCTION(true),TEST(false),DEV(false);
	
	
	private boolean production;

    EnvironmentType(boolean production) {
        this.production = production;
    }

    public boolean isProduction() {
        return this.production;
    }
}
