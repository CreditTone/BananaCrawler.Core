package banana.core.download.impl;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import banana.core.download.impl.DefaultHttpDownloader;
import banana.core.download.pool.PhantomJsDriverPool;
import banana.core.request.PageRequest;
import banana.core.response.Page;

public class PhantomJsDownloader extends DefaultHttpDownloader {
	
	private final Logger logger = Logger.getLogger(PhantomJsDownloader.class);
	
	private PhantomJsDriverPool driverPool = new PhantomJsDriverPool();
	
	public PhantomJsDownloader(String phantomjs) {
		System.setProperty("phantomjs.binary.path", phantomjs);
	}

	@Override
	public Page download(PageRequest request) {
		Page page = null;
		PhantomJSDriver driver = null;
		try {
			driver = driverPool.get();
			driver.get(request.getEncodeUrl());
			page = new Page();
			if (driver.getPageSource().startsWith("<html><head></head><body><pre style=\"word-wrap: break-word; white-space: pre-wrap;\">")){
				int beginIndex = 84;
				page.setContent(driver.getPageSource().substring(beginIndex, driver.getPageSource().length()-20));
			}else{
				page.setContent(driver.getPageSource());
			}
			page.setStatus(200);
			page.setRequest(request);
		} catch (InterruptedException e) {
			logger.warn("download error " + request.getUrl(),e);
		}finally{
			if (driver != null){
				driverPool.returnToPool(driver);
			}
		}
		return page;
	}

	@Override
	public void close() throws IOException {
		super.close();
		driverPool.closeAll();
	}

	@Override
	public void open() {
		super.open();
		driverPool.open();
	}

	@Override
	public boolean supportJavaScript() {
		return true;
	}
	
}
