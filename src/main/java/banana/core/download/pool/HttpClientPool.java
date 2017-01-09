package banana.core.download.pool;

import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;

import banana.core.download.impl.HttpCookieSpecProvider;
import banana.core.request.Cookie;
import banana.core.request.Cookies;


public final class HttpClientPool extends DriverPoolInterface<CloseableHttpClient>{
	

	private BasicCookieStore cookieStore = new BasicCookieStore();
	
	private Registry<CookieSpecProvider> registry = RegistryBuilder.<CookieSpecProvider> create()
			.register(CookieSpecs.DEFAULT, new HttpCookieSpecProvider())
			.register(CookieSpecs.BROWSER_COMPATIBILITY, new HttpCookieSpecProvider())
			.register(CookieSpecs.NETSCAPE, new HttpCookieSpecProvider())
			.register(CookieSpecs.BEST_MATCH, new HttpCookieSpecProvider())
			.register(CookieSpecs.IGNORE_COOKIES, new HttpCookieSpecProvider())
			.register(CookieSpecs.STANDARD, new HttpCookieSpecProvider())
			.register(CookieSpecs.STANDARD_STRICT, new HttpCookieSpecProvider())
			.build();

    public HttpClientPool(Cookies initCookie) {
		if (initCookie != null){
			setCookies(initCookie);
		}
    }

    /**
     * 创建核心实例
     */
	public final CloseableHttpClient createDriver(){
		CloseableHttpClient poll = null;
		try {
			poll = createHttpClient(cookieStore);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return poll;
	}

    /**
     * 打开
     */
    public final void open(){}
    
    /**
     * 关闭DefaultHttpClient
     */
    @SuppressWarnings("deprecation")
	public final void closeAll() {
    }
    
    public final void setPageLoadTimeout(int timeout){
    }

	public BasicCookieStore getCookieStore() {
		return cookieStore;
	}
	
	public void setCookies(Cookies cookies) {
		cookieStore.clear();
		Iterator<Cookie> iter = cookies.iterator();
		while(iter.hasNext()){
			Cookie cookie = iter.next();
			cookieStore.addCookie(cookie.convertHttpClientCookie());
		}
	}
	
	private CloseableHttpClient createHttpClient(BasicCookieStore cookieStore) throws NoSuchAlgorithmException {
		PoolingHttpClientConnectionManager cm = null;
		SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
		try {
			sslContextBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
			SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build());
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
					.register("https", socketFactory).register("http", new PlainConnectionSocketFactory()).build();
			cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			cm.setMaxTotal(500);
			cm.setDefaultMaxPerRoute(80);
		} catch (Exception e) {
			e.printStackTrace();
		}
		final int MAX_TIMEOUT = 15 * 1000;
		RequestConfig.Builder configBuilder = RequestConfig.custom();
		// 设置连接超时
		configBuilder.setConnectTimeout(MAX_TIMEOUT);
		// 设置读取超时
		configBuilder.setSocketTimeout(MAX_TIMEOUT);
		// 设置从连接池获取连接实例的超时
		configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
		configBuilder.setCookieSpec(CookieSpecs.DEFAULT);
		RequestConfig requestConfig = configBuilder.build();
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig)
				.setDefaultCookieStore(cookieStore)
				.setDefaultCookieSpecRegistry(registry)
				.setConnectionManager(cm).build();
		return httpClient;
	}
	
}
