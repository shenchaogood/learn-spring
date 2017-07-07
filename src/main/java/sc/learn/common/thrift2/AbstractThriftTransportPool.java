package sc.learn.common.thrift2;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.thrift.transport.TTransport;

public abstract class AbstractThriftTransportPool<T extends TTransport> extends GenericObjectPool<T>{

	private AbstractThriftConnectionFactory<T> factory;
	
	public AbstractThriftTransportPool(AbstractThriftConnectionFactory<T> factory, int maxActive, byte whenExhaustedAction, long maxWait,
            int maxIdle, int minIdle, boolean testOnBorrow, boolean testOnReturn, long timeBetweenEvictionRunsMillis,
            int numTestsPerEvictionRun, long minEvictableIdleTimeMillis, boolean testWhileIdle,
            long softMinEvictableIdleTimeMillis, boolean lifo) {
		super(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, minIdle, testOnBorrow, testOnReturn, 
				timeBetweenEvictionRunsMillis, numTestsPerEvictionRun, minEvictableIdleTimeMillis, testWhileIdle, 
				softMinEvictableIdleTimeMillis,lifo);
		this.factory=factory;
	}
	
	public boolean isSynchronized(){
		return factory.isSynchronized();
	}
	

}
