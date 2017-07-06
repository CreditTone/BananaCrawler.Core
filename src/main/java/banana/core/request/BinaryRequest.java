package banana.core.request;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 文件下载类型Request的表示
 *
 */
public final class BinaryRequest extends HttpRequest {
	
	private String downloadPath;

	public BinaryRequest(){
	}
	
	public BinaryRequest(String url, String downloadPath){
		this.downloadPath = downloadPath;
		this.setUrl(url);
		this.processor = "";
	}

	public String getDownloadPath() {
		return downloadPath;
	}

	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		super.write(out);
		out.writeUTF(downloadPath);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		super.readFields(in);
		downloadPath = in.readUTF();
	}
	
}
