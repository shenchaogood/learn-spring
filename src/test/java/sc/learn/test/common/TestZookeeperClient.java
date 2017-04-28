package sc.learn.test.common;

import org.junit.Test;

import sc.learn.common.util.ZookeeperClient;

public class TestZookeeperClient {
	
	@Test
	public void createPath() throws Exception{
		ZookeeperClient client=new ZookeeperClient();
		client.createPath("/shenchaoEPHEMERAL", "woshi".getBytes(), ZookeeperClient.EPHEMERAL);
		Thread.sleep(500);
		client.createPath("/shenchaoPERSISTENT", "woshi".getBytes(), ZookeeperClient.PERSISTENT);
		Thread.sleep(50000);
	}
	
	
	@Test
	public void deletePath() throws Exception{
		ZookeeperClient client=new ZookeeperClient();
		client.deletePath("/shenchaoEPHEMERAL");
		client.deletePath("/shenchaoPERSISTENT");
		Thread.sleep(500);
	}

}
