package banana.core.download;

import banana.core.request.BinaryRequest;

public  interface FileDownloader {
	
	/**
	 * 下载文件
	 * @param req
	 * @param saveFile
	 */
	public void downloadFile(BinaryRequest req );
	
	/**
	 * 下载延迟时间
	 * @param time
	 */
	public void setDelayTime(int time);
	
}
