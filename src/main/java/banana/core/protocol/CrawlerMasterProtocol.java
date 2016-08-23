package banana.core.protocol;


import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.ipc.VersionedProtocol;


import banana.core.exception.CrawlerMasterException;
import banana.core.request.BasicRequest;
import banana.core.request.HttpRequest;


public interface CrawlerMasterProtocol extends VersionedProtocol{
	
	public static final long versionID = 1L;
	
	void registerDownloadNode(String remote,int port) throws CrawlerMasterException;
	
	void submitTask(Task config) throws Exception;
	
	BooleanWritable existTask(String taskName);
	
	BooleanWritable dataExists(String collection,String taskName);
	
	IntWritable removeBeforeResult(String collection,String taskName) throws Exception;
	
	Text getMasterPropertie(String name) throws CrawlerMasterException;
	
	void pushTaskRequest(String taskId,HttpRequest request) throws CrawlerMasterException;
	
	HttpRequest pollTaskRequest(String taskId) throws CrawlerMasterException;

	//Text getStartContextAttribute(String taskName,String hashCode,String attribute) throws java.rmi.RemoteException;
	
}
