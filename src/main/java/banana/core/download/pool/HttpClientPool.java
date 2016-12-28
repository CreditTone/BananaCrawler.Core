package banana.core.download.pool;

import java.util.Iterator;

import org.apache.http.impl.client.BasicCookieStore;

import banana.core.download.ZHttpClient;
import banana.core.request.Cookie;
import banana.core.request.Cookies;


public final class HttpClientPool extends DriverPoolInterface<ZHttpClient>{
	

	private Cookies cookies;

    public HttpClientPool(Cookies initCookie) {
    	cookies = initCookie;
    }

    /**
     * 创建核心实例
     */
	public final ZHttpClient createDriver(){
		ZHttpClient poll;
		poll = new ZHttpClient();
		if (cookies != null){
			BasicCookieStore cookieStore = new BasicCookieStore();
			Iterator<Cookie> iter = cookies.iterator();
			while(iter.hasNext()){
				Cookie cookie = iter.next();
				cookieStore.addCookie(cookie.convertHttpClientCookie());
				System.out.println("设置Cookie "+cookie);
			}
			poll.setCookieStore(cookieStore);
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
    
}
