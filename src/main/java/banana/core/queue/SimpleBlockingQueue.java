package banana.core.queue;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.LinkedBlockingQueue;


import banana.core.request.HttpRequest;
import banana.core.request.PageRequest;
import banana.core.util.SystemUtil;

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
			HttpRequest req = poll();
			byte[] data = null;
			while(req != null){
				data = req.toBytes();
				out.write(SystemUtil.intToBytes(data.length));
				out.write(data);
				req = poll();
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
	
}
