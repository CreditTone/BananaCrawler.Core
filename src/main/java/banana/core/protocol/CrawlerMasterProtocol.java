package banana.core.protocol;


import org.apache.hadoop.ipc.VersionedProtocol;


import banana.core.exception.CrawlerMasterException;
import banana.core.request.BasicRequest;


public interface CrawlerMasterProtocol extends VersionedProtocol{
	
	public static final long versionID = 1L;
	
	void registerDownloadNode(String remote,int port) throws CrawlerMasterException;
	
	public void startTask(Task config) throws Exception;
	
	Task getConfig(String taskId);
	
	Object getTaskPropertie(String taskId,String name) ;
	
	Object getMasterPropertie(String name) throws CrawlerMasterException;
	
	void pushTaskRequest(String taskId,BasicRequest request) throws CrawlerMasterException;
	
	BasicRequest pollTaskRequest(String taskId) throws CrawlerMasterException;
	
	//Object getStartContextAttribute(String taskName,String hashCode,String attribute) throws java.rmi.RemoteException;
	
}
