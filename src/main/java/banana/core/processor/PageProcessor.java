package banana.core.processor;

import java.util.List;

import banana.core.Static;
import banana.core.modle.CrawlData;
import banana.core.request.BasicRequest;
import banana.core.request.HttpRequest;
import banana.core.request.StartContext;
import banana.core.response.Page;


/**
 * PageProcessor是所有PageRequest请求完成后处理的接口。
 * 例如：startContext.createPageRequest("http://my.oschina.net/u/1377701/blog/425984",OschinaProcessor.class)即可指定处理页面结果
 * 此时重写Processor.process方法即可完成解析工作
 *
 */
public interface PageProcessor {
	
	/**
	 * 处理一个页面
	 * @param page  下载完成的网页
	 * @param context  当前所有入口的上下文对象
	 * @param queue  加入跟进Request的List容器，处理完成后queue的所有Request会被推送到抓取队列中
	 * @throws Exception 
	 */
	public void process(Page page,StartContext context,List<HttpRequest> queue,List<CrawlData> objectContainer)throws Exception; 
	
}


