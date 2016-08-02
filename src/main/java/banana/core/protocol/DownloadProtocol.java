package banana.core.protocol;



import org.apache.hadoop.ipc.VersionedProtocol;

import banana.core.NodeStatus;
import banana.core.exception.DownloadException;

public interface DownloadProtocol extends VersionedProtocol {
	
	public static final long versionID=1L;
	
	boolean startDownloadTracker(String trackerId) throws DownloadException;
	
	String newDownloadTracker(String taskName,int thread) throws DownloadException;
	
	void stopDownloadTracker(String trackerId) throws DownloadException;
	
	NodeStatus healthCheck() throws DownloadException;
}
