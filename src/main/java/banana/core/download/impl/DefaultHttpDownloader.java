package banana.core.download.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Set;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.log4j.Logger;

import banana.core.download.HttpDownloader;
import banana.core.download.pool.HttpClientPool;
import banana.core.request.BinaryRequest;
import banana.core.request.Cookie;
import banana.core.request.Cookies;
import banana.core.request.HttpRequest;
import banana.core.request.PageRequest;
import banana.core.response.Page;
import banana.core.response.StreamResponse;

/**
 * 缺省的PageDownloader使用HttpClient作为下载内核
 */
public class DefaultHttpDownloader extends HttpDownloader {

	private final Logger log = Logger.getLogger(DefaultHttpDownloader.class);

	private int timeout = 15;

	private volatile HttpClientPool httpClientPool;
	
	protected Set<String> blockedDrivers = Collections.synchronizedSet(new HashSet<String>());

	public DefaultHttpDownloader() {
		this(null);
	}

	public DefaultHttpDownloader(Cookies initCookies) {
		httpClientPool = new HttpClientPool(initCookies);
	}

	@Override
	public void close() throws IOException {
		if (httpClientPool != null) {
			httpClientPool.closeAll();
		}
	}

	@Override
	public Page download(PageRequest request) {
		Page page = null;
		CloseableHttpClient client = null;
		HttpRequestBase method = null;
		try {
			client = httpClientPool.get();
			while (blockedDrivers.contains(String.valueOf(client.hashCode()))) {
				httpClientPool.returnToPool(client);
				Thread.sleep(1000);
				log.warn("driver blocked " + String.valueOf(client.hashCode()));
				client = httpClientPool.get();
			}
			method = buildHttpUriRequest(request);
			HttpClientContext context = HttpClientContext.create();
			HttpResponse response = client.execute(method, context);
			page = new Page(request, response);
			page.setDriverId(String.valueOf(client.hashCode()));
            page.setOwnerUrl(getOwnerUrl(context));
            page.setRedirected(!method.getURI().toString().equals(page.getOwnerUrl()));
		} catch (Exception e) {
			page = new Page();
			page.setStatus(500);
			page.setDriverId(String.valueOf(client.hashCode()));
			log.warn("download error " + request.getUrl(), e);
		} finally {
			if (method != null) {
				method.abort();
				method.releaseConnection();
			}
			httpClientPool.returnToPool(client);
		}
		return page;
	}

	@Override
	public StreamResponse downloadBinary(BinaryRequest request) {
		CloseableHttpClient client = null;
		HttpRequestBase method = null;
		StreamResponse stream = null;
		try {
			client = httpClientPool.get();
			while (blockedDrivers.contains(String.valueOf(client.hashCode()))) {
				httpClientPool.returnToPool(client);
				Thread.sleep(1000);
				log.warn("driver blocked " + String.valueOf(client.hashCode()));
				client = httpClientPool.get();
			}
			method = buildHttpUriRequest(request);
			HttpClientContext context = HttpClientContext.create();
			HttpResponse response = client.execute(method, context);
			stream = new StreamResponse(request, response);
			stream.setOwnerUrl(getOwnerUrl(context));
			stream.setRedirected(!method.getURI().toString().equals(stream.getOwnerUrl()));
		} catch (Exception e) {
			log.warn("download error " + request.getUrl(), e);
		} finally {
			if (method != null) {
				method.abort();
				method.releaseConnection();
			}
			httpClientPool.returnToPool(client);
		}
		return stream;
	}
	
	private String getOwnerUrl(HttpClientContext context){
		HttpHost targetHost = (HttpHost)context.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
        HttpUriRequest realRequest = (HttpUriRequest)context.getAttribute(HttpCoreContext.HTTP_REQUEST);
        return targetHost.toString() + realRequest.getURI().toString();
	}
	
