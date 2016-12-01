package banana.core.protocol;


import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.ipc.VersionedProtocol;


import banana.core.exception.CrawlerMasterException;
import banana.core.modle.CommandResponse;
import banana.core.modle.MasterConfig;
import banana.core.modle.TaskStatus;
import banana.core.request.Cookies;
import banana.core.request.HttpRequest;


public interface MasterProtocol extends VersionedProtocol{
	
	public static final long versionID = 1L;
	
	CommandResponse registerDownloadNode(String remote,int port);
	
	CommandResponse submitTask(Task config) throws Exception;
	
	CommandResponse stopTask(String taskname)throws Exception;
	
	CommandResponse stopCluster() throws Exception;
	
	TaskStatus taskStatus(Task taskconfig);
	
	IntWritable removeBeforeResult(String collection,String taskname) throws Exception;
	
	BooleanWritable verifyPassword(String password) throws Exception;
	
	MasterConfig getMasterConfig() throws CrawlerMasterException;
	
	CommandResponse pushTaskRequest(String taskId,HttpRequest request) throws CrawlerMasterException;
	
	HttpRequest pollTaskRequest(String taskId) throws CrawlerMasterException;
	
	BooleanWritable filterQuery(String taskId,String ... fields);
	
	CommandResponse injectCookies(Cookies cookies,String taskId) throws Exception;

	//Text getStartContextAttribute(String taskname,String hashCode,String attribute) throws java.rmi.RemoteException;
	
}
