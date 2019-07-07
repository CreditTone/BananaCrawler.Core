package banana.core.download.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.Set;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

import banana.core.download.HttpDownloader;
import banana.core.download.pool.HttpClientPool;
import banana.core.request.BinaryRequest;
import banana.core.request.Cookie;
import banana.core.request.Cookies;
import banana.core.request.HttpRequest;
import banana.core.request.PageRequest;
import banana.core.request.RequestBuilder;
import banana.core.request.HttpRequest.Method;
import banana.core.request.PageRequest.PageEncoding;
import banana.core.response.Page;
import banana.core.response.StreamResponse;

/**
 * 缺省的PageDownloader使用HttpClient作为下载内核
 */
public class DefaultHttpDownloader implements HttpDownloader{

	private final Logger log = Logger.getLogger(DefaultHttpDownloader.class);

	private int timeout = 15;

	private HttpsProxy httpsProxy;
	
	private CloseableHttpClient httpClient;
	
	private BasicCookieStore cookieStore;
	
	private String userAgent;
	
	public DefaultHttpDownloader() {
		this(null);
	}

	public DefaultHttpDownloader(Cookies initCookies) {
		cookieStore = new BasicCookieStore();
		if (initCookies != null) {
			Iterator<Cookie> iter = initCookies.iterator();
			while(iter.hasNext()){
				Cookie cookie = iter.next();
				cookieStore.addCookie(cookie.convertHttpClientCookie());
			}
		}
		try {
			httpClient = HttpClientPool.createHttpClient(cookieStore);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	private BasicHttpContext getHttpContext(HttpRequest request) {
		BasicHttpContext defaultContext = new BasicHttpContext();
		Builder build = RequestConfig.custom().setSocketTimeout(timeout * 1000 * 3).setConnectTimeout(timeout * 1000)
				.setConnectionRequestTimeout(timeout * 1000).setRedirectsEnabled(true).setCircularRedirectsAllowed(true);
		HttpsProxy tempProxy = request.getHttpsProxy()!=null?request.getHttpsProxy():this.httpsProxy;
		if (tempProxy != null) {
			HttpHost proxy = new HttpHost(tempProxy.getServer(),tempProxy.getPort());
			build.setProxy(proxy);
			if (tempProxy.getUsername() != null && tempProxy.getPassword() != null) {
				CredentialsProvider credsProvider = new BasicCredentialsProvider(); //
				  credsProvider.setCredentials(new AuthScope(tempProxy.getServer(), tempProxy.getPort()),
				  new UsernamePasswordCredentials(tempProxy.getUsername(), tempProxy.getPassword()));
				  defaultContext.setAttribute(HttpClientContext.CREDS_PROVIDER, credsProvider);
			}
		}
		defaultContext.setAttribute(HttpClientContext.REQUEST_CONFIG, build.build());
		return defaultContext;
	}
	
	public Page download(String url) {
		try {
			Page page = download((PageRequest) RequestBuilder.custom().setUrl(url).build());
			return page;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Page download(String url,PageEncoding pageEncoding) {
		try {
			Page page = download((PageRequest) RequestBuilder.custom().setUrl(url).setPageEncoding(pageEncoding).build());
			return page;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Page download(PageRequest request) {
		Page page = null;
		HttpRequestBase method = null;
		try {
			method = buildHttpUriRequest(request);
			HttpContext httpContext = getHttpContext(request);
			HttpResponse response = httpClient.execute(method, httpContext);
			page = new Page(request, response);
			page.setDriverId(String.valueOf(httpClient.hashCode()));
            page.setOwnerUrl(getOwnerUrl(httpContext));
            page.setRedirected(!method.getURI().toString().equals(page.getOwnerUrl()));
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("download error " + request.getUrl(), e);
		} finally {
			if (method != null) {
				method.abort();
				method.releaseConnection();
			}
		}
		return page;
	}
	
	
	public Page downloadWithVrify(PageRequest request,String verifyPattern,int maxRetry) {
		Page page = null;
		Pattern verifyPat = null;
		try {
			verifyPat = Pattern.compile(verifyPattern);
		}catch(Exception e) {}
		for (int i = 0 ; i < maxRetry ; i ++) {
			System.out.println("downloadWithVrify:"+request.getUrl());
			try {Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			page = download(request);
			if (page == null) {
				continue;
			}
			System.out.println(Jsoup.parse(page.getContent()).text());
			if (page.getContent().contains(verifyPattern)) {
				break;
			}
			if (verifyPat != null && verifyPat.matcher(page.getContent()).find()) {
				break;
			}
		}
		return page;
	}
	
	public StreamResponse downloadBinary(BinaryRequest request) {
		HttpRequestBase method = null;
		StreamResponse stream = null;
		try {
			method = buildHttpUriRequest(request);
			HttpContext context = getHttpContext(request);
			HttpResponse response = httpClient.execute(method, context);
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
		}
		return stream;
	}
	
	private String getOwnerUrl(HttpContext context) {
		HttpHost targetHost = (HttpHost)context.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
		//BasicHttpRequest realRequest = (BasicHttpRequest)context.getAttribute(HttpCoreContext.HTTP_REQUEST);
		return targetHost.toString();
	}
	
	

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
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
		if (userAgent != null) {
			headers.put("User-Agent", userAgent);
		}
		headers.putAll(custom_headers);// 覆盖自定义请求头
		Set<Entry<String, String>> keyValues = headers.entrySet();
		if (request.getMethod() == Method.GET) {
			HttpGet get = new HttpGet(request.getUrl());
			// 设置请求头
			for (Entry<String, String> entry : keyValues) {
				get.setHeader(entry.getKey(), entry.getValue());
			}
			return get;
		}else if (request.getMethod() == Method.POST) {
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
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs,Charset.forName("UTF-8")));
//				post.setEntity(new StringEntity(nameValuePairs));
			}
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
		headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:56.0) Gecko/20100101 Firefox/56.0");
		return headers;
	}

	public boolean supportJavaScript() {
		return false;
	}

	public void setTimeout(int second) {
		this.timeout = second;
	}
	
	public void setProxy(HttpsProxy proxy) {
		this.httpsProxy = proxy;
	}

	public void injectCookies(Cookies cookies) {
		Iterator<Cookie> iter = cookies.iterator();
		while(iter.hasNext()){
			Cookie cookie = iter.next();
			cookieStore.addCookie(cookie.convertHttpClientCookie());
		}
	}

	public Cookies getCookies() {
		List<org.apache.http.cookie.Cookie> cookies = cookieStore.getCookies();
		Cookies cookies2 = new Cookies();
		for (org.apache.http.cookie.Cookie cookie : cookies) {
			cookies2.addCookie(new Cookie(cookie.getName(), cookie.getValue(), cookie.getDomain(), cookie.getPath(),
					cookie.getExpiryDate(), cookie.isSecure(), false));
		}
		return cookies2;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void open() {
		// TODO Auto-generated method stub
		
	}

}
