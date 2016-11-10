package banana.core.request;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import banana.core.processor.TransactionCallBack;

/**
 * 为了实现和维护并发抓取的属性信息提供线程安全的事务请求。TransactionRequest是一个抽象类自己不能设置Processor，却需要实现
 * TransactionCallBack接口。TransactionRequest是个复合的HttpRequest。他可以将多个PageRequest、BinaryRequest甚至TransactionRequest
 * 自己的对象添加到child集合中，在下载过程中首先下载TransactionRequest中的所有childRequest，每个childRequest下载完成后使用notify方式逐步向上通知，
 * 直到所有的child下载完成TransactionRequest回调 TransactionCallBack的callBack方法通知业务层这个TransactionRequest下载完成。
 * 
 */
public class TransactionRequest extends BasicRequest implements TransactionCallBack{
	
	/**
	 * 事务处理可以含有多个Request
	 */
	private List<BasicRequest> childRequest = new ArrayList<BasicRequest>();
	
	/**
	 * 标记所有的PageRequest是否都已完成请求和处理
	 */
	private ConcurrentHashMap<String, Boolean> successFlag = new ConcurrentHashMap<String, Boolean>();
	
	public TransactionRequest(){
	}



	/**
	 * 添加一个HttpRequest到TransactionRequest的child中
	 * @param request
	 */
	public void addChildRequest(BasicRequest request){
		if(request.getUuid().equals(this.getUuid())){
			return;
		}
		request.setParentRequest(this);
		successFlag.put(request.getUuid(), false);
		childRequest.add(request);
	}
	
	public void addChildRequest(List<HttpRequest> childRequest) {
		if (childRequest != null){
			for (HttpRequest request : childRequest) {
				addChildRequest(request);
			}
		}
	}

	
	/**
	 * 返回这个TransactionRequest所有child的迭代器
	 * @return
	 */
	public Iterator<BasicRequest> iteratorChildRequest(){
		return childRequest.iterator();
	}

	@Override
	public void notify(String uuid) {
		if(successFlag.containsKey(uuid)){
			successFlag.put(uuid, true);
			checkComplete();
		}else{
			throw new RuntimeException("not found uuid :"+uuid);
		}
		super.notify(this.uuid);
	}
	
	/**
	 * 检查是否所有的Request标记都不是false。如果是那么所有的Request已经请求完成和处理。
	 * 
	 */
	public void checkComplete(){
		if(!successFlag.containsValue(false)){
			try {
				callBack(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public List<BasicRequest> getChildRequest() {
		return childRequest;
	}



	@Override
	public void callBack(TransactionRequest transactionRequest) throws Exception {
	}

}
