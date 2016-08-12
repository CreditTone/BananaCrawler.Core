package banana.core;

public final class PropertiesNamespace {
	
	public static final class Master{
		public static final String REDIS_HOST = "banana.master.redis.host";
		public static final String REDIS_PORT = "banana.master.redis.port";
	}
	
	public static final class Task{
		public static final String MAX_PAGE_RETRY_COUNT = "banana.task.page.maxretrycount";
	}
	
	
}
