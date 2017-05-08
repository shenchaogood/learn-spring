package sc.learn.common.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.AsyncCallback.VoidCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ZookeeperClient {
	private static final Logger LOG=LoggerFactory.getLogger(ZookeeperClient.class);
	
	public static final String EPHEMERAL="EPHEMERAL";
	public static final String EPHEMERAL_SEQUENTIAL="EPHEMERAL_SEQUENTIAL";
	public static final String PERSISTENT="PERSISTENT";
	public static final String PERSISTENT_SEQUENTIAL="PERSISTENT_SEQUENTIAL";
	
	private final ZkCallback callback=new ZkCallback();
	
	private final class ZkCallback implements Watcher,StringCallback,VoidCallback,StatCallback,ChildrenCallback{
		
		@Override
		public void process(WatchedEvent event) {
			switch(event.getState()){
			case Disconnected:
			case Expired:
				try {
					if(client!=null){
						client.close();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				try {
					client=new ZooKeeper(ZkConfig.ZK_SERVER, ZkConfig.ZK_SESSION_TIMEOUT, callback);
				} catch (IOException e) {
					LOG.error(ExceptionUtils.getStackTrace(e));
				}
			case AuthFailed:
				LOG.error(event.toString());
				break;
			default:
				LOG.info(event.toString());
			}
		}


		@Override
		public void processResult(int rc, String path, Object ctx, String name) {
			switch(Code.get(rc)){
			case CONNECTIONLOSS:
				@SuppressWarnings("unchecked") Map<String,Object> context=(Map<String, Object>)ctx;
				createPath(path,(byte[])context.get("data"),(String)context.get("createMode"));
				break;
			case OK:
				break;
			default:
				LOG.info(KeeperException.create(Code.get(rc)).getMessage());
				break;
			}
		}


		@Override
		public void processResult(int rc, String path, Object ctx) {
			switch(Code.get(rc)){
			case CONNECTIONLOSS:
				deletePath(path);
				break;
			case OK:
				break;
			default:
				LOG.info(KeeperException.create(Code.get(rc)).getMessage());
				break;
			}
		}


		@Override
		public void processResult(int rc, String path, Object ctx, Stat stat) {
			switch(Code.get(rc)){
			case CONNECTIONLOSS:
				setDate(path,(byte[])ctx);
				break;
			case OK:
				break;
			default:
				LOG.info(KeeperException.create(Code.get(rc)).getMessage());
				break;
			}
		}


		@Override
		public void processResult(int rc, String path, Object ctx, List<String> children) {
			switch(Code.get(rc)){
			case CONNECTIONLOSS:
				LOG.error(KeeperException.create(Code.get(rc)).getMessage());
				break;
			case OK:
				break;
			default:
				LOG.info(KeeperException.create(Code.get(rc)).getMessage());
				break;
			}
			
		}
	}

	private ZooKeeper client;
	
	public void getChildren(String path,Watcher watcher,ChildrenCallback cb){
		client.getChildren(path, watcher, cb, null);
	}
	
	public ZookeeperClient() throws IOException{
		client=new ZooKeeper(ZkConfig.ZK_SERVER, ZkConfig.ZK_SESSION_TIMEOUT, callback);
	}
	
	
	
	public void createPath(String path,byte[] data,String createMode){
		Map<String,Object> ctx=new HashMap<>();
		ctx.put("data", data);
		ctx.put("createMode", createMode);
		client.create(path, data, Ids.OPEN_ACL_UNSAFE, CreateMode.valueOf(createMode), callback, ctx);
	}
	
	public void deletePath(String path){
		client.delete(path,-1,callback,null);
	}
	
	public void setDate(String path,byte[] data){
		client.setData(path, data, -1, callback, data);
	}
}
