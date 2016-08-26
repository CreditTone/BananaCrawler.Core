package banana.core.request;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 文件下载类型Request的表示
 *
 */
public final class BinaryRequest extends HttpRequest {

	public BinaryRequest(){
		this.type = Type.BINARY_REQUEST;
	}
	
	public BinaryRequest(String url, String binaryProccessor){
		this();
		this.processor = binaryProccessor;
		this.setUrl(url);
	}
	
}
