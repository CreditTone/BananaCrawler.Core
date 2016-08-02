package banana.core.download.impl;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import banana.core.download.pool.HttpClientFactory;


/**
 * 扩展的HttpClient。增加对cookie的控制
 *
 */
public final class ZHttpClient{
	
	/**
	 * 实例池中的编号
	 */
	private int index;
	
	private CloseableHttpClient core ;
	
	private BasicCookieStore cookieStore ;
	
	private HttpClientFactory httpClientFactory;
	
	public ZHttpClient(HttpClientFactory httpClientFactory){
		this.httpClientFactory = httpClientFactory;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setCookieStore(BasicCookieStore cookieStore){
		if(cookieStore == null){
			throw new NullPointerException();
		}
		this.cookieStore = cookieStore;
		core = httpClientFactory.buildDefaultHttpClient(cookieStore);
		
	}
	
	
	
	public CloseableHttpClient getCore() {
		return core;
	}

	public BasicCookieStore getCookieStore() {
		return cookieStore;
	}

	private final void checkInit(){
		if(core == null){
			this.cookieStore = new BasicCookieStore();
			core = httpClientFactory.buildDefaultHttpClient(cookieStore);
		}
	}

	public final void close() throws IOException {
		core.close();
	}

	public final HttpResponse execute(HttpRequestBase method,HttpContext context) throws ClientProtocolException, IOException {
		checkInit();
		return context!=null?core.execute(method,context):core.execute(method);
	}
	
//	public void setProxy(){
//		core.execute(request, context)
//		new BasicHttpContext();
//		HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider)
//	}
	
}
