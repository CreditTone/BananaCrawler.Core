package banana.core.download.pool;

import banana.core.download.impl.ZHttpClient;


public final class HttpClientPool extends DriverPoolInterface<ZHttpClient>{
	
    private HttpClientFactory httpClientFactory = new HttpClientFactory();


    public HttpClientPool() {}

    /**
     * 创建核心实例
     */
	public final ZHttpClient createDriver(){
		ZHttpClient poll;
		int driverIndex = driverList.size();
		poll = new ZHttpClient(httpClientFactory);
		poll.setIndex(driverIndex);
		return poll;
	}

    public final void returnToPool(ZHttpClient httpClient) {//将HttpClient添加到pool   	
    	queue.add(httpClient);
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
