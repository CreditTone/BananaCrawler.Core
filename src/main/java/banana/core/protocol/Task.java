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
		
		protected String url;
		
		protected String[] urls;
		
		protected String method;
		
		protected Map<String,String> headers;
		
		protected Map<String,String> params;
		
		protected String processor;

		public String[] getUrls() {
			return urls;
		}

		public void setUrls(String[] urls) {
			this.urls = urls;
		}

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
	
	public static class ExpandableHashMap extends HashMap<String,Object>{
		
		public String tag = null;
		
		public HashSet<String> sendRequest = null;
		
		public List<String> unique = null;

		public HashMap<String,Object> cite = new HashMap<String,Object>();
		
		@Override
		public Object put(String key, Object value) {
			if (key.equals("_tag")){
				tag = (String) value;
			}else if (key.equals("_sendrequest")){
				sendRequest = new HashSet<String>((Collection<? extends String>) value);
			}else if (key.equals("_unique")){
				unique = (List<String>) value;
				return value;
			}else if (key.startsWith("_")){
				return super.put(key, value);
			}
			if (value instanceof String 
				&& value.toString().contains("{{")
				&& value.toString().contains("}}")){
				String valueString = (String) value;
				cite.put(key, valueString);
				return valueString;
			}
			return super.put(key, value);
		}

		@Override
		public void putAll(Map<? extends String, ? extends Object> m) {
			for (Entry<? extends String, ? extends Object> entry : m.entrySet()) {
				put(entry.getKey(), entry.getValue());
			}
		}
		
		
		@Override
		public Set<Entry<String, Object>> entrySet() {
			Set<Entry<String, Object>> entrys = new HashSet<Entry<String, Object>>(super.entrySet());
			entrys.addAll(cite.entrySet());
			HashMap<String,Object> uniqueMap = new HashMap<String,Object>();
			uniqueMap.put("_unique", unique);
			uniqueMap.put("_sendrequest", sendRequest);
			uniqueMap.put("_tag", tag);
			entrys.addAll(uniqueMap.entrySet());
			return entrys;
		}

		public HashMap<String, Object> getCite() {
			return cite;
		}
		
		public Collection<String> getUnique(){
			return unique;
		}
		
		public String getUnique(int index){
			return unique.get(index);
		}
		
		public HashSet<String> getSendRequest(){
			return sendRequest;
		}
		
		public String getTag(){
			return tag;
		}
		
	}
	
	public static final class CrawlerRequest extends ExpandableHashMap{}
	
	public static final class CrawlerData extends ExpandableHashMap{}
	
	public static final class ProcessorForwarder {
		
		private String index;
		
		private Map<String,String>[] selector;
		
		private Map<String,Object> page_context;

		public String getIndex() {
			return index;
		}

		public void setIndex(String index) {
			this.index = index;
		}

		public Map<String, String>[] getSelector() {
			return selector;
		}

		public void setSelector(Map<String, String>[] selector) {
			this.selector = selector;
		}

		public Map<String, Object> getPage_context() {
			return page_context;
		}

		public void setPage_context(Map<String, Object> page_context) {
			this.page_context = page_context;
		}
	}
	
	public static final class ContentProcessor{
		
		public List<String> direct;
		
		public Map<String,Object> define;
		
	}
	
	public static final class Processor {
		
		private String index;
		
		private ContentProcessor content;
		
		private Map<String,Object> page_context;
		
		private Map<String,Object> task_context;
		
		private CrawlerRequest[] crawler_request;
		
		private CrawlerData[] crawler_data;
		

		public String getIndex() {
			return index;
		}

		public void setIndex(String index) {
			this.index = index;
		}
		
		public ContentProcessor getContent() {
			return content;
		}

		public void setContent(ContentProcessor content) {
			this.content = content;
		}

		public Map<String, Object> getPage_context() {
			return page_context;
		}

		public void setPage_context(Map<String, Object> page_context) {
			this.page_context = page_context;
		}

		public Map<String, Object> getTask_context() {
			return task_context;
		}

		public void setTask_context(Map<String, Object> task_context) {
			this.task_context = task_context;
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
		if (seeds.isEmpty()){
			throw new Exception("There is no seed");
		}
		for (Seed seed : seeds) {
			if (seed.url == null && seed.urls == null){
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
		String seedJson = JSON.toJSONString(seeds);
		String forwarderJson = forwarders==null?"[]":JSON.toJSONString(forwarders);
		String processorJson = JSON.toJSONString(processors);
		out.writeUTF(filterJson);
		out.writeUTF(queueJson);
		out.writeUTF(seedJson);
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


