package banana.core.queue;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.PriorityBlockingQueue;

import banana.core.request.HttpRequest;
import banana.core.request.PageRequest;

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
