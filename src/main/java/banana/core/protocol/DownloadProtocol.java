package banana.core.protocol;



import org.apache.hadoop.ipc.VersionedProtocol;

import banana.core.NodeStatus;
import banana.core.exception.DownloadException;
import banana.core.modle.Task;
import banana.core.request.Cookies;

public interface DownloadProtocol extends VersionedProtocol {
	
	public static final long versionID = 1L;
	
	boolean startDownloadTracker(String taskId,Task config,Cookies cookies) throws DownloadException;
	
	void resubmitTaskConfig(String taskId,int thread,Task config) throws DownloadException;
	
	boolean isWorking(String taskId) throws DownloadException;
	
	void stopDownloadTracker(String taskId) throws DownloadException;
	
	void stopDownloader() throws DownloadException;
	
	void injectCookies(String taskId,Cookies cookies) throws DownloadException;
	
	NodeStatus healthCheck() throws DownloadException;
}
