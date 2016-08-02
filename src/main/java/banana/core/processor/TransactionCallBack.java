package banana.core.processor;

import banana.core.request.TransactionRequest;

/**
 * TransactionRequest的实现接口
 *
 */
public interface TransactionCallBack {
	
	/**
	 * 处理一个事务完成的所有结果
	 * @param page
	 * @param context
	 * @param queue  
	 * @throws Exception
	 */
	void callBack(TransactionRequest transactionRequest)throws Exception;
	
}
