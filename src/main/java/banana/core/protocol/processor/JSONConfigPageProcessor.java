package banana.core.protocol.processor;

import java.util.List;

import com.alibaba.fastjson.JSON;

import banana.core.modle.CrawlData;
import banana.core.processor.PageProcessor;
import banana.core.protocol.Extractor;
import banana.core.protocol.Task;
import banana.core.protocol.Task.Processor;
import banana.core.request.BasicRequest;
import banana.core.request.StartContext;
import banana.core.response.Page;

public class JSONConfigPageProcessor implements PageProcessor {
	
	private Task.Processor config;
	
	private String[] dataParser;
	
	private String[] requestParser;
	
	private Extractor extractor;
	
	public JSONConfigPageProcessor(Processor config) {
		this.config = config;
		this.dataParser = new String[config.getCrawler_data().length];
		for (int i = 0 ;i < config.getCrawler_data().length ;i++) {
			dataParser[i] = JSON.toJSONString(config.getCrawler_data()[i]);
		}
		this.requestParser = new String[config.getCrawler_request().length];
		for (int i = 0 ;i < config.getCrawler_request().length ;i++) {
			requestParser[i] = JSON.toJSONString(config.getCrawler_request()[i]);
		}
	}


	@Override
	public void process(Page page, StartContext context, List<BasicRequest> queue, List<CrawlData> objectContainer)
			throws Exception {
	}

}
