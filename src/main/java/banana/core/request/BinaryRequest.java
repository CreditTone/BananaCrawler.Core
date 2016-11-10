package banana.core.request;

/**
 * 文件下载类型Request的表示
 *
 */
public final class BinaryRequest extends HttpRequest {

	public BinaryRequest(){
	}
	
	public BinaryRequest(String url, String binaryProccessor){
		this.processor = binaryProccessor;
		this.setUrl(url);
	}
	
}
