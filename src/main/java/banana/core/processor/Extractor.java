package banana.core.processor;

/**
 * 负责调用extractor服务定义
 * @author stephen
 *
 */
public interface Extractor {
	public String parseData(String config,String body) throws Exception;
}
