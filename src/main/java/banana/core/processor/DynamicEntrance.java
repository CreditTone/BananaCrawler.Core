package banana.core.processor;

import java.util.List;
import java.util.Map;

import banana.core.modle.ContextModle;

/**
 * 动态生成入口URL。
 * 当你的任务的入口URL每次都不是固定的话，那么可以设置一个DynamicEntrance
 * @author Administrator
 *
 */
public abstract class DynamicEntrance {
	
	/**
	 * 当爬虫调用loadStartContext前会询问是否需要清空上一次加载的入口URL
	 * @return
	 */
	public boolean isClearLast(){
		return true;
	};
	
	
	/**
	 * 自定义入口URL
	 * @return
	 */
	public abstract void loadContext(ContextModle taskContext);
	
	/**
	 * 当爬虫完成所有上一次加载的入口URL后，询问是否需要调用loadStartContext继续加载新的入口URL
	 * @return
	 */
	public boolean continueLoad(){
		return false;
	};
		
	
}
