package banana.core.modle;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import org.apache.hadoop.io.Writable;

import com.alibaba.fastjson.JSON;

public class TaskStatus implements Writable{
	
	public static enum Stat {
		Runing,Timing,Stopd,NoTask
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
		}
		dataCount = in.readInt();
		requestCount = in.readInt();
//		JSONArray array = JSON.parseArray(in.readUTF());
//		for (int i = 0; i < array.length; i++) {
//			
//		}
		downloaderTrackerStatus = JSON.parseObject(in.readUTF(), List.class);
	}

	@Override
	public String toString() {
		return "TaskStatus [name=" + name + ", id=" + id + ", stat=" + stat + ", dataCount=" + dataCount
				+ ", requestCount=" + requestCount + ", downloaderTrackerStatus=" + downloaderTrackerStatus + "]";
	}

}
