package banana.core.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.io.Writable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

public final class Task implements Writable{
	
	public static class Seed{
		
		protected String url;
		
		protected String method;
		
		protected Map<String,String> headers;
		
		protected Map<String,String> params;
		
		protected String processor;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		public Map<String, String> getHeaders() {
			return headers;
		}

		public void setHeaders(Map<String, String> headers) {
			this.headers = headers;
		}

		public Map<String, String> getParams() {
			return params;
		}

		public void setParams(Map<String, String> params) {
			this.params = params;
		}

		public String getProcessor() {
			return processor;
		}

		public void setProcessor(String processor) {
			this.processor = processor;
		}
	}
	
	public static final class CrawlerRequest extends HashMap<String,Object>{}
	
	public static final class CrawlerData extends HashMap<String,Object>{}
	
	public static final class Processor {
		
		private String index;
		
		private CrawlerRequest[] crawler_request;
		
		private CrawlerData[] crawler_data;

		public String getIndex() {
			return index;
		}

		public void setIndex(String index) {
			this.index = index;
		}

		public CrawlerRequest[] getCrawler_request() {
			return crawler_request;
		}

		public void setCrawler_request(CrawlerRequest[] crawler_request) {
			this.crawler_request = crawler_request;
		}

		public CrawlerData[] getCrawler_data() {
			return crawler_data;
		}

		public void setCrawler_data(CrawlerData[] crawler_data) {
			this.crawler_data = crawler_data;
		}

	}
	
	public void verify() throws Exception {
		if (name == null || name.trim().equals("")){
			throw new NullPointerException("task name cannot be null");
		}
		if (processors == null || processors.isEmpty()){
			throw new Exception("There is no processors");
		}
		Set<String> indexs = new HashSet<String>();
		for (Processor processor : processors) {
			if (processor.index == null){
				throw new NullPointerException("processor index cannot be null");
			}
			indexs.add(processor.index);
		}
		if (seeds.isEmpty()){
			throw new Exception("There is no seed");
		}
		for (Seed seed : seeds) {
			if (seed.url == null){
				throw new NullPointerException("seed url cannot be null");
			}
			if (seed.processor == null){
				throw new NullPointerException("seed processor cannot be null");
			}
			if (!indexs.contains(seed.processor)){
				throw new IllegalArgumentException("processor " + seed.processor + " does not exist");
			}
		}
		if (thread <= 0){
			throw new IllegalArgumentException("the number of threads must be greater than zero");
		}
	}
	
	/**
	 * 任务的名字
	 */
	public String name;
	
	public int thread;
	
	/**
	 * 任务的初始种子
	 */
	public List<Seed> seeds;
	
	/**
	 * 页面处理器
	 */
	public List<Processor> processors;

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(name);
		out.writeInt(thread);
		String seedJson = JSON.toJSONString(seeds);
		String processorJson = JSON.toJSONString(processors);
		out.writeUTF(seedJson);
		out.writeUTF(processorJson);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		name = in.readUTF();
		thread = in.readInt();
		seeds = new ArrayList<Seed>();
		String seedJson = in.readUTF();
		String processorJson = in.readUTF();
		JSONArray array = JSONArray.parseArray(seedJson);
		for (int i = 0; i < array.size(); i++) {
			Seed seed = JSON.parseObject(array.getJSONObject(i).toString(), Seed.class);
			seeds.add(seed);
		}
		processors = new ArrayList<Processor>();
		array = JSONArray.parseArray(processorJson);
		for (int i = 0; i < array.size(); i++) {
			Processor processor = JSON.parseObject(array.getJSONObject(i).toString(), Processor.class);
			processors.add(processor);
		}
	}
	
	
}


