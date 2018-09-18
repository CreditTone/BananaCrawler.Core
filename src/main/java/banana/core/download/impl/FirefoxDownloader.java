package banana.core.download.impl;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import banana.core.download.impl.DefaultHttpDownloader;
import banana.core.request.Cookies;
import banana.core.request.PageRequest;
import banana.core.response.Page;

public class FirefoxDownloader extends DefaultHttpDownloader {
	
	private final Logger logger = Logger.getLogger(FirefoxDownloader.class);
	
	private RemoteWebDriver webDriver;
	
	public FirefoxDownloader(String service,Cookies cookies) {
		DesiredCapabilities desiredCapabilities = DesiredCapabilities.firefox();
		Map<String,Object> timeouts = new HashMap<String,Object>();
		timeouts.put("implicit", 5000);
		timeouts.put("pageLoad", 15000);
		timeouts.put("script", 10000);
		desiredCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
		desiredCapabilities.setCapability("ignoreProtectedModeSettings", true);
		desiredCapabilities.setCapability("timeouts", timeouts);
		//desiredCapabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
		Map<String,Object> that = new HashMap<String,Object>();
		Map<String, Object> prefs = new HashMap<String,Object>();
		//禁用css
		//prefs.put("permissions.default.stylesheet", 2);
		//禁用flash
		prefs.put("dom.ipc.plugins.enabled.libflashplayer.so", false);
		that.put("prefs", prefs);
		desiredCapabilities.setCapability("moz:firefoxOptions", that);
		System.out.println("初始化中....");
		try {
			webDriver = new RemoteWebDriver(new URL(service),desiredCapabilities) {
				@Override
				public void get(String url) {
					try {
						super.get(url);
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
	
	public static void main(String[] args) {
		FirefoxDownloader downloader = new FirefoxDownloader("http://10.2.11.176:4444/wd/hub", null);
		downloader.webDriver.get("http://wf.meituan.com");
	}
	
	@Override
	public Page download(PageRequest request) {
		Page page = null;
		try {
			webDriver.get(request.getUrl());
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
		} catch (Exception e) {
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
