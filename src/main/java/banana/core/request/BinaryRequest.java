package banana.core.request;


import banana.core.processor.BinaryProcessor;


/**
 * 文件下载类型Request的表示
 *
 */
public final class BinaryRequest extends HttpRequest {

	private Class<? extends BinaryProcessor> binaryProccessor;
	
	
	public BinaryRequest(){
		this.type = Type.BINARY_REQUEST;
	}
	
	public BinaryRequest(String url, Class<? extends BinaryProcessor> binaryProccessor){
		this();
		this.binaryProccessor = binaryProccessor;
		this.setUrl(url);
	}
	

	public Class<? extends BinaryProcessor> getBinaryProccessor() {
		return binaryProccessor;
	}
	
	

	public void setBinaryProccessor(Class<? extends BinaryProcessor> binaryProccessor) {
		this.binaryProccessor = binaryProccessor;
	}

	@Override
	public String toString() {
		return "BinaryRequest [binaryProccessor=" + binaryProccessor + ", method=" + method + ", url=" + url
				+ ", requestParams=" + requestParams + ", headers=" + headers + ", attributes=" + attributes + ", uuid="
				+ uuid + ", type=" + type + "]";
	}
}
