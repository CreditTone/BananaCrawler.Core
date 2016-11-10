package banana.core.request;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.alibaba.fastjson.JSON;

import banana.core.BytesWritable;


/**
 * 所有Request都直接或间接的继承自BasicRequest。BasicRequest是所有Request的父类
 * 基本的实现有
 * 1、priority优先级设置，在0-1000之间。值越大越先被请求
 * 2、requestCount记录Request总共被请求了多少次
 * 3、实现Comparable接口。可排序，和priority相关
 * 
 */
public abstract class BasicRequest extends BytesWritable implements Comparable<BasicRequest>{
	
	protected String uuid = UUID.randomUUID().toString();
	
	/**
     * 父节点的Request
     */
    protected BasicRequest parentRequest;
	
	private int priority = 0; 
	
	/**
     * 记录Request被发送的次数
     */
    private int requestCount = 0;
    
    /**
     * request属性
     */
    protected Map<String,Object> attributes = new HashMap<String, Object>();
    

    public int getPriority() {
		return 1000 - priority;
	}
    
	public void setPriority(int priority) {
		this.priority = 1000 - priority;
	}
	
    public void recodeRequest(){
    	requestCount++;
    }
    
    public int getHistoryCount(){
    	return requestCount;
    }
	
    public BasicRequest getParentRequest() {
		return parentRequest;
	}

	public void setParentRequest(BasicRequest parentRequest) {
		this.parentRequest = parentRequest;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public BasicRequest addAttribute(String attribute, Object value) {
    	attributes.put(attribute, value);
    	return this;
	}

	public Object getAttribute(String attribute) {
		Object value = attributes.get(attribute);
		if(value == null && parentRequest != null){
			value = parentRequest.getAttribute(attribute);
		}
    	return value;
	}
	
	public Map<String,Object> getAttributes(){
		return attributes;
	}

	public Set<String> enumAttributeNames() {
		return attributes.keySet();
	}
	
	/**
	 * request排序
	 * getPriority越小  优先级越高   但是对于上层调用无需关心  框架会做反转
	 */
	@Override
	public int compareTo(BasicRequest o) {
		if(this.getPriority() < o.getPriority()){
    		return 1;
    	}else if(this.getPriority() == o.getPriority()){
    		return 0;
    	}else{
    		return -1;
    	}
	}
	
	/**
	 * 当子url或者当前url完成的时候回调
	 * @param hashcode  实际Request的hashCode
	 */
	public void notify(String uuid) {
		if( parentRequest != null){
			parentRequest.notify(uuid);
		}
	}
	
	public void notifySelf() {
		if( parentRequest != null){
			parentRequest.notify(uuid);
		}
	}
	
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(priority);
		out.writeInt(requestCount);
		String attributesJson = JSON.toJSONString(attributes);
		byte[] body = attributesJson.getBytes("UTF-8");
		out.writeInt(body.length);
		out.write(body);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		priority = in.readInt();
		requestCount = in.readInt();
		int len = in.readInt();
		byte[] body = new byte[len];
		in.readFully(body);
		String attributesJson = new String(body,"UTF-8");
		attributes = JSON.parseObject(attributesJson, Map.class);
	}
	
}