	/**
	 * private final HttpContext setProxyIpAndTimeOut(HttpRequestBase method,int
	 * timeout){ BasicHttpContext httpContext = new BasicHttpContext(); Builder
	 * builder =
	 * RequestConfig.custom().setSocketTimeout(timeout*1000).setConnectTimeout(
	 * timeout*1000).setAuthenticationEnabled(true);//设置请求和传输超时时间; // if(ip !=
	 * null){ // HttpHost proxy = new HttpHost(ip.getIp(),ip.getPort()); //
	 * builder.setProxy(proxy); // if (ip.getUsername() != null &&
	 * ip.getPassword() !=null){ // builder.setAuthenticationEnabled(true); //
	 * httpContext = new BasicHttpContext(); // CredentialsProvider
	 * credsProvider = new BasicCredentialsProvider(); //
	 * credsProvider.setCredentials( // new AuthScope(ip.getIp(), ip.getPort()),
	 * // new UsernamePasswordCredentials(ip.getUsername(), ip.getPassword()));
	 * // httpContext.setAttribute(HttpClientContext.CREDS_PROVIDER,
	 * credsProvider); // } // log.info(String.format("set proxy %s",
	 * ip.toString())); // } RequestConfig config = builder.build();
	 * method.setConfig(config);
	 * httpContext.setAttribute(HttpClientContext.REQUEST_CONFIG, config);
	 * return httpContext; }
	 **/
	public void setMaxDriverCount(int drivercount) {
		httpClientPool.setMaxDriverCount(drivercount);
	}

	public void setMinDriverCount(int drivercount) {
		httpClientPool.setMinDriverCount(drivercount);
	}

	@Override
	public void open() {
		httpClientPool.open();
	}

	/**
	 * 根据request构建get或者post请求
	 * 
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private final HttpRequestBase buildHttpUriRequest(HttpRequest request) throws UnsupportedEncodingException {
		Map<String, String> custom_headers = request.getHedaers();
		Map<String, String> headers = getFirefoxHeaders();
		headers.putAll(custom_headers);// 覆盖自定义请求头
		Set<Entry<String, String>> keyValues = headers.entrySet();
		RequestConfig config = RequestConfig.custom().setSocketTimeout(timeout * 1000).setConnectTimeout(timeout * 1000)
				.setConnectionRequestTimeout(timeout * 1000).setRedirectsEnabled(true).setCircularRedirectsAllowed(true)
				.build();
		switch (request.getMethod()) {
		case GET:
			HttpGet get = new HttpGet(request.getEncodeUrl());
			// 设置请求头
			for (Entry<String, String> entry : keyValues) {
				get.setHeader(entry.getKey(), entry.getValue());
			}
			get.setConfig(config);
			return get;
		case POST:
			HttpPost post = new HttpPost(request.getUrl());
			// 设置请求头
			for (Entry<String, String> entry : keyValues) {
				post.setHeader(entry.getKey(), entry.getValue());
			}
			// 设置请求参数
			Set<Entry<String, String>> params = request.getParams();
			if (!params.isEmpty()) {
				List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
				for (Entry<String, String> entry : params) {
					BasicNameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
					nameValuePairs.add(pair);
				}
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			}
			post.setConfig(config);
			return post;
		}
		return null;
	}

	private static final Map<String, String> getFirefoxHeaders() {
		Map<String, String> headers = new HashMap<String, String>();
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

	@Override
	public void setTimeout(int second) {
		this.timeout = second;
	}

	@Override
	public void injectCookies(Cookies cookies) {
		Iterator<CloseableHttpClient> drivers = httpClientPool.drivers();
		httpClientPool.setCookies(cookies);
		while (drivers.hasNext()) {
			CloseableHttpClient client = drivers.next();
			blockedDrivers.remove(String.valueOf(client.hashCode()));
		}
	}

	@Override
	public void blockDriver(String driverId) {
		blockedDrivers.add(driverId);
	}

	@Override
	public Cookies getCookies() {
		List<org.apache.http.cookie.Cookie> cookies = this.httpClientPool.getCookieStore().getCookies();
		Cookies cookies2 = new Cookies();
		for (org.apache.http.cookie.Cookie cookie : cookies) {
			cookies2.addCookie(new Cookie(cookie.getName(), cookie.getValue(), cookie.getDomain(), cookie.getPath(),
					cookie.getExpiryDate(), cookie.isSecure(), false));
		}
		return cookies2;
	}

}
