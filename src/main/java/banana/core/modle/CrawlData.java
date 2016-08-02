package banana.core.modle;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.apache.hadoop.io.Writable;



/**
 * 爬虫解析后的数据，你可以知道每条数据的来自哪个task等信息
 *
 */
public class CrawlData implements Serializable,Writable{

	private String taskName;
	
	private String link;
	
	private Date crawlTime;
	
	private Map<String,Object> data;

	public CrawlData(String taskName, String link, Map<String,Object> data) {
		super();
		this.taskName = taskName;
		this.link = link;
		this.data = data;
		this.crawlTime = new Date();
	}

	public String getTaskName() {
		return taskName;
	}

	public String getLink() {
		return link;
	}

	public Date getCrawlTime() {
		return crawlTime;
	}

	public Map<String,Object> getData() {
		return data;
	}

	@Override
	public String toString() {
		return "CrawlData [taskName=" + taskName + ", link=" + link + ", crawlTime=" + crawlTime + ", data=" + data
				+ "]";
	}

	@Override
	public void readFields(DataInput in) throws IOException {
	}

	@Override
	public void write(DataOutput out) throws IOException {
	}
	
}
