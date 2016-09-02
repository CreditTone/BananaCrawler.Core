package banana.core.queue;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import banana.core.request.HttpRequest;
import banana.core.request.PageRequest;


/**
 * 延迟优先级队列
 * @author Administrator
 *
 */
public final class DelayedPriorityBlockingQueue implements BlockingRequestQueue ,Serializable{
	
	private final BlockingRequestQueue queue;
	private final int delayInMilliseconds;
	private volatile long lastSuccesfullPop;
	
	public DelayedPriorityBlockingQueue(final int delayInMilliseconds) {
		this(delayInMilliseconds,null);
	}
	public DelayedPriorityBlockingQueue(final int delayInMilliseconds,BlockingRequestQueue queue) {
		this.delayInMilliseconds = delayInMilliseconds;
		if (queue == null){
			this.queue = new RequestPriorityBlockingQueue();	
		}else{
			this.queue = queue;
		}
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

	// Delegate Methods. Java is just soooo fun sometimes...

	public boolean add(final HttpRequest e) {
		return queue.add(e);
	}

	public void clear() {
		queue.clear();
	}


	public HttpRequest element() {
		return queue.element();
	}

	@Override
	public boolean equals(final Object o) {
		return queue.equals(o);
	}

	@Override
	public int hashCode() {
		return queue.hashCode();
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	public HttpRequest peek() {
		return queue.peek();
	}

	public int size() {
		return queue.size();
	}

	@Override
	public boolean remove(HttpRequest e) {
		return queue.remove(e);
	}

	@Override
	public InputStream getStream() {
		return new QueueInputStream(this);
	}
	
	@Override
	public void load(InputStream input) {
		DataInputStream dataInput = new DataInputStream(input);
		try{
			int dataLength = dataInput.readInt();
			byte[] packet = new byte[dataLength];
			PageRequest req = null;
			while(true){
				dataInput.read(packet, 0, dataLength);
				req = new PageRequest();
				req.load(packet);
				add(req);
				dataLength = dataInput.readInt();
				packet = new byte[dataLength];
			}
		}catch(EOFException e){
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (dataInput != null){
				try {
					dataInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (input != null){
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
