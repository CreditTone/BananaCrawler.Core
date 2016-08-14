package banana.core.protocol;

import java.util.List;

import org.apache.hadoop.ipc.VersionedProtocol;


import banana.core.exception.CrawlerMasterException;
import banana.core.request.BasicRequest;


public interface CrawlerMasterProtocol extends VersionedProtocol{
	
	public static final long versionID = 1L;
	
	void registerDownloadNode(String remote,int port) throws CrawlerMasterException;
	
	public void startTask(Task config) throws Exception;
	
	Object getTaskPropertie(String taskId,String name) ;
	
	Object getMasterPropertie(String name) throws CrawlerMasterException;
	
	void pushTaskRequests(String taskId,List<BasicRequest> requests) throws CrawlerMasterException;
	
	List<BasicRequest> pollTaskRequests(String taskId,int fetchsize) throws CrawlerMasterException;
	
	//Object getStartContextAttribute(String taskName,String hashCode,String attribute) throws java.rmi.RemoteException;
	
}
