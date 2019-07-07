package banana.core.download.impl;

import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import banana.core.download.pool.HtmlUnitDriverPool;
import banana.core.request.Cookie;
import banana.core.request.Cookies;
import banana.core.request.PageRequest;
import banana.core.response.Page;

public class HtmlUnitDownloader extends DefaultHttpDownloader {
	
	private final Logger logger = Logger.getLogger(HtmlUnitDownloader.class);
	
	private HtmlUnitDriverPool driverPool;
	
	public HtmlUnitDownloader(Cookies cookies) {
		driverPool = new HtmlUnitDriverPool(cookies);
	}

	@Override
	public Page download(PageRequest request) {
		Page page = null;
		HtmlUnitDriver driver = null;
		try {
			driver = driverPool.get();
			driver.get(request.getEncodeUrl());
			page = new Page();
			if (driver.getPageSource().startsWith("<html><head></head><body><pre style=\"word-wrap: break-word; white-space: pre-wrap;\">")){
				int beginIndex = 84;
				page.setContent(driver.getPageSource().substring(beginIndex, driver.getPageSource().length() - 20));
			}else{
				page.setContent(driver.getPageSource());
			}
			page.setStatus(200);
			page.setRequest(request);
			page.setDriverId(String.valueOf(driver.hashCode()));
		} catch (InterruptedException e) {
			logger.warn("download error " + request.getUrl(),e);
		}finally{
			if (driver != null){
				driverPool.returnToPool(driver);
			}
		}
		return page;
	}

	public void close() throws IOException {
		driverPool.closeAll();
	}

	public void open() {
		driverPool.open();
	}

	@Override
	public boolean supportJavaScript() {
		return true;
	}

	@Override
	public void injectCookies(Cookies cookies) {
		super.injectCookies(cookies);
		Iterator<HtmlUnitDriver> drivers = driverPool.drivers();
		while(drivers.hasNext()){
			HtmlUnitDriver driver = drivers.next();
			Iterator<Cookie> cookieIter = cookies.iterator();
			while (cookieIter.hasNext()){
				Cookie cookie = cookieIter.next();
				driver.manage().addCookie(cookie.convertSeleniumCookie());
			}
		}
	}
}
