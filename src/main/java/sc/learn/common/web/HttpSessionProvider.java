package sc.learn.common.web;

public interface HttpSessionProvider{
	
	void setAttibute(String name,Object value);

	<T> T getAttibute(String name, Class<T> type);
	
}
