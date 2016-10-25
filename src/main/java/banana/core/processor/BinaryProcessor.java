package banana.core.processor;

import banana.core.response.StreamResponse;


/**
 * 文件请求处理
 */
public interface BinaryProcessor {
	
	public void process(StreamResponse stream);
	
}
