package banana.core.queue;

import java.io.IOException;
import java.io.InputStream;

import banana.core.BytesWritable;

public class QueueInputStream extends InputStream {
	
	private final BlockingRequestQueue queue ;
	
	private byte[] dataLength = null;
	
	private byte lengthPosition = 0;
	
	private byte[] cache = new byte[0];
	
	private int readPosition = 0;

	public QueueInputStream(BlockingRequestQueue queue) {
		this.queue = queue;
	}
	
	private final boolean initCache(){
		BytesWritable writable = queue.poll();
		if (writable == null){
			return false;
		}
		cache = writable.toBytes();
		readPosition = 0;
		dataLength = new byte[4];
		int x = cache.length;
		dataLength[0] = (byte)((x >> 24) & 0xFF);
		dataLength[1] = (byte)((x >> 16) & 0xFF);
		dataLength[2] = (byte)((x >> 8) & 0xFF); 
		dataLength[3] = (byte)(x & 0xFF);
		lengthPosition = 0;
		return true;
	}

	@Override
	public int read() throws IOException {
		if (readPosition == cache.length){
			if (!initCache()){
				return -1;
			}
		}
		if (lengthPosition < dataLength.length){
			int result = dataLength[lengthPosition];
			lengthPosition ++;
			return result;
		}
		int result = cache[readPosition];
		readPosition ++;
		return result;
	}

}
