package banana.core.queue;

import java.util.concurrent.PriorityBlockingQueue;

import banana.core.request.HttpRequest;

/**
 * 优先级队列
 * @author Administrator
 *
 */
public final class RequestPriorityBlockingQueue extends
		PriorityBlockingQueue<HttpRequest> implements BlockingRequestQueue{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public RequestPriorityBlockingQueue(){
		super();
	}

	@Override
	public boolean remove(HttpRequest e) {
		return super.remove(e);
	}
	
}
