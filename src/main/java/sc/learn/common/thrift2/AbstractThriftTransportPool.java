package sc.learn.common.thrift2;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.thrift.transport.TTransport;

import sc.learn.common.util.ThriftUtil;

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
		ThriftUtil.registTransportPool(factory.getIfaceClass(), this);
	}
	
	public boolean isSynchronized(){
		return factory.isSynchronized();
	}
	
}
