package banana.core.request;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import banana.core.BytesWritable;
import banana.core.processor.TransactionCallBack;
import banana.core.request.HttpRequest.Method;
import banana.core.request.PageRequest.PageEncoding;

/**
 *  StartContext是注入时所有seed的上下文信息如果爬虫在抓取过程当中需要共享一些变量。那么可使用StartContext作为容器。
 *
 */
public final class StartContext extends BytesWritable{
	
	/**
	 * 全局属性
	 */
	private final HashMap<String, Object> contextAttribute = new HashMap<String, Object>();
	/**
	 * 定义根url
	 */
	private List<HttpRequest> seeds = new ArrayList<HttpRequest>();
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 构造一个StartContext。通常用来充当seedRequest的容器
	 */
	public StartContext(){}
	
	/**
	 * 构造一个StartContext。并且加入一个种子URL
	 * @param url
	 * @param processorCls
	 */
	public StartContext(String url,String processor) {
		this(url, processor, null);
	}
	
	
	/**
	 * 构造一个StartContext。并且加入一个种子URL
	 * @param url 
	 * @param processorCls 
	 * @param pageEncoding  URL对应网页的编码
	 */
	public StartContext(String url,String processor,PageEncoding pageEncoding) {
		HttpRequest seed = createPageRequest(url, processor, 0 ,pageEncoding);
		seeds.add(seed);
	}
	
	
	/**
	 * 创建网页下载请求PageRequest
	 * @param url  这个请求对应的http或者https 地址
	 * @param processorCls 下载完成后处理这个网页Page的PageProcessor的class对象
	 * @return PageRequest
	 */
    public static PageRequest createPageRequest(String url,String processor){
    	PageRequest req = new PageRequest();
    	req.setUrl(url);
    	req.setMethod(Method.GET);
    	req.setProcessor(processor);
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
    

	/**
	 * 注入种子
	 * @param request
	 */
	public void injectSeed(HttpRequest request){
		this.seeds.add(request);
	}
	
	/**
	 * 返回该StartContext所包含的所有种子URL
	 * @return
	 */
	public List<HttpRequest> getSeedRequests(){
		return this.seeds;
	}
	
	/**
	 * 返回该StartContext所包含的所有种子URL
	 * @return
	 */
	public List<HttpRequest> getSeedRequestsAndClear(){
		List<HttpRequest> s = this.seeds;
		this.seeds = new ArrayList<>();
		return s;
	}

	/**
	 * 返回attribute对应的value 这个方法是线程安全的
	 * @param key
	 * @return  返回attribute对应的value
	 */
	public  Object getContextAttribute(String attribute){
		Object value;
		synchronized (contextAttribute) {
			value = contextAttribute.get(attribute);
		}
		return value;
	}
	
	/**
	 * 向StartContext域put一个属性值。并返回之前的attribute对应的value。如果之前没有attribute属性那么返回null。这个方法是线程安全的
	 * @param attribute
	 * @param value
	 * @return 返回之前的attribute对应的value。如果之前没有attribute属性那么返回null
	 */
	public Object putContextAttribute(String attribute, Object value) {
		synchronized (contextAttribute) {
			contextAttribute.put(attribute, value);
		}
		return value;
	}
	
	/**
	 * 返回种子URL的个数
	 * @return
	 */
	public int getSeedSize(){
		return seeds.size();
	}
	
	/**
	 * 返回StartContext是否为空。
	 * @return
	 */
	public boolean isEmpty(){
		return seeds.isEmpty();
	}
	

	@Override
	public void write(DataOutput out) throws IOException {
		String contextAttributeJson = JSON.toJSONString(contextAttribute);
		out.writeUTF(contextAttributeJson);
//		byte[][] seedBytes = new byte[seeds.size()][];
//		for (int x = 0; x < seeds.size() ; x ++) {
//			HttpRequest seed = seeds.get(x);
//			seedBytes[x] = seed.toBytes();
//		}
//		JSONArray cutpoint = new JSONArray();
//		for (int i = 0; i < seedBytes.length; i++) {
//			cutpoint.add(seedBytes[i].length);
//		}
//		out.writeUTF(cutpoint.toJSONString());
//		for (int i = 0; i < seedBytes.length; i++) {
//			out.write(seedBytes[i]);
//		}
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		String contextAttributeJson = in.readUTF();
		HashMap<String, Object> attribute = JSON.parseObject(contextAttributeJson, HashMap.class);
		contextAttribute.putAll(attribute);
//		JSONArray cutpoint = JSON.parseArray(in.readUTF());
//		PageRequest seed = null;
//		for (int i = 0; i < cutpoint.size(); i++) {
//			int size = cutpoint.getIntValue(i);
//			byte[] requestBody = new byte[size];
//			in.readFully(requestBody);
//			seed = createPageRequest("", "");
//			seed.load(requestBody);
//			seeds.add(seed);
//		}
	}
	
}
