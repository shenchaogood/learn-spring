package sc.learn.common.util.net;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.google.common.collect.Sets;

/**
 * HttpClient工具类
 */
public class HttpClientUtil {
	
	public static class HttpResult{
		public final int statusCode;
		public final String content;
		public HttpResult(int statusCode, String content) {
			this.statusCode = statusCode;
			this.content = content;
		}
		@Override
		public String toString() {
			return "HttpResult [statusCode=" + statusCode + ", content=" + content + "]";
		}
	}

    private static final int TIMEOUT = 10 * 1000;
    private static CloseableHttpClient httpClient = null;
    private final static Object SYN_CLOCK = new Object();
    private static final String CHARSET="UTF-8";

    private static void config(@Nonnull HttpRequestBase httpRequestBase,Map<String,String> headers,RequestConfig requestConfig) {
        // 设置Header等
        // httpRequestBase.setHeader("User-Agent", "Mozilla/5.0");
        // httpRequestBase.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        // httpRequestBase.setHeader("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");// "en-US,en;q=0.5");
        // httpRequestBase.setHeader("Accept-Charset","ISO-8859-1,utf-8,gbk,gb2312;q=0.7,*;q=0.7");
    	if(headers!=null){
    		headers.forEach((name,value)->httpRequestBase.setHeader(name, value));
    	}
        httpRequestBase.setConfig(requestConfig);
    }

    /**
     * 获取HttpClient对象
     */
    public static CloseableHttpClient getHttpClient(String url) {
        String hostname = url.split("/")[2];
        int port = 80;
        if (hostname.contains(":")) {
            String[] arr = hostname.split(":");
            hostname = arr[0];
            port = Integer.parseInt(arr[1]);
        }
        if (httpClient == null) {
            synchronized (SYN_CLOCK) {
                if (httpClient == null) {
                    httpClient = createHttpClient(200, 40, 100, hostname, port);
                }
            }
        }
        return httpClient;
    }

    /**
     * 创建HttpClient对象
     * 
     */
    public static CloseableHttpClient createHttpClient(int maxTotal, int defaultMaxPerRoute, int maxPerRoute, String hostname, int port) {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create().register("http", plainsf)
                .register("https", sslsf).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        // 将最大连接数增加
        cm.setMaxTotal(maxTotal);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(defaultMaxPerRoute);
        HttpHost httpHost = new HttpHost(hostname, port);
        // 将目标主机的最大连接数增加
        cm.setMaxPerRoute(new HttpRoute(httpHost), maxPerRoute);

        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception,int executionCount, HttpContext context) {
//                if (executionCount >= 5) {// 如果已经重试了5次，就放弃
//                    return false;
//                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// SSL握手异常
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };
        
        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(TIMEOUT)
                .setConnectTimeout(TIMEOUT).setSocketTimeout(TIMEOUT).build();
        SocketConfig sockConfig=SocketConfig.custom().setSoTimeout(TIMEOUT).build();
        Set<BasicHeader> headers=Sets.newHashSet(new BasicHeader(HttpHeaders.ACCEPT, "*/*"));
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultHeaders(headers).setConnectionManager(cm)
        		.setDefaultRequestConfig(requestConfig).setDefaultSocketConfig(sockConfig).setRetryHandler(httpRequestRetryHandler).build();
        return httpClient;
    }

    private static void setPostParams(HttpPost httpost,Map<String, Object> params) {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key).toString()));
        }
        try {
            httpost.setEntity(new UrlEncodedFormEntity(nvps, CHARSET));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
    private static void setPostParams(HttpPost httpost,String stringEntity) {
        httpost.setEntity(new StringEntity(stringEntity, CHARSET));
    }

    /**
     * POST请求URL获取内容
     */
    public static HttpResult post(String url, Map<String, Object> params,Map<String,String> headers,
    		RequestConfig requestConfig) throws IOException {
        HttpPost httppost = new HttpPost(url);
        config(httppost,headers,requestConfig);
        setPostParams(httppost, params);
        CloseableHttpResponse response = null;
        try {
            response = getHttpClient(url).execute(httppost,HttpClientContext.create());
            int statusCode=response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity,CHARSET);
            EntityUtils.consume(entity);
            return new HttpResult(statusCode, content);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (response != null){
                	response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * POST请求URL获取内容
     */
    public static HttpResult post(String url, String stringEntity,Map<String,String> headers,
    		RequestConfig requestConfig) throws IOException {
        HttpPost httppost = new HttpPost(url);
        config(httppost,headers,requestConfig);
        setPostParams(httppost, stringEntity);
        CloseableHttpResponse response = null;
        try {
            response = getHttpClient(url).execute(httppost,HttpClientContext.create());
            int statusCode=response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity,CHARSET);
            EntityUtils.consume(entity);
            return new HttpResult(statusCode, content);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (response != null){
                	response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    

    /**
     * GET请求URL获取内容
     */
    public static HttpResult get(String url, Map<String, Object> params,Map<String,String> headers,RequestConfig requestConfig) throws IOException{
        HttpGet httpget = new HttpGet(url);
        config(httpget,headers,requestConfig);
        CloseableHttpResponse response = null;
        try {
            response = getHttpClient(url).execute(httpget,HttpClientContext.create());
            int statusCode=response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, CHARSET);
            EntityUtils.consume(entity);
            return new HttpResult(statusCode, content);
        } catch (IOException e) {
        	throw e;
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}