package banana.core.queue;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


import org.apache.log4j.Logger;

import banana.core.JedisOperator;
import banana.core.JedisOperator.Command;
import banana.core.request.HttpRequest;
import banana.core.request.BasicRequest;
import banana.core.request.BinaryRequest;
import banana.core.request.PageRequest;
import banana.core.request.TransactionRequest;
import banana.core.util.SystemUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsDateJsonBeanProcessor;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * 优先级队列
 * 
 * @author Administrator
 *
 */
public final class RedisRequestBlockingQueue implements BlockingRequestQueue,Closeable {

	private static Logger logger = Logger.getLogger(RedisRequestBlockingQueue.class);
	
	public static final String PREFIX = "_QUEUE_";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JedisOperator jOperator = null;

	private String name;
	
	/**
	 * 	按从小到依次保存所有优先级
	 */
	private List<Integer> prioritys = new ArrayList<Integer>();
	
	/**
	 * 优先级索引，用于快速确定该优先级的队列key
	 */
	private Map<Integer,String> priorityIndex = new ConcurrentHashMap<Integer,String>();
	
	
	private JsonConfig jsonConfig;
	
	/**
	 * 给定JedisPoolConfig初始化一个队列
	 * @param host
	 * @param port
	 * @param config
	 * @param queue
	 */
	public RedisRequestBlockingQueue(final String host, final int port, final JedisPoolConfig config, String queueKey) {
		jOperator = new JedisOperator() {
			
			@Override
			protected JedisPool initJedisPool() {
				return new JedisPool(config, host, port, 15000);
			}
		};
		init(queueKey);
	}


	/**
	 * 基于默认配置初始化一个队列
	 * @param host
	 * @param port
	 * @param queue
	 */
	public RedisRequestBlockingQueue(final String host, final int port, String queueKey) {
		jOperator = new JedisOperator() {
			@Override
			protected JedisPool initJedisPool() {
				JedisPoolConfig config = new JedisPoolConfig();
				// 连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
				config.setBlockWhenExhausted(true);
				// 设置的逐出策略类名, 默认DefaultEvictionPolicy(当连接超过最大空闲时间,或连接数超过最大空闲连接数)
				config.setEvictionPolicyClassName("org.apache.commons.pool2.impl.DefaultEvictionPolicy");
				// 是否启用pool的jmx管理功能, 默认true
				config.setJmxEnabled(true);
				// MBean ObjectName = new
				// ObjectName("org.apache.commons.pool2:type=GenericObjectPool,name=" +
				// "pool" + i); 默 认为"pool", JMX不熟,具体不知道是干啥的...默认就好.
				config.setJmxNamePrefix("pool");
				// 是否启用后进先出, 默认true
				config.setLifo(true);
				// 最大空闲连接数, 默认8个
				config.setMaxIdle(100);
				// 最大连接数, 默认8个
				config.setMaxTotal(300);
				// 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,
				// 默认-1
				config.setMaxWaitMillis(10000);
				// 逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
				config.setMinEvictableIdleTimeMillis(1800000);
				// 最小空闲连接数, 默认0
				config.setMinIdle(20);
				// 每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
				config.setNumTestsPerEvictionRun(3);
				// 对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数
				// 时直接逐出,不再根据MinEvictableIdleTimeMillis判断 (默认逐出策略)
				config.setSoftMinEvictableIdleTimeMillis(1800000);
				// 在获取连接的时候检查有效性, 默认false
				config.setTestOnBorrow(true);
				// 在空闲时检查有效性, 默认false
				config.setTestWhileIdle(true);
				// 逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
				config.setTimeBetweenEvictionRunsMillis(20);
				return new JedisPool(config, host, port, 15000);
			}
		};
		init(queueKey);
	}
	
