package banana.core.queue;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import banana.core.request.HttpRequest;


/**
 * 请求延迟队列，设置好延迟时间
 * @author Administrator
 *
 */
public class DelayedBlockingQueue implements BlockingRequestQueue,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final BlockingQueue<HttpRequest> queue;
	private final int delayInMilliseconds;
	private volatile long lastSuccesfullPop;

	/**
	 * 
	 * @param delayInMilliseconds
	 */
	public DelayedBlockingQueue(final int delayInMilliseconds) {
		this.delayInMilliseconds = delayInMilliseconds;
		queue = new LinkedBlockingQueue<HttpRequest>();
		lastSuccesfullPop = System.currentTimeMillis() - delayInMilliseconds;
	}

	public HttpRequest poll() {
		synchronized (queue) {
			while ((System.currentTimeMillis() - lastSuccesfullPop <= delayInMilliseconds) && !queue.isEmpty()) {
				sleep();
			}
			lastSuccesfullPop = System.currentTimeMillis();
			return queue.poll();
		}
	}

	public HttpRequest poll(final long timeout, final TimeUnit unit) throws InterruptedException {
		synchronized (queue) {
			while ((System.currentTimeMillis() - lastSuccesfullPop <= delayInMilliseconds) && !queue.isEmpty()) {
				sleep();
			}
			lastSuccesfullPop = System.currentTimeMillis();
			return queue.poll(timeout, unit);
		}
	}

	public HttpRequest take() throws InterruptedException {
		synchronized (queue) {
			while ((System.currentTimeMillis() - lastSuccesfullPop <= delayInMilliseconds) && !queue.isEmpty()) {
				sleep();
			}
			lastSuccesfullPop = System.currentTimeMillis();
			return queue.take();
		}
	}

	public HttpRequest remove() {
		return queue.remove();
	}

	private void sleep() {
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean add(HttpRequest e) {
		return queue.add(e);
	}

	@Override
	public HttpRequest peek() {
		return queue.peek();
	}

	@Override
	public HttpRequest element() {
		return queue.element();
	}

	@Override
	public boolean remove(HttpRequest e) {
		return queue.remove(e);
	}

	@Override
	public int size() {
		return queue.size();
	}

	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public void clear() {
		queue.clear();
	}

}
