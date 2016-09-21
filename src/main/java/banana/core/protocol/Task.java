package banana.core.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
		
		public String url;
		
		public String[] urls;
		
		public Map<String,Object> url_iterator;
		
		public String method;
		
		public Map<String,String> headers;
		
		public Map<String,String> params;
		
		public String processor;
	}
	
	public static class SeedGenerator{
		
		public HashMap<String,Object> find;
		
		public boolean keep;
		
		public String url;
		
		public String processor;
		
		public String method;
		
		public Map<String,String> headers;
		
		public Map<String,String> params;
		
	}
	
	public static final class CrawlerRequest extends HashMap{}
	
	public static final class CrawlerData extends HashMap{}
	
	public static final class ContentPrepare{
		
		public List<String> direct;
		
		public Map<String,Object> define;
		
	}
	
	public static class BasicProcessor {
		
		public String index;
		
		public ContentPrepare content_prepare;
		
		public Map<String,Object> page_context;
		
		public Map<String,Object> task_context;
		
	}
	
	public static final class Processor extends BasicProcessor {
		
		public CrawlerRequest[] crawler_request;
		
		public CrawlerData[] crawler_data;

	}
	
	public static final class ProcessorForwarder extends BasicProcessor {
		
		public Map<String,String>[] selector;
		
	}
	
	public static final class Filter{
		
		public String type;
		
		public HashSet<String>  target;

	}
	
	public void verify() throws Exception {
		if (name == null || name.trim().equals("")){
			throw new NullPointerException("task name cannot be null");
		}else if(name.contains("_")){
			throw new IllegalArgumentException("name cannot contain underscore symbols");
		}
		if (collection == null || collection.trim().equals("")){
			throw new NullPointerException("task collection cannot be null");
		}else if(collection.contains("_")){
			throw new IllegalArgumentException("collection cannot contain underscore symbols");
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
		if (seed_generator == null || seeds == null || seeds.isEmpty()){
			throw new Exception("There is no seed");
		}
		if (seed_generator != null){
			if (!indexs.contains(seed_generator.processor)){
				throw new IllegalArgumentException("processor " + seed_generator.processor + " does not exist");
			}
		}
		if (seeds != null){
			for (Seed seed : seeds) {
				if (seed.url == null && seed.urls == null && seed.url_iterator == null){
					throw new NullPointerException("seed url cannot be null");
				}
				if (seed.processor == null){
					throw new NullPointerException("seed processor cannot be null");
				}
				if (!indexs.contains(seed.processor)){
					throw new IllegalArgumentException("processor " + seed.processor + " does not exist");
				}
			}
		}
		if (thread <= 0){
			throw new IllegalArgumentException("the number of threads must be greater than zero");
		}
		
		if (loops <= 0){
			loops = 1;
		}
		
		if (filter.target == null){
			filter.target = new HashSet<String>();
		}
	}
	
	/**
	 * 任务的名字
	 */
	public String name;
	
	public String collection;
	
	public int thread;
	
	public int loops;
	
	public Map<String,Object> queue;
	
	public Filter filter;
	
	/**
	 * 任务的初始种子
	 */
	public List<Seed> seeds;
	
	public SeedGenerator seed_generator;
	
	public List<ProcessorForwarder> forwarders;
	
	/**
	 * 页面处理器
	 */
	public List<Processor> processors;
	
	public String data;
	
	public boolean synchronizeStat;

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(name);
		out.writeUTF(collection);
		out.writeInt(thread);
		out.writeInt(loops);
		out.writeBoolean(synchronizeStat);
		String filterJson = JSON.toJSONString(filter == null?new Filter():filter);
		String queueJson = JSON.toJSONString(queue == null?new HashMap<String,Object>():queue);
		String seedJson = JSON.toJSONString(seeds == null?new ArrayList<Seed>():seeds);
		String seedGeneratorJson = seed_generator == null?"{}":JSON.toJSONString(seed_generator);
		String forwarderJson = forwarders==null?"[]":JSON.toJSONString(forwarders);
		String processorJson = JSON.toJSONString(processors);
		out.writeUTF(filterJson);
		out.writeUTF(queueJson);
		out.writeUTF(seedJson);
		out.writeUTF(seedGeneratorJson);
		out.writeUTF(forwarderJson);
		out.writeUTF(processorJson);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		name = in.readUTF();
		collection = in.readUTF();
		thread = in.readInt();
		loops = in.readInt();
		synchronizeStat = in.readBoolean();
		String filterJson = in.readUTF();
		String queueJson = in.readUTF();
		String seedJson = in.readUTF();
		String seedGeneratorJson = in.readUTF();
		String forwarderJson = in.readUTF();
		String processorJson = in.readUTF();
		
		filter = JSON.parseObject(filterJson, Filter.class);
		
		queue = JSON.parseObject(queueJson, Map.class);
		
		seeds = new ArrayList<Seed>();
		JSONArray array = JSONArray.parseArray(seedJson);
		for (int i = 0; i < array.size(); i++) {
			Seed seed = JSON.parseObject(array.getJSONObject(i).toString(), Seed.class);
			seeds.add(seed);
		}
		
		if (!seedGeneratorJson.equals("{}")){
			seed_generator = JSON.parseObject(seedGeneratorJson, SeedGenerator.class);
		}
		
		forwarders = new ArrayList<ProcessorForwarder>();
		array = JSONArray.parseArray(forwarderJson);
		for (int i = 0; i < array.size(); i++) {
			ProcessorForwarder proforwarder = JSON.parseObject(array.getJSONObject(i).toString(), ProcessorForwarder.class);
			forwarders.add(proforwarder);
		}
		
		processors = new ArrayList<Processor>();
		array = JSONArray.parseArray(processorJson);
		for (int i = 0; i < array.size(); i++) {
			Processor processor = JSON.parseObject(array.getJSONObject(i).toString(), Processor.class);
			processors.add(processor);
		}
	}
	
}


