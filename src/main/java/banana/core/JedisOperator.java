package banana.core;

import java.io.Closeable;
import java.io.IOException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public abstract class JedisOperator implements Closeable{
	
	public static JedisOperator newInstance(final String redisHost,final int redisPort){
		JedisOperator redis = new JedisOperator(){
			@Override
			protected JedisPool initJedisPool() {
				JedisPoolConfig config = new JedisPoolConfig();
				config.setBlockWhenExhausted(true);
				config.setEvictionPolicyClassName("org.apache.commons.pool2.impl.DefaultEvictionPolicy");
				config.setJmxEnabled(true);
				config.setJmxNamePrefix("pool");
				config.setLifo(true);
				config.setMaxIdle(30);
				config.setMaxTotal(100);
				config.setMaxWaitMillis(10000);
				config.setMinEvictableIdleTimeMillis(1800000);
				config.setMinIdle(20);
				config.setNumTestsPerEvictionRun(3);
				config.setSoftMinEvictableIdleTimeMillis(1800000);
				config.setTestOnBorrow(true);
				config.setTestWhileIdle(true);
				config.setTimeBetweenEvictionRunsMillis(20);
				return new JedisPool(config, redisHost, redisPort, 15000);
			}};
		return redis;
	}
	
	public static JedisOperator newInstance(final JedisPoolConfig config,final String redisHost,final int redisPort){
		JedisOperator redis = new JedisOperator(){
			@Override
			protected JedisPool initJedisPool() {
				return new JedisPool(config, redisHost, redisPort, 15000);
			}};
		return redis;
	}
	
	private JedisPool pool = null;
	
	public JedisOperator(){
		pool = initJedisPool();
	}

	protected abstract JedisPool initJedisPool();
	
	public <E> E exe(Command<E> cmd){
		Jedis jedis = null;
		E result = null;
		try {
			jedis = pool.getResource();
			result = cmd.operation(jedis);
		} catch (Exception e) {
			e.printStackTrace();
			cmd.exceptionOccurs(e);
			if (jedis != null) {
				pool.returnBrokenResource(jedis);
				jedis = null;
			}
		} finally {
			if (jedis != null) {
				pool.returnResource(jedis);
			}
		}
		return result;
	}
	
	@SuppressWarnings("unused")
	public static abstract class Command<E> {
		public abstract E operation(Jedis jedis) throws Exception;
		
		protected void exceptionOccurs(Exception e){}
		
	}

	public void close() throws IOException {
		this.pool.close();
		this.pool.destroy();
	}
	
	
}