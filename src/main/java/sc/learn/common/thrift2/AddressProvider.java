package sc.learn.common.thrift2;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.learn.common.util.StringUtil;

public class AddressProvider {
    private static Logger LOGGER = LoggerFactory.getLogger(AddressProvider.class);

    /**
     * 最新的服务器IP列表，由zookeeper来维护更新
     */
    private List<InetSocketAddress> serverAddresses = new CopyOnWriteArrayList<InetSocketAddress>();

    /**
     * 没有配置zookeeper时使用原来配置文件中的IP列表
     */
    private List<InetSocketAddress> backupAddresses = new LinkedList<InetSocketAddress>();

    /**
     * 轮循队列，获取IP时使用
     */
    private Queue<InetSocketAddress> loop = new LinkedList<InetSocketAddress>();

    private Lock loopLock = new ReentrantLock();

    /**
     * zookeeper 监控
     */
    private PathChildrenCache cachedPath;

    public AddressProvider() {
    }

    public AddressProvider(String backupAddress, CuratorFramework zkClient, String zookeeperPath) {
        // 默认使用配置文件中的IP列表
        this.backupAddresses.addAll(this.transfer(backupAddress));
        this.serverAddresses.addAll(this.backupAddresses);
        Collections.shuffle(this.backupAddresses);
        Collections.shuffle(this.serverAddresses);

        // 配置zookeeper时，启动客户端
        if (!StringUtil.isBlank(zookeeperPath) && zkClient != null) {
            buildPathChildrenCache(zkClient, zookeeperPath, true);
            try {
				cachedPath.start(StartMode.POST_INITIALIZED_EVENT);
			} catch (Exception e) {
				throw new ThriftException(e);
			}
        }
    }

    public InetSocketAddress selectOne() {
        loopLock.lock();
        try {
            if (this.loop.isEmpty()) {
                this.loop.addAll(this.serverAddresses);
            }
            return this.loop.poll();
        } finally {
            loopLock.unlock();
        }
    }

    public Iterator<InetSocketAddress> addressIterator() {
        return this.serverAddresses.iterator();
    }

    /**
     * 初始化 cachedPath，并添加监听器，当zookeeper上任意节点数据变动时，更新本地serverAddresses
     * @param client
     * @param path
     * @param cacheData
     * @throws Exception
     */
    private void buildPathChildrenCache(final CuratorFramework client, String path, Boolean cacheData) {
        final String logPrefix = "buildPathChildrenCache_" + path + "_";
        cachedPath = new PathChildrenCache(client, path, cacheData);
        cachedPath.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                PathChildrenCacheEvent.Type eventType = event.getType();
                switch (eventType) {
                case CONNECTION_RECONNECTED:
                    LOGGER.info(logPrefix + "Connection is reconection.");
                    break;
                case CONNECTION_SUSPENDED:
                    LOGGER.info(logPrefix + "Connection is suspended.");
                    break;
                case CONNECTION_LOST:
                    LOGGER.warn(logPrefix + "Connection error,waiting...");
                    return;
                case INITIALIZED:
                    LOGGER.warn(logPrefix + "Connection init ...");
                default:
                }
                // 任何节点的时机数据变动,都会rebuild,此处为一个"简单的"做法.
                cachedPath.rebuild();
                rebuild();
            }

            private void rebuild() throws Exception {
                List<ChildData> children = cachedPath.getCurrentData();
                serverAddresses.clear();
                if (CollectionUtils.isEmpty(children)) {
                    // 有可能所有的thrift server都与zookeeper断开了链接
                    // 但是 thrift client与thrift server之间的网络是良好的
                    // 因此此处是否需要清空serverAddresses,是需要多方面考虑的.
                    // 这里我们认为zookeeper的服务是可靠的，数据为空也是正确的
                    // serverAddresses.clear();
                    LOGGER.error(logPrefix + "server ips in zookeeper is empty");
                    return;
                }else{
                	List<InetSocketAddress> lastServerAddress = new LinkedList<InetSocketAddress>();
                    for (ChildData data : children) {
                        String address = new String(data.getData(), "utf-8");
                        lastServerAddress.add(transferSingle(address));
                    }
                    serverAddresses.addAll(lastServerAddress);
                    Collections.shuffle(serverAddresses);
                }
            }
        });
    }

    /**
     * 将String地址转换为InetSocketAddress
     * @param serverAddress
     *            10.183.222.59:1070
     * @return
     */
    private InetSocketAddress transferSingle(String serverAddress) {
        if (StringUtil.isBlank(serverAddress)) {
            return null;
        }
        String[] address = serverAddress.split(":");
        return new InetSocketAddress(address[0], Integer.valueOf(address[1]));
    }

    /**
     * 将多个String地址转为InetSocketAddress集合
     * @param serverAddresses
     *            ip:port;ip:port;ip:port;ip:port
     * @return
     */
    private List<InetSocketAddress> transfer(String serverAddresses) {
        if (StringUtil.isBlank(serverAddresses)) {
            return null;
        }
        List<InetSocketAddress> tempServerAdress = new LinkedList<InetSocketAddress>();
        String[] hostnames = serverAddresses.split(",;");
        for (String hostname : hostnames) {
            tempServerAdress.add(this.transferSingle(hostname));
        }
        return tempServerAdress;
    }

}