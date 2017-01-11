package banana.core.modle;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;

import banana.core.BytesWritable;
import banana.core.request.HttpRequest;
import banana.core.request.RequestBuilder;
import banana.core.request.PageRequest.PageEncoding;

/**
 *  TaskContext是注入时所有seed的上下文信息如果爬虫在抓取过程当中需要共享一些变量。那么可使用StartContext作为容器。
 *
 */
public final class TaskContextImpl extends BytesWritable implements TaskContext{
	
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
	public TaskContextImpl(){}
	
	/**
	 * 构造一个StartContext。并且加入一个种子URL
	 * @param url
	 * @param processorCls
	 */
	public TaskContextImpl(String url,String processor) {
		this(url, processor, null);
	}
	
	
	/**
	 * 构造一个StartContext。并且加入一个种子URL
	 * @param url 
	 * @param processorCls 
	 * @param pageEncoding  URL对应网页的编码
	 */
	public TaskContextImpl(String url,String processor,PageEncoding pageEncoding) {
		HttpRequest seed = RequestBuilder.custom().setUrl(url).setProcessor(processor).setPriority(0).setPageEncoding(pageEncoding).build();
		seeds.add(seed);
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

	/* (non-Javadoc)
	 * @see banana.core.modle.TaskContext#getContextAttribute(java.lang.String)
	 */
	@Override
	public  Object getContextAttribute(String attribute){
		Object value;
		synchronized (contextAttribute) {
			value = contextAttribute.get(attribute);
		}
		return value;
	}
	
	/* (non-Javadoc)
	 * @see banana.core.modle.TaskContext#putContextAttribute(java.lang.String, java.lang.Object)
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see banana.core.modle.TaskContext#isEmpty()
	 */
	@Override
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
