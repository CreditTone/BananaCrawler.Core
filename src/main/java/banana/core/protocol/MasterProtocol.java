package banana.core.protocol;


import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.ipc.VersionedProtocol;


import banana.core.exception.CrawlerMasterException;
import banana.core.modle.MasterConfig;
import banana.core.request.Cookies;
import banana.core.request.HttpRequest;


public interface MasterProtocol extends VersionedProtocol{
	
	public static final long versionID = 1L;
	
	void registerDownloadNode(String remote,int port) throws CrawlerMasterException;
	
	void submitTask(Task config) throws Exception;
	
	void stopTask(String taskname) throws Exception;
	
	void stopCluster() throws Exception;
	
	BooleanWritable existTask(String taskname);
	
	BooleanWritable taskdataExists(String collection,String taskname);
	
	BooleanWritable statExists(String collection,String taskname);
	
	IntWritable removeBeforeResult(String collection,String taskname) throws Exception;
	
	MasterConfig getMasterConfig() throws CrawlerMasterException;
	
	void pushTaskRequest(String taskId,HttpRequest request) throws CrawlerMasterException;
	
	HttpRequest pollTaskRequest(String taskId) throws CrawlerMasterException;
	
	BooleanWritable filterQuery(String taskId,String ... fields);
	
	void injectCookies(Cookies cookies,String taskId) throws Exception;

	//Text getStartContextAttribute(String taskname,String hashCode,String attribute) throws java.rmi.RemoteException;
	
}