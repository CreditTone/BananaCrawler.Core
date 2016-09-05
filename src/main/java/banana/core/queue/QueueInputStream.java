package banana.core.queue;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;

import banana.core.BytesWritable;
import banana.core.request.HttpRequest;

public class QueueInputStream extends InputStream {
	
	private BlockingRequestQueue queue ;
	
	private BlockingQueue<HttpRequest> queue2 ;
	
	private byte[] dataLength = null;
	
	private byte lengthPosition = 0;
	
	private byte[] cache = new byte[0];
	
	private int readPosition = 0;
	
	private int size = 0;

	public QueueInputStream(BlockingRequestQueue queue) {
		this.queue = queue;
	}
	
	public QueueInputStream(BlockingQueue<HttpRequest> queue){
		this.queue2 = queue;
	}
	
	private final BytesWritable poll(){
		if (queue != null){
			return queue.poll();
		}
		return queue2.poll();
	}
	
	private final boolean initCache(){
		BytesWritable writable = poll();
		if (writable == null){
			return false;
		}
		cache = writable.toBytes();
		readPosition = 0;
		dataLength = new byte[4];
		int x = cache.length;
		if (x == 0)
			throw new RuntimeException("");
		System.out.println("write len = " + x);
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
				System.out.println("总共写入:" + size);
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

    public int read(byte b[], int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }
        int c = read();
        if (c == -1) {
            return -1;
        }
        b[off] = (byte)c;

        int i = 1;
        try {
            for (; i < len ; i++) {
                c = read();
                if (c == -1) {
                    break;
                }
                b[off + i] = (byte)c;
            }
        } catch (IOException ee) {
        }
        size += i;
        return i;
    }
	
}
