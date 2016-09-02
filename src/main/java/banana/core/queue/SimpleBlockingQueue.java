package banana.core.queue;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.LinkedBlockingQueue;


import banana.core.request.HttpRequest;
import banana.core.request.HttpRequest.Method;
import banana.core.request.PageRequest;
import banana.core.request.StartContext;

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
