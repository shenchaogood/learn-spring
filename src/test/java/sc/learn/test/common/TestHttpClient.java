package sc.learn.test.common;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Test;
import org.springframework.util.StreamUtils;

public class TestHttpClient {
	
	@Test
	public void testHttpClient() throws InterruptedException{
		int count=10;
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		CountDownLatch countDownLatch = new CountDownLatch(count);
		cm.setMaxTotal(5);
		cm.setDefaultMaxPerRoute(7);
		cm.setMaxPerRoute(new HttpRoute(new HttpHost("localhost", 8080)), 1);
		ExecutorService executors = Executors.newFixedThreadPool(count);
		for(int i=0;i<count;i++){
			final int ii=i;
			executors.submit(()->{
				try {
					/**
					 * Socket timeout in SocketConfig represents the default value applied to newly created connections. 
					 * This value can be overwritten for individual requests by setting a non zero value of socket timeout in RequestConfig.
					 */
					RequestConfig config=RequestConfig.custom().setConnectionRequestTimeout(1).build();
					SocketConfig sockConfig=SocketConfig.custom().setSoTimeout(5000).build();
					HttpGet get=new HttpGet("http://localhost:8080/learn-spring/manage/user/e"+ii+"x");
					get.setConfig(config);
					CloseableHttpClient httpClient=HttpClients.custom().setDefaultRequestConfig(config).setDefaultSocketConfig(sockConfig).setConnectionManager(cm).build();
					CloseableHttpResponse response=httpClient.execute(get);
					InputStream is=response.getEntity().getContent();
					System.out.println(StreamUtils.copyToString(is, Charset.forName("UTF-8")));
				} catch (Exception e) {
					e.printStackTrace();
				}
				countDownLatch.countDown();
				
			});
		}
		countDownLatch.await();
		executors.shutdown();
	}


}
