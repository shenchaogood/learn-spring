package sc.learn.test.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class TestHttpClient {

	@Test
	public void testHttpClient() throws InterruptedException, NoSuchAlgorithmException, KeyStoreException,
			KeyManagementException, CertificateException, IOException, UnrecoverableKeyException {
		int count = 10;
		SSLContextBuilder builder = new SSLContextBuilder();
		// 全部信任 不做身份鉴定
		builder.loadTrustMaterial(null, new TrustStrategy() {
			public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				return true;
			}
		});
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(),
				new String[] { "SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2" }, null, NoopHostnameVerifier.INSTANCE);

		// 指定读取证书格式为PKCS12
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		// 读取本机存放的PKCS12证书文件
		InputStream instream = getClass().getResourceAsStream("/dibai_server.pk12");
		// 指定PKCS12的密码(商户ID)
		keyStore.load(instream, "dibaidanche".toCharArray());
		instream.close();
		SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, "dibaidanche".toCharArray()).build();
		// 指定TLS版本
		sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2" },
				null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

		Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", new PlainConnectionSocketFactory()).register("https", sslsf).build();
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);

		CountDownLatch countDownLatch = new CountDownLatch(10);
		cm.setMaxTotal(count);
		cm.setDefaultMaxPerRoute(3);

		HttpHost httphost = HttpHost.create("https://shenchao.xin:8443");
		HttpRoute httproute = new HttpRoute(httphost);
		cm.setMaxPerRoute(httproute, 5);
		ExecutorService executors = Executors.newFixedThreadPool(10);
		for (int i = 0; i < count; i++) {
			final int ii = i;
			executors.submit(() -> {
				try {

					RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(1).build();
					SocketConfig sockConfig = SocketConfig.custom().setSoTimeout(5000).build();
					String url = "https://shenchao.xin:8443";
					HttpGet get = new HttpGet(url);
					get.setConfig(config);
					CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(config)
							.setDefaultSocketConfig(sockConfig).setConnectionManager(cm).build();
					CloseableHttpResponse response = httpClient.execute(get);
					HttpEntity entity = response.getEntity();
					System.out.println(entity.getContentEncoding().getValue());
					InputStream is = entity.getContent();
					System.out.println(ii);
					// System.out.println(StreamUtils.copyToString(is,
					// Charset.forName("UTF-8")));
					// response.close();
					is.close();
				} catch (Exception e) {
					// e.printStackTrace();
					System.err.println(e.getMessage());
				} finally {

					System.out.println(cm.getStats(httproute));
					;
				}
				countDownLatch.countDown();

			});
		}
		countDownLatch.await();
		executors.shutdown();
	}

	/**
	 * 设置信任自签名证书
	 * 
	 * @param keyStorePath
	 *            密钥库路径
	 * @param keyStorepass
	 *            密钥库密码
	 * @return
	 */
	public static SSLContext custom(String keyStorePath, String keyStorepass) {
		SSLContext sc = null;
		FileInputStream instream = null;
		KeyStore trustStore = null;
		try {
			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			instream = new FileInputStream(new File(keyStorePath));
			trustStore.load(instream, keyStorepass.toCharArray());
			// 相信自己的CA和所有自签名的证书
			sc = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException
				| KeyManagementException e) {
			e.printStackTrace();
		} finally {
			try {
				instream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sc;
	}

	/**
	 * 模拟请求
	 * 
	 * @param url
	 *            资源地址
	 * @param map
	 *            参数列表
	 * @param encoding
	 *            编码
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws ClientProtocolException
	 */
	public static String send(String url, Map<String, String> map, String encoding)
			throws ClientProtocolException, IOException {
		String body = "";

		// tomcat是我自己的密钥库的密码，你可以替换成自己的
		// 如果密码为空，则用"nopassword"代替
		String keyPath=TestHttpClient.class.getResource("/client.keystore").getFile();
		SSLContext sslcontext = custom(keyPath, "123456");

		// 设置协议http和https对应的处理socket链接工厂的对象
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", new SSLConnectionSocketFactory(sslcontext)).build();
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		HttpClients.custom().setConnectionManager(connManager);

		// 创建自定义的httpclient对象
		CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).build();

		// 创建post方式请求对象
		HttpPost httpPost = new HttpPost(url);

		// 装填参数
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (map != null) {
			for (Entry<String, String> entry : map.entrySet()) {
				nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		// 设置参数到请求对象中
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));

		System.out.println("请求地址：" + url);
		System.out.println("请求参数：" + nvps.toString());

		// 设置header信息
		// 指定报文头【Content-type】、【User-Agent】
		httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
		httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

		// 执行请求操作，并拿到结果（同步阻塞）
		CloseableHttpResponse response = client.execute(httpPost);
		// 获取结果实体
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			// 按指定编码转换结果实体为String类型
			body = EntityUtils.toString(entity, encoding);
		}
		EntityUtils.consume(entity);
		// 释放链接
		response.close();
		return body;
	}

	@Test
	public void testSSL() throws ClientProtocolException, IOException{
		String url = "https://www.shenchao.xin:8443";
		String body = send(url, null, "utf-8");
		System.out.println("交易响应结果长度：" + body.length());
	}
}
