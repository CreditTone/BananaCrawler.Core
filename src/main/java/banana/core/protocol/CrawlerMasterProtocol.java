package banana.core.protocol;

import java.util.List;

import org.apache.hadoop.ipc.VersionedProtocol;

import banana.core.exception.CrawlerMasterException;
import banana.core.request.BasicRequest;


public interface CrawlerMasterProtocol extends VersionedProtocol{
	
	public static final long versionID = 1L;
	
	void registerDownloadNode(String remote) throws CrawlerMasterException;
	
	void startTask(String taskConfig) throws CrawlerMasterException;
	
	Object getTaskPropertie(String taskName,String name) throws CrawlerMasterException;
	
	Object getMasterPropertie(String name) throws CrawlerMasterException;
	
	void pushTaskRequests(String taskName,List<BasicRequest> requests) throws CrawlerMasterException;
	
	List<BasicRequest> pollTaskRequests(String taskName,int fetchsize) throws CrawlerMasterException;
	
	//Object getStartContextAttribute(String taskName,String hashCode,String attribute) throws java.rmi.RemoteException;
	
}
