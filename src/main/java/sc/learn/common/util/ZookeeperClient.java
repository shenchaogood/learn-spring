package sc.learn.common.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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


public class ZookeeperClient implements Watcher,StringCallback,VoidCallback,StatCallback{
	private static final Logger LOG=LoggerFactory.getLogger(ZookeeperClient.class);
	
	public static final String EPHEMERAL="EPHEMERAL";
	public static final String EPHEMERAL_SEQUENTIAL="EPHEMERAL_SEQUENTIAL";
	public static final String PERSISTENT="PERSISTENT";
	public static final String PERSISTENT_SEQUENTIAL="PERSISTENT_SEQUENTIAL";

	private ZooKeeper client;
	
	public ZookeeperClient() throws IOException{
		client=new ZooKeeper(ZkConfig.ZK_SERVER, ZkConfig.ZK_SESSION_TIMEOUT, this);
	}

	
	public void createPath(String path,byte[] data,String createMode){
		Map<String,Object> ctx=new HashMap<>();
		ctx.put("data", data);
		ctx.put("createMode", createMode);
		client.create(path, data, Ids.OPEN_ACL_UNSAFE, CreateMode.valueOf(createMode), this, ctx);
	}
	
	public void deletePath(String path){
		client.delete(path,-1,this,null);
	}
	
	public void setDate(String path,byte[] data){
		client.setData(path, data, -1, this, data);
	}
	
	@Override
	public void process(WatchedEvent event) {
		LOG.info(event.toString());
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
	

	public static void main(String[] args) throws Exception{
		ZookeeperClient client=new ZookeeperClient();
		client.deletePath("/SC");
		client.createPath("/SC2", "woshi".getBytes(), ZookeeperClient.EPHEMERAL);
		Thread.sleep(10000000);
	}
	
	
}
