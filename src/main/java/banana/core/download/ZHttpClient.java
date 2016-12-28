package banana.core.download;

import java.io.IOException;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;

import banana.core.download.pool.HttpClientFactory;


/**
 * 扩展的HttpClient。增加对cookie的控制
 *
 */
public final class ZHttpClient{
	
	private String id;
	
	private CloseableHttpClient core ;
	
	private BasicCookieStore cookieStore ;
	
	public ZHttpClient(){
		this.id = UUID.randomUUID().toString();
	}

	public void setCookieStore(BasicCookieStore cookieStore){
		if(cookieStore == null){
			throw new NullPointerException();
		}
		this.cookieStore = cookieStore;
		core = new HttpClientFactory().buildDefaultHttpClient(cookieStore);
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
			core = new HttpClientFactory().buildDefaultHttpClient(cookieStore);
		}
	}

	public final void close() throws IOException {
		core.close();
	}

	public final HttpResponse execute(HttpRequestBase method,HttpContext context) throws ClientProtocolException, IOException {
		checkInit();
		return context!=null?core.execute(method,context):core.execute(method);
	}

	public String getId() {
		return id;
	}
	
}
