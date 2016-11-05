package banana.core.download.impl;

import org.apache.log4j.Logger;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import banana.core.download.impl.DefaultHttpDownloader;
import banana.core.download.pool.PhantomJsDriverPool;
import banana.core.request.PageRequest;
import banana.core.response.Page;

public class PhantomJsDownloader extends DefaultHttpDownloader {
	
	private final Logger logger = Logger.getLogger(PhantomJsDownloader.class);
	
	private PhantomJsDriverPool driverPool = new PhantomJsDriverPool();

	@Override
	public Page download(PageRequest request) {
		Page page = null;
		try {
			PhantomJSDriver driver = driverPool.get();
			driver.get(request.getUrl());
			page = new Page();
			page.setContent(driver.getPageSource());
			page.setStatus(200);
			page.setRequest(request);
		} catch (InterruptedException e) {
			logger.warn("download error " + request.getUrl(),e);
		}
		return page;
	}
	
	
	
}
