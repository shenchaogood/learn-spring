package sc.learn.test.common;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
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
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
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
	public void testHttpClient() throws InterruptedException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException{
		int count=10;
		SSLContextBuilder builder = new SSLContextBuilder();
        // 全部信任 不做身份鉴定
        builder.loadTrustMaterial(null, new TrustStrategy() {
			public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				return true;
			}
        });
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(), new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new PlainConnectionSocketFactory())
                .register("https", sslsf)
                .build();
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
		
		CountDownLatch countDownLatch = new CountDownLatch(10);
		cm.setMaxTotal(count);
		cm.setDefaultMaxPerRoute(3);
		
		HttpHost httphost=HttpHost.create("https://shenchao.xin:8443");
		HttpRoute httproute=new HttpRoute(httphost);
		cm.setMaxPerRoute(httproute, 5);
		ExecutorService executors = Executors.newFixedThreadPool(10);
		for(int i=0;i<count;i++){
			final int ii=i;
			executors.submit(()->{
				try {
					
					RequestConfig config=RequestConfig.custom().setConnectionRequestTimeout(1).build();
					SocketConfig sockConfig=SocketConfig.custom().setSoTimeout(5000).build();
					String url="https://shenchao.xin:8443";
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
