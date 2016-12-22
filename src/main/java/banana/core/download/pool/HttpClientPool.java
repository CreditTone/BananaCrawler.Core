package banana.core.download.pool;

import banana.core.download.ZHttpClient;


public final class HttpClientPool extends DriverPoolInterface<ZHttpClient>{
	
    private HttpClientFactory httpClientFactory = new HttpClientFactory();


    public HttpClientPool() {}

    /**
     * 创建核心实例
     */
	public final ZHttpClient createDriver(){
		ZHttpClient poll;
		poll = new ZHttpClient(httpClientFactory);
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
