package sc.learn.common.web;


import redis.clients.jedis.JedisCluster;
import sc.learn.common.util.JsonUtil;

public class ClusterHttpSessionProvider implements HttpSessionProvider {

	private static final int DEFAULT_SESSTION_TIMEOUT=20*60*1000;
	
	private JedisCluster jedisCluster;
	
	public ClusterHttpSessionProvider(JedisCluster jedisCluster){
		this.jedisCluster=jedisCluster;
	}
	
	@Override
	public void setAttibute(String name, Object value) {
		jedisCluster.set(name, JsonUtil.toJSONString(value), "NX","PX",DEFAULT_SESSTION_TIMEOUT);
		jedisCluster.set(name, JsonUtil.toJSONString(value), "XX","PX",DEFAULT_SESSTION_TIMEOUT);
	}
	
	@Override
	public <T> T getAttibute(String name,Class<T> type) {
		return JsonUtil.parseJSONString(jedisCluster.get("name"), type);
	}
	
}
