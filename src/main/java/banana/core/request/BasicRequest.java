package banana.core.request;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


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
	 * 请求类型
	 * @author Administrator
	 *
	 */
	public enum Type{
    	PAGE_REQUEST,
    	TRANSACTION_REQUEST,
    	BINARY_REQUEST;
    }
	
	protected Type type;
	
	/**
     * 父节点的Request
     */
    protected BasicRequest parentRequest;
	
	private int priority = 0; 
	
	/**
     * 记录Request被发送的次数
     */
    private int requestCount = 0;
    

	public Type getType() {
		return type;
	}
	 
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
	
	/**
	 * 设置属性
	 * @param attribute
	 * @param value
	 * @return  返回BasicRequest对象自身
	 */
	public abstract  BasicRequest addAttribute(String attribute,Object value);
	
	/**
	 * 取得属性
	 * @param attribute
	 * @return 返回attribute属性对应的value。没有则返回null
	 */
	public abstract Object getAttribute(String attribute);
	
	/**
	 * 枚举所有的属性名
	 * @param attribute
	 * @return
	 */
	public abstract Set<String> enumAttributeNames();
	
	public abstract Map<String,Object> getAttributes();

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(type.name());
		out.writeInt(priority);
		out.writeInt(requestCount);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		String typeName = in.readUTF();
		if (typeName.equals(Type.PAGE_REQUEST.name())){
			type = Type.PAGE_REQUEST;
		}else if(typeName.equals(Type.BINARY_REQUEST.name())){
			type = Type.BINARY_REQUEST;
		}else if(typeName.equals(Type.TRANSACTION_REQUEST.name())){
			type = Type.TRANSACTION_REQUEST;
		}
		priority = in.readInt();
		requestCount = in.readInt();
	}
	
}
