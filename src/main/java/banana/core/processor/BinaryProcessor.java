package banana.core.processor;

import java.io.InputStream;
import java.util.concurrent.BlockingQueue;

import banana.core.request.BinaryRequest;


/**
 * 文件请求处理
 */
public interface BinaryProcessor {
	/**
	 * 请求文件
	 * @param binaryRequest
	 * @param input  文件对应的字节流
	 * @param requestQueue 
	 */
	public void process(BinaryRequest binaryRequest,InputStream input, BlockingQueue<BinaryRequest> requestQueue);
}
