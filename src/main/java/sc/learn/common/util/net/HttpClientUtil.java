package sc.learn.common.util.net;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger LOGGER =LoggerFactory.getLogger(HttpClientUtil.class);
    private static final String CHARSET="UTF-8";
    private static final int MAX_TIMEOUT = 7000;
    private static final int MAX_CONNECTION=100;
    
    private static PoolingHttpClientConnectionManager CONNECTION_MANAGER=generatePoolConnectionManager();
    /**
	 * Socket timeout in SocketConfig represents the default value applied to newly created connections. 
	 * This value can be overwritten for individual requests by setting a non zero value of socket timeout in RequestConfig.
	 */
    private static final RequestConfig DEFAULT_REQUEST_CONFIG=RequestConfig.custom()
    		.setConnectionRequestTimeout(1000)
    		.setConnectTimeout(MAX_TIMEOUT)
    		.setConnectTimeout(MAX_TIMEOUT)
    		.build();
    private static final SocketConfig DEFAULT_SOCKET_CONFIG=SocketConfig.custom().setSoTimeout(MAX_TIMEOUT).build();
    private static final Set<BasicHeader> DEFAULT_HEADERS=Sets.newHashSet(new BasicHeader(HttpHeaders.ACCEPT, "*/*"));
  
    private static PoolingHttpClientConnectionManager generatePoolConnectionManager(){
    	PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager();  
        connMgr.setMaxTotal(MAX_CONNECTION);  
        connMgr.setDefaultMaxPerRoute(MAX_CONNECTION/5*4);  
        //关闭空闲两分钟的连接
        connMgr.closeIdleConnections(300, TimeUnit.SECONDS);
        return connMgr;
    }
    

    private static void config(HttpRequestBase httpRequestBase,Map<String,String> headers,RequestConfig requestConfig) {
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
     * 创建HttpClient对象
     * 
     */
    public static CloseableHttpClient createHttpClient(String url) {
        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception,int executionCount, HttpContext context) {
            	if(Objects.nonNull(context)){//TODO 先不重试 后期调整策略
            		return false;
            	}else if (executionCount >= 5) {// 如果已经重试了5次，就放弃
                    return false;
                }else if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }else if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }else if (exception instanceof InterruptedIOException) {// 超时
                    return false;
                }else if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }else if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }else if (exception instanceof SSLException) {// SSL握手异常
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
        
        CloseableHttpClient httpClient = HttpClients.custom()
        		.setDefaultHeaders(DEFAULT_HEADERS)
        		.setConnectionManager(CONNECTION_MANAGER)
        		.setDefaultRequestConfig(DEFAULT_REQUEST_CONFIG)
        		.setDefaultSocketConfig(DEFAULT_SOCKET_CONFIG)
        		.setRetryHandler(httpRequestRetryHandler)
        		.build();
        return httpClient;
    }

    private static void setPostParams(HttpPost httpost,Map<String, Object> params) throws UnsupportedEncodingException {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key).toString()));
        }
        httpost.setEntity(new UrlEncodedFormEntity(nvps, CHARSET));
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
        return doRequest(url, httppost);
    }
    
    /**
     * POST请求URL获取内容
     */
    public static HttpResult post(String url, String stringEntity,Map<String,String> headers,
    		RequestConfig requestConfig) throws IOException {
        HttpPost httppost = new HttpPost(url);
        config(httppost,headers,requestConfig);
        setPostParams(httppost, stringEntity);
        return doRequest(url, httppost);
    }

	private static HttpResult doRequest(String url, HttpRequestBase httpbase)
			throws IOException {
		CloseableHttpResponse response = null;
		try {
        	LOGGER.debug("{}",httpbase);
        	LOGGER.debug("{}",Arrays.toString(httpbase.getAllHeaders()));
        	if(httpbase instanceof HttpEntityEnclosingRequestBase){
        		HttpEntityEnclosingRequestBase httppost=(HttpEntityEnclosingRequestBase)httpbase;
        		LOGGER.debug("{}",httppost.getEntity());
        		LOGGER.debug("{}",EntityUtils.toString(httppost.getEntity()));
        	}
            response = createHttpClient(url).execute(httpbase,HttpClientContext.create());
            LOGGER.debug("{}",response);
            int statusCode=response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity,CHARSET);
            LOGGER.debug("{}",content);
            EntityUtils.consume(entity);
            return new HttpResult(statusCode, content);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (response != null){
                	response.close();
                }
            } catch (IOException e) {}
        }
	}
    

    /**
     * GET请求URL获取内容
     */
    public static HttpResult get(String url, Map<String, Object> params,Map<String,String> headers,RequestConfig requestConfig) throws IOException{
        HttpGet httpget = new HttpGet(url);
        config(httpget,headers,requestConfig);
        return doRequest(url, httpget);
    }

}