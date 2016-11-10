package banana.core.request;

import banana.core.request.HttpRequest.Method;
import banana.core.request.PageRequest.PageEncoding;

public final class RequestBuilder {
	
	private byte[] requestBody;
	private String url;
	private String download;
	private Method method = Method.GET;
	private String processor;
	private PageEncoding pageEncoding;
	private int priority;
	
	public static RequestBuilder custom() {
        return new RequestBuilder();
    }

	/**
	 * 创建网页下载请求PageRequest
	 * @param url  这个请求对应的http或者https 地址
	 * @param processorCls 下载完成后处理这个网页Page的PageProcessor的class对象
	 * @return PageRequest
	 */
    public RequestBuilder setUrl(String url) {
    	this.url = url;
    	return this;
    }
    
    public RequestBuilder setDownload(String download) {
    	this.download = download;
    	return this;
    }
    
    public RequestBuilder setProcessor(String processor) {
    	this.processor = processor;
    	return this;
    }
    
    public RequestBuilder setMethod(Method method) {
    	this.method = method;
    	return this;
    }
    
    public RequestBuilder setPriority(int priority) {
    	this.priority = priority;
    	return this;
    }
    
    public RequestBuilder setPageEncoding(PageEncoding pageEncoding) {
    	this.pageEncoding = pageEncoding;
    	return this;
    }
    
    public RequestBuilder setRequestBody(byte[] requestBody) {
    	this.requestBody = requestBody;
    	return this;
    }
    
    public HttpRequest build(){
    	HttpRequest ret = null;
    	if (requestBody != null){
    		PageRequest req = new PageRequest();
        	req.load(requestBody);
    	}else{
    		if (url != null){
    			ret = new PageRequest(url,processor);
    			if (pageEncoding != null){
        			((PageRequest)ret).setPageEncoding(pageEncoding);
        		}
    		}else{
    			ret = new BinaryRequest(download, processor);
    		}
    		ret.setMethod(method);
    		if(priority >=0 && priority<=1000){
    			ret.setPriority(priority);
    		}else{
    			throw new IllegalArgumentException("priority的值必须在0-1000之间");
    		}
    	}
    	return ret;
    }
    
}
