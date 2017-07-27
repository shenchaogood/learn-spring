package sc.learn.test.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;

import sc.learn.common.util.net.HttpClientUtil;
import sc.learn.common.util.net.HttpClientUtil.HttpResult;

public class TestHttpClient {
	
	@Test
	public void testSendSms() throws IOException{
		String url="https://sandboxapp.cloopen.com:8883/2013-12-26/Accounts/accountSid/SMS/TemplateSMS?sig=C1F20E7A9";
		JSONObject json=new JSONObject();
		json.put("to", "18600138712");
		json.put("appId", "ff8080813fc70a7b013fc72312324213");
		json.put("templateId", "1");
		json.put("datas", new JSONArray(Arrays.asList("123456","30")));
		
		Map<String,String> headers=Maps.newHashMap();
		headers.put("Accept", "application/json");
		headers.put("Content-Type", "application/xml;charset=utf-8");
		headers.put("Authorization", "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		
		HttpResult result=HttpClientUtil.post(url, json.toJSONString(), headers, null);
		System.out.println(result);
	}
	
	@Test
	public void testCreateSMSTemplate() throws IOException{
		String url="https://sandboxapp.cloopen.com:8883/2013-12-26/Accounts/accountSid/SMS/CreateSMSTemplate?sig=C1F20E7A9";
		JSONObject json=new JSONObject();
		json.put("appId", "aaf98f8946471bb00146806064f02206");
		json.put("productType", "1");
		json.put("addr", "http://yuntong.com/app");
		json.put("title", "title");
		json.put("signature", "signature");
		json.put("templateContent", "{},{}");
		json.put("auditNotifyUrl", "http://www.baidu.com");
		
		Map<String,String> headers=Maps.newHashMap();
		headers.put("Accept", "application/json");
		headers.put("Content-Type", "application/xml;charset=utf-8");
		headers.put("Authorization", "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		
		HttpResult result=HttpClientUtil.post(url, json.toJSONString(), headers, null);
		System.out.println(result);
	}
	
	@Test
	public void testHttpClient() throws InterruptedException{
		int count=10;
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		CountDownLatch countDownLatch = new CountDownLatch(10);
		cm.setMaxTotal(10);
		cm.setDefaultMaxPerRoute(3);
		
		HttpHost httphost=HttpHost.create("https://www.baidu.com");
		HttpRoute httproute=new HttpRoute(httphost);
		cm.setMaxPerRoute(httproute, 5);
		ExecutorService executors = Executors.newFixedThreadPool(10);
//		executors=Executors.newSingleThreadExecutor();
		for(int i=0;i<10;i++){
			final int ii=i;
			executors.submit(()->{
//				int max=cm.getStats(new HttpRoute(new HttpHost("www.google.com.hk", 80,"https"))).getMax();
//				if(max==5){
//					cm.setDefaultMaxPerRoute(10);
//				}
				try {
					/**
					 * Socket timeout in SocketConfig represents the default value applied to newly created connections. 
					 * This value can be overwritten for individual requests by setting a non zero value of socket timeout in RequestConfig.
					 */
					RequestConfig config=RequestConfig.custom().setConnectionRequestTimeout(1).build();
					SocketConfig sockConfig=SocketConfig.custom().setSoTimeout(5000).build();
					String url="https://www.baidu.com";
					HttpGet get=new HttpGet(url);
					get.setConfig(config);
					CloseableHttpClient httpClient=HttpClients.custom().setDefaultRequestConfig(config).setDefaultSocketConfig(sockConfig).setConnectionManager(cm).build();
					CloseableHttpResponse response=httpClient.execute(get);
					HttpEntity entity=response.getEntity();
					InputStream is=entity.getContent();
					System.out.println(ii);
//					System.out.println(StreamUtils.copyToString(is, Charset.forName("UTF-8")));
//					response.close();
					is.close();
				} catch (Exception e) {
//					e.printStackTrace();
					System.err.println(e.getMessage());
				}finally {
					
					System.out.println(cm.getStats(httproute));;
				}
				countDownLatch.countDown();
				
			});
		}
		countDownLatch.await();
		executors.shutdown();
	}


}