	private void init(String queueKey){
		this.name = PREFIX + queueKey;
		jOperator.exe(new Command<Void>(){

			@Override
			public Void operation(Jedis jedis) throws Exception {
				Set<String> names = jedis.keys(RedisRequestBlockingQueue.this.name + "*");
				String replece = RedisRequestBlockingQueue.this.name + "_";
				for(String name : names){
					String priorityStr = name.replace(replece, "");
					Integer priority = Integer.parseInt(priorityStr);
					prioritys.add(priority);
					priorityIndex.put(priority, name);
				}
				sortPriority();
				return null;
			}});
		jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.registerJsonBeanProcessor(Date.class, new JsDateJsonBeanProcessor());
		jsonConfig.setExcludes( new String[]{"parentRequest"});
	}
	
	private void sortPriority(){
		Collections.sort(prioritys,new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				if (o1 > o2){
					return -1;
				}else if(o1 < o2){
					return 1;
				}						
				return 0;
			}
		});
	}
	
	@Override
	public HttpRequest remove() {
		HttpRequest HttpRequest = poll();
		if (HttpRequest == null) {
			throw new NoSuchElementException("队列长度为0");
		} 
		return HttpRequest;
	}

	@Override
	public HttpRequest poll() {
		HttpRequest HttpRequest = null;
		String data = jOperator.exe(new Command<String>() {

			@Override
			public String operation(Jedis jedis) throws Exception {
				for (Integer priority : prioritys) {
					String key = priorityIndex.get(priority);
					String data = jedis.rpop(key);
					if (data != null) {
						return data;
					}
				}
				return null;
			}
		});

		HttpRequest = jsonToObject(data);
		return HttpRequest;
	}

	@Override
	public HttpRequest element() {
		HttpRequest HttpRequest = peek();
		if (HttpRequest == null) {
			throw new NoSuchElementException("队列长度为0");
		}
		return HttpRequest;
	}

	
	@Override
	public HttpRequest peek() {
		HttpRequest HttpRequest = null;

		String data = jOperator.exe(new Command<String>() {
			@Override
			public String operation(Jedis jedis) throws Exception {
				for (Integer priority : prioritys) {
					String key = priorityIndex.get(priority);
					String data = jedis.rpoplpush(key, key);
					if (data != null) {
						return data;
					}
				}
				return null;
			}
		});
		HttpRequest = jsonToObject(data);
		return HttpRequest;
	}

	/**
	 * 返回队列中的元素个数。
	 */
	@Override
	public int size() {
		long size = jOperator.exe(new Command<Long>() {

			@Override
			public Long operation(Jedis jedis) throws Exception {
				Long size = new Long(0);
				for (Integer priority : prioritys) {
					Long count = jedis.llen(priorityIndex.get(priority));
					size = size + count;
				}
				return size;
			}
		});
		return (int) size;
	}

	
	@Override
	public boolean isEmpty() {
		boolean isEmpty = jOperator.exe(new Command<Boolean>() {

			@Override
			public Boolean operation(Jedis jedis) throws Exception {
				for (Integer priority : prioritys) {
					if (jedis.llen(priorityIndex.get(priority)) != 0){
						return false;
					}
				}
				return true;
			}
		});
		return isEmpty;
	}

	@Override
	public void clear() {
		jOperator.exe(new Command<Void>() {

			@Override
			public Void operation(Jedis jedis) throws Exception {
				for (Integer priority : prioritys) {
					jedis.del(priorityIndex.get(priority));
				}
				return null;
			}
		});
	}

	@Override
	public boolean add(final HttpRequest e) {
		if (!existQueue(e.getPriority())){
			generateQueuekey(e.getPriority());
		}
		jOperator.exe(new Command<Void>() {
			@Override
			public Void operation(Jedis jedis) throws Exception {
				String key = priorityIndex.get(e.getPriority());
				String data = objectToJson(e);
				jedis.lpush(key, data);
				return null;
			}
		});
		return true;
	}
	
	public final boolean existQueue(int priority){
		return priorityIndex.get(priority) != null; 
	}
	
	public final void generateQueuekey(int priority){
		String key = name + "_" + priority;
		prioritys.add(priority);
		priorityIndex.put(priority, key);
		sortPriority();
	}


	public HttpRequest take() {
		HttpRequest httpRequest = null;

		String data = jOperator.exe(new Command<String>() {

			@Override
			public String operation(Jedis jedis) throws Exception {
				while(true){
					for (Integer priority : prioritys) {
						String data = jedis.rpop(priorityIndex.get(priority));
						if (data != null){
							return data;
						}
					}
					Thread.sleep(1000);
				}
			}
		});

		httpRequest = jsonToObject(data);
		return httpRequest;
	}

	@Override
	public boolean remove(final HttpRequest o) {
		jOperator.exe(new Command<Void>() {

			@Override
			public Void operation(Jedis jedis) throws Exception {
				for (Integer priority : prioritys) {
					String data = objectToJson(o);
					long size = jedis.lrem(priorityIndex.get(priority), 1, data);
					if (size > 0){
						break;
					}
				}
				return null;
			}
		});
		return false;
	}

	public String objectToJson(HttpRequest obj) {
		if (obj == null) {
			return null;
		}
		JSONObject json = JSONObject.fromObject(obj,jsonConfig);
		return json.toString();
	}

	public HttpRequest jsonToObject(String json) {
		if (json == null) {
			return null;
		}
		HttpRequest basicRequest = null;
		JSONObject jsonObj = JSONObject.fromObject(json);
		String type = jsonObj.getString("type");
		switch(type){
			case "PAGE_REQUEST":
				basicRequest = (PageRequest)JSONObject.toBean(jsonObj, PageRequest.class);
				break;
			case "BINARY_REQUEST":
				basicRequest = (BinaryRequest)JSONObject.toBean(jsonObj, BinaryRequest.class);
				break;
		}
		if(jsonObj.containsKey("attributes")){
			JSONObject attributes = jsonObj.getJSONObject("attributes");
			if(!attributes.isEmpty()){
				Set<String> keys = attributes.keySet();
				for (String key : keys) {
					basicRequest.addAttribute(key, attributes.get(key));
				}
			}
		}
		PageRequest pageRequest =  basicRequest instanceof PageRequest?(PageRequest) basicRequest : null;
		if (pageRequest == null){
			return basicRequest;
		}
		if(jsonObj.containsKey("hedaers")){
			JSONObject hedaers = jsonObj.getJSONObject("hedaers");
			if(!hedaers.isEmpty()){
				Set<String> keys = hedaers.keySet();
				for (String key : keys) {
					pageRequest.putHeader(key, hedaers.getString(key));
				}
			}
		}
		if(jsonObj.containsKey("params")){
			JSONArray params = jsonObj.getJSONArray("params");
			for (int i = 0; i < params.size(); i++) {
				JSONObject valuePair = params.getJSONObject(i);
				pageRequest.putParams(valuePair.getString("key"), valuePair.getString("value"));
			}
		}
		return basicRequest;
	}


	@Override
	public void close(){
		try {
			jOperator.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void load(InputStream input) {
		DataInputStream dataInput = new DataInputStream(input);
		try{
			int dataLength = dataInput.readInt();
			byte[] packet = new byte[dataLength];
			PageRequest req = null;
			while(true){
				dataInput.read(packet, 0, dataLength);
				req = new PageRequest();
				req.load(packet);
				add(req);
				dataLength = dataInput.readInt();
				packet = new byte[dataLength];
			}
		}catch(EOFException e){
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (dataInput != null){
				try {
					dataInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (input != null){
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	@Override
	public byte[] toBytes() {
		byte[] qdata = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try{
			HttpRequest req = poll();
			byte[] data = null;
			while(req != null){
				data = req.toBytes();
				out.write(SystemUtil.intToBytes(data.length));
				out.write(data);
				req = poll();
			}
			qdata = out.toByteArray();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (out != null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return qdata;
	}
	
}
