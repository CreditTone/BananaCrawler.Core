package banana.core.modle;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.Writable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

public class TaskStatus implements Writable{
	
	public static enum Stat {
		Runing,Timing,Stopd,NoTask,Prepared
	}
	
	public static final class DownloaderTrackerStatus {
		
		public String owner;
		
		public int thread;
		
		public Stat stat;
		
	}
	
	public String name;
	
	public String id;
	
	public Stat stat;
	
	public int dataCount;
	
	public int requestCount;
	
	public List<DownloaderTrackerStatus> downloaderTrackerStatus;
	

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(name);
		out.writeUTF(id);
		if (stat == Stat.Runing){
			out.writeInt(0);
		}else if (stat == Stat.Timing){
			out.writeInt(1);
		}else if (stat == Stat.Stopd){
			out.writeInt(2);
		}else if (stat == Stat.NoTask){
			out.writeInt(3);
		}else if (stat == Stat.Prepared){
			out.writeInt(4);
		}
		out.writeInt(dataCount);
		out.writeInt(requestCount);
		out.writeUTF(JSON.toJSONString(downloaderTrackerStatus));
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		name = in.readUTF();
		id = in.readUTF();
		int stat = in.readInt();
		switch(stat){
		case 0:
			this.stat = Stat.Runing;
			break;
		case 1:
			this.stat = Stat.Timing;
			break;
		case 2:
			this.stat = Stat.Stopd;
			break;
		case 3:
			this.stat = Stat.NoTask;
			break;
		case 4:
			this.stat = Stat.Prepared;
		}
		dataCount = in.readInt();
		requestCount = in.readInt();
		JSONArray array = JSON.parseArray(in.readUTF());
		downloaderTrackerStatus = new ArrayList<DownloaderTrackerStatus>();
		for (int i = 0; i < array.size(); i++) {
			String json = array.getJSONObject(i).toJSONString();
			downloaderTrackerStatus.add(JSON.parseObject(json, DownloaderTrackerStatus.class));
		}
	}

	@Override
	public String toString() {
		return "TaskStatus [name=" + name + ", id=" + id + ", stat=" + stat + ", dataCount=" + dataCount
				+ ", requestCount=" + requestCount + ", downloaderTrackerStatus=" + downloaderTrackerStatus + "]";
	}

}
