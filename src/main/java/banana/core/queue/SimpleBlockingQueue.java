package banana.core.queue;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import banana.core.request.BasicRequest;
import banana.core.request.HttpRequest;

/**
 * SimpleBlockingQueue采用先进先出的FIFO原则。广度优先策略合适的队列
 *
 */
public class SimpleBlockingQueue extends LinkedBlockingQueue<HttpRequest> implements BlockingRequestQueue {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean remove(HttpRequest e) {
		return super.remove(e);
	}
	
}
