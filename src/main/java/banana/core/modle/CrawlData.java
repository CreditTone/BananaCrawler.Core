package banana.core.modle;

import java.io.Serializable;
import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
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

	public CrawlData(String taskId, String link, String data) {
		super();
		this.taskId = taskId;
		this.taskName = taskId.split("_")[0];
		this.link = link;
		this.data = (DBObject) JSON.parse(data);
		this.crawlTime = new Date();
		BasicDBObject dbObject = new BasicDBObject();
		dbObject.put("task_id", taskId);
		dbObject.put("task_name", taskName);
		dbObject.put("link", link);
		dbObject.put("crawl_time", crawlTime);
		dbObject.put("body", this.data);
		this.data = dbObject;
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

	@Override
	public String toString() {
		return "CrawlData [taskId=" + taskId + ", link=" + link + ", crawlTime=" + crawlTime + ", data=" + data
				+ "]";
	}

}
