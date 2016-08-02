package banana.core.download.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import banana.core.download.PageDownloader;
import banana.core.download.pool.HttpClientPool;
import banana.core.request.PageRequest;
import banana.core.response.Page;


/**
 * 缺省的PageDownloader使用HttpClient作为下载内核
 */
public class DefaultPageDownloader extends PageDownloader{

	private final Logger log = Logger.getLogger(DefaultPageDownloader.class);
	private int timeout = 15;
	
	private volatile HttpClientPool httpClientPool;
	
	public DefaultPageDownloader(){}
	
	@Override
	public void close() throws IOException {
		if(httpClientPool != null){
			httpClientPool.closeAll();
		}
	}

	@Override
	public Page download(PageRequest request,String taskName) {
		Page page = go(request);
		return page;
	}
	
	
	/**
	 * 去下载
	 * @param request
	 * @param task
	 * @return
	 * @throws ProxyIpLoseException
	 */
	public Page go(PageRequest request){
		Page  page = null;
		int statuCode = 0;
		ZHttpClient client =  null;
		HttpRequestBase method = null;
		try {
			client = httpClientPool.get();
			method = buildHttpUriRequest(request);
			HttpContext httpContext = setProxyIpAndTimeOut(method,timeout);
			HttpResponse response = client.execute(method,httpContext);
			statuCode = response.getStatusLine().getStatusCode();
			//获取网页内容
			String content = EntityUtils.toString(response.getEntity(),request.getPageEncoding().toString());
			page = new Page();
			page.setContent(content);
			page.setStatus(statuCode);
			page.setRequest(request);
			page.setContentType(response.getEntity().getContentType().getValue());
			Header []headers = response.getAllHeaders();
			Map<String,String> headerCopy = new HashMap<String,String>();
			for (int i = 0; i < headers.length; i++) {
				headerCopy.put(headers[i].getName(), headers[i].getValue());
			}
			page.setResponseHeader(headerCopy);
		} catch (Exception e) {
			if(e instanceof NullPointerException){
				throw new RuntimeException(e); 
			}
			log.warn("download error",e);
		}finally {
			if(method != null){
				method.abort();
				method.releaseConnection();
				httpClientPool.returnToPool(client);
			}
		}
		return page;
	}
	
	private final HttpContext setProxyIpAndTimeOut(HttpRequestBase method,int timeout){
		HttpContext httpContext = null;
		Builder builder = RequestConfig.custom().setSocketTimeout(timeout*1000).setConnectTimeout(timeout*1000);//设置请求和传输超时时间;
//		if(ip != null){
//			HttpHost proxy = new HttpHost(ip.getIp(),ip.getPort()); 
//			builder.setProxy(proxy);
//			if (ip.getUsername() != null && ip.getPassword() !=null){
//				builder.setAuthenticationEnabled(true);
//				httpContext = new BasicHttpContext();
//				CredentialsProvider credsProvider = new BasicCredentialsProvider();
//				credsProvider.setCredentials(
//			                new AuthScope(ip.getIp(), ip.getPort()),
//			                new UsernamePasswordCredentials(ip.getUsername(), ip.getPassword()));
//				httpContext.setAttribute(HttpClientContext.CREDS_PROVIDER, credsProvider);
//			}
//			log.info(String.format("set proxy %s", ip.toString()));
//		}
		method.setConfig(builder.build());
		return httpContext;
	}

	@Override
	public void setTimeout(int second) {
		this.timeout = second;
	}
	
	public void setMaxDriverCount(int drivercount) {
		checkInit();
		httpClientPool.setMaxDriverCount(drivercount);
	}
	
	public void setMinDriverCount(int drivercount){
		checkInit();
		httpClientPool.setMinDriverCount(drivercount);
	}
	
	@Override
	public void open() {
		checkInit();
		httpClientPool.open();
	}
	
	 private void checkInit() {
	        if (httpClientPool == null) {
	            synchronized (this){
	            	httpClientPool = new HttpClientPool();
	         }
	     }
	 }
	
	/**
	 * 根据request构建get或者post请求
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private final HttpRequestBase buildHttpUriRequest(PageRequest request) throws UnsupportedEncodingException{
		Map<String,String> custom_headers = request.getHedaers();
		Map<String,String> headers = getFirefoxHeaders();
		headers.putAll(custom_headers);//覆盖自定义请求头
		Set<Entry<String, String>> keyValues = headers.entrySet();
		Builder builder = RequestConfig.custom().setSocketTimeout(10*1000).setConnectTimeout(10*1000).setRedirectsEnabled(false);
		switch(request.getMethod()){
			case GET:
				HttpGet get = new HttpGet(request.getUrl());
				//设置请求头
				for (Entry<String, String> entry : keyValues) {
					get.setHeader(entry.getKey(), entry.getValue());
				}
				get.setConfig(builder.build());
				return get;
			case POST:
				HttpPost post = new HttpPost(request.getUrl());
				//设置请求头
				for (Entry<String, String> entry : keyValues) {
					post.setHeader(entry.getKey(), entry.getValue());
				}
				//设置请求参数
				Set<Entry<String, String>> params = request.getParams();
				if(!params.isEmpty()){
					List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
					for (Entry<String, String> entry : params) {
						BasicNameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
						nameValuePairs.add(pair);
					}
					post.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
				}
				post.setConfig(builder.build());
				return post;
		}
		return null;
	}
	
	private static final Map<String,String> getFirefoxHeaders(){
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		headers.put("Accept-Encoding", "gzip, deflate");
		headers.put("Connection", "keep-alive");
		headers.put("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
		headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:35.0) Gecko/20100101 Firefox/35.0");
		return headers;
	}

	@Override
	public boolean supportJavaScript() {
		return false;
	}
	
	
}
