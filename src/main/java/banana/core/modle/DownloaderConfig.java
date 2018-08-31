package banana.core.modle;


public class DownloaderConfig {
	
	public Integer listen;
	
	public Integer handlers;

	public static class Master {
		public String host;
		public Integer port;
	}

	public Master master;
	
}
