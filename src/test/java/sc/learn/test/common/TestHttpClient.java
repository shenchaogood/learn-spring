package sc.learn.test.common;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
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
