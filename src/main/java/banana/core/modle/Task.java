package banana.core.modle;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.io.Writable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

public final class Task implements Writable, Cloneable {
	
	public static class GlobalSeed {
		/**
		 * 任务的初始种子
		 */
		public static class Init {
			
			public List<Seed> seeds;

			public SeedQuery seed_query;
		}
		
		/**
		 * 任务完成后需要执行的种子
		 */
		public static class After {
			
			public List<Seed> seeds;
			
			public SeedQuery seed_query;
		}
		
		public Init init;
		
		public After after;
	}

	public static class Seed {

		public String url;

		public String[] urls;

		public String download;

		public String[] downloads;

		public Map<String, Object> url_iterator;

		public String method;

		public Map<String, String> headers;

		public Map<String, String> params;

		public String processor;
	}

	public static class SeedQuery {

		public HashMap<String, Object> find;

		public boolean keep;

		public String url;

		public String download;

		public String processor;

		public String method;

		public Map<String, String> headers;

		public Map<String, String> params;

	}

	public static class Timer {

		public String first_start;

		public String period;

	}

	public static final class CrawlerRequest extends HashMap {
	}

	public static final class CrawlerData extends HashMap {
	}

	public static class BasicProcessor {

		public static final class BlockCondition {

			public String condition;

			public String email;

		}

		public String index;

		public Object content_prepare;

		public boolean keep_down;

		public Map<String, Object> page_context;

		public Map<String, Object> task_context;
		
		public Map<String, Object> global_context;

		public List<BlockCondition> blockConditions;

		public String[] logs;
	}

	public static final class Processor extends BasicProcessor {

		public static final class Forwarder {

			public String condition;

			public String processor;

		}

		public CrawlerRequest[] crawler_request;

		public CrawlerData[] crawler_data;

		public Forwarder[] forwarders;
		
		
		//文件处理配置
		public boolean zip;
		
		public HashMap csv_data;
	}

	public static final class Mode {

		public Timer timer;

		public boolean prepared;

	}

	public void verify() throws Exception {
		if (name == null || name.trim().equals("")) {
			throw new NullPointerException("task name cannot be null");
		} else if (name.contains("_")) {
			throw new IllegalArgumentException("name cannot contain underscore symbols");
		}
		if (collection == null || collection.trim().equals("")) {
			throw new NullPointerException("task collection cannot be null");
		} else if (collection.contains("_")) {
			throw new IllegalArgumentException("collection cannot contain underscore symbols");
		}
		if (!Arrays.asList("default", "phantomjs", "htmlunit").contains(downloader)) {
			throw new IllegalArgumentException("downloader " + downloader + " doesn't support");
		}
		if (processors == null || processors.isEmpty()) {
			throw new Exception("There is no processors");
		}
		Set<String> indexs = new HashSet<String>();
		for (Processor processor : processors) {
			if (processor.index == null) {
				throw new NullPointerException("processor index cannot be null");
			}
			indexs.add(processor.index);
		}
		if (seed == null) {
			throw new Exception("There is no seed");
		}
		if (thread <= 0) {
			throw new IllegalArgumentException("the number of threads must be greater than zero");
		}
		if (mode != null && mode.prepared && mode.timer != null) {
			throw new IllegalArgumentException("微型任务模式和定时模式不能同时存在");
		}
	}

	/**
	 * 任务的名字
	 */
	public String name;

	public String collection;
	
	public GlobalSeed seed;

	public String downloader = "default";

	public int thread;

	public Map<String, Object> queue;

	public String filter;

	public Mode mode;
	
	public boolean allow_multi_task;

	public String condition;
	/**
	 * 页面处理器
	 */
	public List<Processor> processors;

	public String data;

	public boolean synchronizeLinks;

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(name);
		out.writeUTF(collection);
		out.writeUTF(downloader);
		out.writeInt(thread);
		out.writeBoolean(synchronizeLinks);
		out.writeBoolean(allow_multi_task);
		out.writeUTF(condition == null ? "" : condition);
		out.writeUTF(filter == null ? "" : filter);
		String queueJson = JSON.toJSONString(queue == null ? new HashMap<String, Object>() : queue);
		String seedJson = JSON.toJSONString(seed);
		String modeJson = mode == null ? "{}" : JSON.toJSONString(mode);
		String processorJson = JSON.toJSONString(processors);
		out.writeUTF(queueJson);
		out.writeUTF(seedJson);
		out.writeUTF(modeJson);
		out.writeUTF(processorJson);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		name = in.readUTF();
		collection = in.readUTF();
		downloader = in.readUTF();
		thread = in.readInt();
		synchronizeLinks = in.readBoolean();
		allow_multi_task = in.readBoolean();
		condition = in.readUTF();
		filter = in.readUTF();
		String queueJson = in.readUTF();
		String seedJson = in.readUTF();
		String modeJson = in.readUTF();
		String processorJson = in.readUTF();

		queue = JSON.parseObject(queueJson, Map.class);

		seed = JSON.parseObject(seedJson, GlobalSeed.class);

		if (!modeJson.equals("{}")) {
			mode = JSON.parseObject(modeJson, Mode.class);
		}

		processors = new ArrayList<Processor>();
		JSONArray array = JSONArray.parseArray(processorJson);
		for (int i = 0; i < array.size(); i++) {
			Processor processor = JSON.parseObject(array.getJSONObject(i).toString(), Processor.class);
			processors.add(processor);
		}

	}

	public Object clone() {
		Object o = null;
		try {
			o = (Task) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}
}
