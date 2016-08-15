package banana.core.request;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 文件下载类型Request的表示
 *
 */
public final class BinaryRequest extends HttpRequest {

	private String binaryProccessor;
	
	
	public BinaryRequest(){
		this.type = Type.BINARY_REQUEST;
	}
	
	public BinaryRequest(String url, String binaryProccessor){
		this();
		this.binaryProccessor = binaryProccessor;
		this.setUrl(url);
	}
	

	public String getBinaryProccessor() {
		return binaryProccessor;
	}
	
	

	public void setBinaryProccessor(String binaryProccessor) {
		this.binaryProccessor = binaryProccessor;
	}

	@Override
	public String toString() {
		return "BinaryRequest [binaryProccessor=" + binaryProccessor + ", method=" + method + ", url=" + url
				+ ", requestParams=" + requestParams + ", headers=" + headers + ", attributes=" + attributes + ", type=" + type + "]";
	}

	@Override
	public void write(DataOutput out) throws IOException {
		super.write(out);
		out.writeUTF(binaryProccessor);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		super.readFields(in);
		binaryProccessor = in.readUTF();
	}
}
