package banana.core.download.pool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import banana.core.download.impl.ZHttpClient;


public final class HttpClientPool extends DriverPoolInterface{
	
    /**
     * 是否是登录模式
     */
    private boolean loginMode = false;
    
    private HttpClientFactory httpClientFactory = new HttpClientFactory();

    /**
     * 统计用过的webDriverList。好释放
     *   
     * */
    private List<ZHttpClient> httpClientList = Collections.synchronizedList(new ArrayList<ZHttpClient>());
    /**
     * store webDrivers available
     */
    private LinkedBlockingQueue<ZHttpClient> queue = new LinkedBlockingQueue<ZHttpClient>();
    

    public HttpClientPool() {
    }


    /**
     * 从池中取得一个DefaultHttpClient
     * @return
     * @throws InterruptedException
     */
    public final ZHttpClient get() throws InterruptedException {
    	if(loginMode){
    		return httpClientList.get(0);
    	}
    	ZHttpClient poll = null;
    	if(httpClientList.size() < min_drivers){
    		synchronized (httpClientList) {
    			if(httpClientList.size() < min_drivers){
    				createSimpleHttpClient();
    			}
    		}
    	}
    	poll = queue.poll();
        if (poll != null && !getIndexs.contains(poll.getIndex())) {
            return poll;
        }
        if (httpClientList.size() < max_drivers) {//如果webDriver使用的数量美誉达到capacity则继续创建webDriver
            synchronized (httpClientList) {
                if (httpClientList.size() < max_drivers) {
                	createSimpleHttpClient();
                }
            }
        }
        return queue.take();//此方法并不保证立即返回WebDriver，有可能等待之前的WebDriver执行完回到pool
    }

    /**
     * 创建核心实例
     */
	private final void createSimpleHttpClient(){
		if(loginMode){
			httpClientFactory.CREATE_MODE = HttpClientFactory.USE_HTTPS_HTTPCLIENT;
		}
		ZHttpClient poll;
		int driverIndex = httpClientList.size() ;
		poll = new ZHttpClient(httpClientFactory);
		poll.setIndex(driverIndex);
		queue.add(poll);
		httpClientList.add(poll);
	}

    public final void returnToPool(ZHttpClient httpClient) {//将HttpClient添加到pool   	
    	if(!getIndexs.contains(httpClient.getIndex())){//被取得出去的driver不能回到队列   		 
    		queue.add(httpClient);
    	}
    }
    
    /**
     * 打开
     */
    public final void open(){
    }
    
    /**
     * 关闭DefaultHttpClient
     */
    @SuppressWarnings("deprecation")
	public final void closeAll() {
    }
    
    public final void setPageLoadTimeout(int timeout){
    }
    
	@Override
	public Object getDriver(int driverIndex) {
		if(getIndexs.contains(driverIndex)){
			return null;
		}else{
			getIndexs.add(driverIndex);
		}
		for (ZHttpClient client : httpClientList) {
			if(client.getIndex() == driverIndex){
				queue.remove(client);//队列移除实例。防止处理未完成之前使用
				return client;
			}
		}
		return null;
	}

	@Override
	public void handleComplete(Object driver) {
		ZHttpClient httpClient = (ZHttpClient) driver;
		getIndexs.remove(httpClient.getIndex());//清除限制
    	queue.add(httpClient);//回到队列
	}
	
}
