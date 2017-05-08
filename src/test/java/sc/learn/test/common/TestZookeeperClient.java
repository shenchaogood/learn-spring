package sc.learn.test.common;

import java.util.List;

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
	
	@Test
	public void getChildren() throws Exception{
		ZookeeperClient client=new ZookeeperClient();
		while(true){
			client.getChildren("/",event->{
				
			},(int rc, String path, Object ctx, List<String> children)->{
				System.out.println("children is :"+children);
			});
			Thread.sleep(5000);
		}
		
	}

}
