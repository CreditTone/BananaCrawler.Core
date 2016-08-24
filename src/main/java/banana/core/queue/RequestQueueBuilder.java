package banana.core.queue;

public final class RequestQueueBuilder {

	private int delay;
	
	private boolean suportPriority;
	
	public RequestQueueBuilder setDelayPeriod(int millisecond){
		delay = millisecond;
		return this;
	}
	
	public RequestQueueBuilder setSuportPriority(boolean suport){
		suportPriority = suport;
		return this;
	}
	
	public BlockingRequestQueue build(){
		BlockingRequestQueue queue = null;
		if (delay > 0){
			if (suportPriority){
				queue = new DelayedPriorityBlockingQueue(delay);
			}else{
				queue = new DelayedBlockingQueue(delay);
			}
		}else{
			if (suportPriority){
				queue = new RequestPriorityBlockingQueue();
			}else{
				queue = new SimpleBlockingQueue();
			}
		}
		return queue;
	}
}
