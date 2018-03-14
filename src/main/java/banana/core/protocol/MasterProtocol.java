package banana.core.protocol;


import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.ipc.VersionedProtocol;


import banana.core.exception.CrawlerMasterException;
import banana.core.modle.BasicWritable;
import banana.core.modle.CommandResponse;
import banana.core.modle.MasterConfig;
import banana.core.modle.PreparedTask;
import banana.core.modle.Task;
import banana.core.modle.TaskError;
import banana.core.modle.TaskStatus;
import banana.core.request.Cookies;
import banana.core.request.HttpRequest;


public interface MasterProtocol extends VersionedProtocol{
	
	public static final long versionID = 1L;
	
	CommandResponse registerDownloadNode(String remote,int port);
	
	CommandResponse submitTask(Task config) throws Exception;
	
	CommandResponse startPreparedTask(PreparedTask config) throws Exception;
	
	CommandResponse stopTaskById(String taskid)throws Exception;
	
	CommandResponse stopCluster() throws Exception;
	
	TaskStatus getTaskStatusById(String taskid);
	
	IntWritable removeBeforeResult(String collection,String taskname) throws Exception;
	
	BooleanWritable verifyPassword(String password) throws Exception;
	
	MasterConfig getMasterConfig() throws CrawlerMasterException;
	
	CommandResponse pushTaskRequest(String taskId,HttpRequest request) throws CrawlerMasterException;
	
	HttpRequest pollTaskRequest(String taskId) throws CrawlerMasterException;
	
	BooleanWritable filterQuery(String taskId,String fieldValue);
	
	void addFilterField(String taskId,String fieldValue);
	
	CommandResponse injectCookies(Cookies cookies,String taskId) throws Exception;
	
	void errorStash(String taskId,TaskError taskError) throws Exception;

	BasicWritable getTaskContextAttribute(String taskid,String attribute) throws Exception;
	
	void putTaskContextAttribute(String taskid,String attribute,BasicWritable value) throws Exception;
	
	void putGlobalContext(String attribute,BasicWritable value) throws Exception;
}
