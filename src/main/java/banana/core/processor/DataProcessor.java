package banana.core.processor;

import java.util.List;

import banana.core.modle.CrawlData;


/**
 * Pipeline
 *
 */
public interface DataProcessor {
	
	/**
	 * 所有的结构化数据将流向这里。在这里存储你的bean
	 * @param procdata
	 */
	public void handleData(List<CrawlData> objectContainer);
	
}
