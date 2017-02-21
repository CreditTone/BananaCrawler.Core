package banana.core.processor;

import java.util.List;
import banana.core.modle.ContextModle;
import banana.core.modle.CrawlData;
import banana.core.request.HttpRequest;
import banana.core.response.Page;
import banana.core.response.StreamResponse;


public interface DownloadProcessor {
	
	/**
	 * 处理一个页面
	 * @param page  下载完成的网页
	 * @param context  当前所有入口的上下文对象
	 * @param queue  加入跟进Request的List容器，处理完成后queue的所有Request会被推送到抓取队列中
	 * @throws Exception 
	 */
	public ContextModle process(Page page, Object taskContext,List<HttpRequest> queue,List<CrawlData> objectContainer)throws Exception; 
	
	public ContextModle process(StreamResponse stream,Object taskContext,List<HttpRequest> queue,List<CrawlData> objectContainer) throws Exception;
}


