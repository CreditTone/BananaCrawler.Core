package banana.core.queue;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Iterator;

import banana.core.request.HttpRequest;
import banana.core.request.PageRequest;
import banana.core.util.SystemUtil;


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
	public void load(InputStream input) {
		DataInputStream dataInput = new DataInputStream(input);
		try{
			int dataLength = dataInput.readInt();
			byte[] packet = new byte[dataLength];
			PageRequest req = null;
			while(true){
				dataInput.read(packet);
				req = new PageRequest();
				req.load(packet);
				add(req);
				dataLength = dataInput.readInt();
				System.out.println("datalength="+dataLength);
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
	
	@Override
	public byte[] toBytes() {
		byte[] qdata = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try{
			HttpRequest req = queue.poll();
			byte[] data = null;
			while(req != null){
				data = req.toBytes();
				out.write(SystemUtil.intToBytes(data.length));
				out.write(data);
				req = queue.poll();
			}
			qdata = out.toByteArray();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (out != null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return qdata;
	}
	@Override
	public Iterator<HttpRequest> iterator() {
		return queue.iterator();
	}
}
