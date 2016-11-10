package banana.core.modle;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

import com.alibaba.fastjson.JSON;

public class MasterConfig implements Writable{

	public Integer listen;
	
	public Integer handlers;

	public static class Extractor {
		public String host;
		public Integer port;
	}

	public Extractor extractor;

	public static class MongoDB {
		public String host;
		public Integer port;
		public String db;
		public String username;
		public String password;
	}

	public MongoDB mongodb;

	public String jdbc;

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(JSON.toJSONString(this));
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		MasterConfig config = JSON.parseObject(in.readUTF(), MasterConfig.class);
		this.listen = config.listen;
		this.jdbc = config.jdbc;
		this.extractor = config.extractor;
		this.mongodb = config.mongodb;
	}

}


