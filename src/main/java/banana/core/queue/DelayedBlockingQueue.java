package banana.core.queue;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import banana.core.request.HttpRequest;
import banana.core.request.PageRequest;
import banana.core.util.SystemUtil;


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
