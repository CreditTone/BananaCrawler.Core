package banana.core.modle;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryOperators;
import com.mongodb.util.JSON;



/**
 * 爬虫解析后的数据，你可以知道每条数据的来自哪个task等信息
 *
 */
public class CrawlData implements Serializable{

	private String taskId;
	
	private String taskName;
	
	private String link;
	
	private Date crawlTime;
	
	private DBObject data;
	
	private DBObject updateQuery;

	public CrawlData(String taskId, String link, DBObject data) {
		this(taskId, link, data, null);
	}
	
	public CrawlData(String taskId, String link, DBObject data,DBObject updateQuery) {
		super();
		this.taskId = taskId;
		this.taskName = taskId.split("_")[0];
		this.link = link;
		this.crawlTime = new Date();
		this.updateQuery = updateQuery;
		this.data = updateQuery == null?data:new BasicDBObject("$set", data);
		data.put("_task_id", taskId);
		data.put("_task_name", taskName);
		data.put("_link", link);
		data.put("_crawl_time", crawlTime);
	}
	

	public String getTaskId() {
		return taskId;
	}

	public String getLink() {
		return link;
	}

	public Date getCrawlTime() {
		return crawlTime;
	}

	public DBObject getData() {
		return data;
	}
	
	public String getTaskName() {
		return taskName;
	}
	
	public DBObject getUpdateQuery() {
		return updateQuery;
	}

	public boolean isUpdate(){
		return updateQuery != null;
	}

	@Override
	public String toString() {
		return "CrawlData [taskId=" + taskId + ", link=" + link + ", crawlTime=" + crawlTime + ", data=" + data
				+ "]";
	}

}
