package banana.core.queue;

import java.util.concurrent.PriorityBlockingQueue;

import banana.core.request.BasicRequest;

/**
 * 优先级队列
 * @author Administrator
 *
 */
public final class RequestPriorityBlockingQueue extends
		PriorityBlockingQueue<BasicRequest> implements BlockingRequestQueue{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public RequestPriorityBlockingQueue(){
		super();
	}

	@Override
	public boolean remove(BasicRequest e) {
		return super.remove(e);
	}
	
}
