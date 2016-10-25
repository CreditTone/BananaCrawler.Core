package banana.core.request;

import banana.core.processor.TransactionCallBack;
import banana.core.request.HttpRequest.Method;
import banana.core.request.PageRequest.PageEncoding;

public final class RequestBuilder {

	/**
	 * 创建网页下载请求PageRequest
	 * @param url  这个请求对应的http或者https 地址
	 * @param processorCls 下载完成后处理这个网页Page的PageProcessor的class对象
	 * @return PageRequest
	 */
    public static PageRequest createPageRequest(String url,String processor){
    	PageRequest req = new PageRequest();
    	req.setUrl(url.replaceAll(" ", "%20"));
    	req.setMethod(Method.GET);
    	req.setProcessor(processor);
    	req.setPriority(1);
    	return req;
    }
    
    /**
     * 创建网页下载请求PageRequest
     * @param url  这个请求对应的http或者https 地址
     * @param processorCls  下载完成后处理这个网页Page的PageProcessor的class对象
     * @param priority   设置这个PageRequest的priority。需要注意的只有使用crawTaskBuilder.useQueuePriorityRequest或者crawTaskBuilder.useQueueDelayedPriorityRequest的时候
     * priority才会起作用并排序。
     * @param pageEncoding  这个PageRequest对应URL的网页编码格式。如果不指定那么会用crawTaskBuilder中指定的usePageEncoding。如果crawTaskBuilder没有
     * 使用usePageEncoding。则默认用UTF-8编码
     * @return PageRequest
     */
    public static PageRequest createPageRequest(String url,String processor,int priority,PageEncoding pageEncoding){
    	if(priority >=0 && priority<=1000){
    		PageRequest req = new PageRequest();
        	req.setUrl(url);
        	req.setMethod(Method.GET);
        	req.setPriority(priority) ;
        	req.setProcessor(processor);
        	req.setPageEncoding(pageEncoding);
        	return req;
    	}else{
    		throw new IllegalArgumentException("priority的值必须在0-1000之间");
    	}
    }
    
    
    public static PageRequest createPageRequest(byte[] body){
    	PageRequest req = new PageRequest();
    	req.load(body);
    	return req;
    }
    
    /**
     * 创建网页下载请求PageRequest
     * @param url  这个请求对应的http或者https 地址
     * @param processorCls  下载完成后处理这个网页Page的PageProcessor的class对象
     * @param priority  设置这个PageRequest的priority。需要注意的只有使用crawTaskBuilder.useQueuePriorityRequest或者crawTaskBuilder.useQueueDelayedPriorityRequest的时候
     * priority才会起作用并排序。
     * @return PageRequest
     */
    public  static PageRequest createPageRequest(String url,String processor,int priority){
    	if(priority >=0 && priority<=1000){
    		PageRequest req = new PageRequest();
    		req.setUrl(url);
    		req.setMethod(Method.GET);
    		req.setPriority(priority) ;
    		req.setProcessor(processor);
    		return req;
    	}else{
    		throw new IllegalArgumentException("priority的值必须在0-1000之间");
    	}
    }
    
    /**
     * 创建一个二进制下载请求
     * @param url 这个请求对应的http或者https 地址
     * @param processorCls  文件下载时处理这个InputStream的BinaryProcessor的class对象
     * @return BinaryRequest
     */
    public static BinaryRequest createBinaryRequest(String url,String processor){
    	BinaryRequest req = new BinaryRequest(url, processor);
    	return req;
    }
    
    /**
     * 创建支持事务的下载请求。
     * @param transactionCallBack   事务完成后的回调接口的Class
     * @return TransactionRequest
     */
    public static TransactionRequest createTransactionRequest(final TransactionCallBack transactionCallBack){
    	TransactionRequest req = new TransactionRequest() {
			
			@Override
			public void callBack(TransactionRequest transactionRequest) throws Exception {
				if(transactionCallBack != null){
					transactionCallBack.callBack(transactionRequest);
				}
			}
		};
    	return req;
    }
    
    /**
     *  给定一个child集合创建支持事务的下载请求。
     * @param transactionCallBack 事务完成后的回调接口
     * @param child child集合
     * @return TransactionRequest
     */
    public static TransactionRequest createTransactionRequest(final TransactionCallBack transactionCallBack,BasicRequest ... child){
    	TransactionRequest req = new TransactionRequest() {
    		
    		@Override
    		public void callBack(TransactionRequest transactionRequest) throws Exception {
    			transactionCallBack.callBack(transactionRequest);
    		}
    	};
    	if(child != null && child.length != 0){
    		for (int i = 0; i < child.length; i++) {
    			req.addChildRequest(child[i]);
			}
    	}
    	return req;
    }
    
}
