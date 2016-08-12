package banana.core.protocol;

/**
 * 负责调用extractor服务定义
 * @author stephen
 *
 */
public interface Extractor {
	public String parseData(String config,String body) throws Exception;
}
