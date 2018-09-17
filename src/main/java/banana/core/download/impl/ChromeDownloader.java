package banana.core.download.impl;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import banana.core.download.impl.DefaultHttpDownloader;
import banana.core.request.Cookies;
import banana.core.request.PageRequest;
import banana.core.response.Page;

public class ChromeDownloader extends DefaultHttpDownloader {
	
	private final Logger logger = Logger.getLogger(ChromeDownloader.class);
	
	private RemoteWebDriver webDriver;
	
	public ChromeDownloader(String service,Cookies cookies) {
		DesiredCapabilities desiredCapabilities = DesiredCapabilities.chrome();
		Map<String,Object> timeouts = new HashMap<String,Object>();
		timeouts.put("implicit", 5000);
		timeouts.put("pageLoad", 15000);
		timeouts.put("script", 10000);
		desiredCapabilities.setCapability("timeouts", timeouts);
		System.out.println("初始化中....");
		try {
			webDriver = new RemoteWebDriver(new URL(service),desiredCapabilities) {
				@Override
				public void get(String url) {
					try {
						super.get(url);
					}catch(UnhandledAlertException e) {
						logger.warn("UnhandledAlertException请忽略", e);
					}catch(Exception e) {
						String message = e.getMessage().toLowerCase();
						if (!message.contains("timeout")) {
							e.printStackTrace();
						}
					}
				}
				
				@Override
				public String getCurrentUrl() {
					String href = (String) executeScript("return window.location.href");
					System.out.println("href="+href);
					return href;
				}
			};
			System.out.println("初始化完成....");
		} catch (UnhandledAlertException e) {
			logger.warn("UnhandledAlertException请忽略", e);
		} catch (Exception e) {
			logger.warn("创建webDriver失败", e);
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		ChromeDownloader downloader = new ChromeDownloader("http://10.2.60.178:4444/wd/hub", null);
		Thread.sleep(10 * 1000);
		downloader.webDriver.get("http://wf.meituan.com");
		Thread.sleep(60 * 1000);
	}
	
	@Override
	public Page download(PageRequest request) {
		Page page = null;
		try {
			webDriver.get(request.getEncodeUrl());
			page = new Page();
			if (webDriver.getPageSource().startsWith("<html><head></head><body><pre style=\"word-wrap: break-word; white-space: pre-wrap;\">")){
				int beginIndex = 84;
				page.setContent(webDriver.getPageSource().substring(beginIndex, webDriver.getPageSource().length() - 20));
			}else{
				page.setContent(webDriver.getPageSource());
			}
			page.setStatus(200);
			page.setOwnerUrl(webDriver.getCurrentUrl());
			page.setRequest(request);
			page.setDriverId(webDriver.getSessionId().toString());
			Thread.sleep(3000);
		}catch (Exception e) {
			logger.warn("download error " + request.getUrl(),e);
		}
		return page;
	}
	
	@Override
	public void close() throws IOException {
		super.close();
	}

	@Override
	public void open() {
		super.open();
	}

	@Override
	public boolean supportJavaScript() {
		return true;
	}

	@Override
	public void injectCookies(Cookies cookies) {
		super.injectCookies(cookies);
	}
	
	public RemoteWebDriver getRemoteWebDriver() {
		return webDriver;
	}
}
