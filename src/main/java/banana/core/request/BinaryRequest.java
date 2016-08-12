package banana.core.request;


import banana.core.processor.BinaryProcessor;


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
				+ ", requestParams=" + requestParams + ", headers=" + headers + ", attributes=" + attributes + ", uuid="
				+ uuid + ", type=" + type + "]";
	}
}
