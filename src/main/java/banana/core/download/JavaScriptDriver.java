package banana.core.download;

import org.openqa.selenium.WebDriver;

public interface JavaScriptDriver extends WebDriver{
	/**
	 * 同步执行javaScript脚本
	 * @param script
	 * @param args
	 * @return
	 */
	public Object executeScript(String script, final Object... args) ;
	
	
	/**
	 * 异步执行javaScript脚本
	 * @param script
	 * @param args
	 * @return
	 */
	public Object executeAsyncScript(String script, Object... args) ;
}
