package banana.core.actions;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

import banana.core.extractor2.html.Util;
import banana.core.modle.ContextModle;
import banana.core.response.HttpResponse;
import banana.core.response.Page;

public class Actions {

	private static Logger logger = Logger.getLogger(Actions.class);
	
	private RemoteWebDriver webdriver;

	private ContextModle context;
	
	private HttpResponse response;

	public Actions(RemoteWebDriver webdriver,HttpResponse response,ContextModle context) {
		this.webdriver = webdriver;
		this.context = context;
		this.response = response;
		init();
	}

	private void init() {
		context.registerHelper("selenium_captureElement", new Helper<Object>() {
			@Override
			public Object apply(Object context, Options options) throws IOException {
				String cssSelector = options.param(0);
				cssSelector = Util.jqueryCompatibility(cssSelector);
				String output = options.param(1);
				String statuskey = options.params.length > 2 ? options.param(options.params.length - 1) : null;
				selenium_captureElement(cssSelector, output, statuskey);
				return null;
			}
		});
		context.registerHelper("selenium_sendKeys", new Helper<Object>() {
			@Override
			public Object apply(Object context, Options options) throws IOException {
				String cssSelector = options.param(0);
				cssSelector = Util.jqueryCompatibility(cssSelector);
				String keys = options.param(1);
				String statuskey = options.params.length > 2 ? options.param(options.params.length - 1) : null;
				selenium_sendKeys(cssSelector, keys, statuskey);
				return null;
			}
		});
		context.registerHelper("selenium_click", new Helper<Object>() {
			@Override
			public Object apply(Object context, Options options) throws IOException {
				String cssSelector = options.param(0);
				cssSelector = Util.jqueryCompatibility(cssSelector);
				String statuskey = options.params.length > 1 ? options.param(options.params.length - 1) : null;
				selenium_click(cssSelector, statuskey);
				return null;
			}
		});
		context.registerHelper("selenium_jsclick", new Helper<Object>() {
			@Override
			public Object apply(Object context, Options options) throws IOException {
				String cssSelector = options.param(0);
				cssSelector = Util.jqueryCompatibility(cssSelector);
				String statuskey = options.params.length > 1 ? options.param(options.params.length - 1) : null;
				selenium_jsclick(cssSelector, statuskey);
				return null;
			}
		});
		context.registerHelper("ocrDecode", new Helper<Object>() {
			@Override
			public Object apply(Object context, Options options) throws IOException {
				byte[] imageBody = options.param(0);
				String type = options.param(1);
				String output = options.param(2);
				ocrDecode(imageBody, type, output);
				return null;
			}
		});
	}

	public void start(String[] actions) throws IOException {
		for (int i = 0; i < actions.length; i++) {
			context.parseString(actions[i]);
		}
	}
	
	public void ocrDecode(byte[] imageBody, String type, String output) {
		String result = OCR.ocrDecode(imageBody, type);
		if (result == null || result.trim().isEmpty()) {
			result = "noResult";
		}
		context.put(output, result);
		logger.info("ocrDecode result:"+result);
	}
	
	public void selenium_click(String cssSelector, String statuskey) {
		try {
			WebElement element = webdriver.findElementByCssSelector(cssSelector);
			if (element == null) {
				logger.info("没有找到元素"+cssSelector);
				return;
			}
			if (!element.isEnabled()) {
				logger.info("元素点击");
			}
//			org.openqa.selenium.interactions.Actions seleniumAction = new org.openqa.selenium.interactions.Actions(webdriver);
//			seleniumAction.moveToElement(element).build().perform();
//			webdriver.executeScript("arguments[0].scrollTop=100;", element);Thread.sleep(2000);
			element.click();
			context.put("_owner_url", webdriver.getCurrentUrl());
			context.put("_content", webdriver.getPageSource());
			response.setOwnerUrl((String) context.get("_owner_url"));
			((Page)response).setContent((String) context.get("_content"));Thread.sleep(5000);
			if (statuskey != null) {
				context.put(statuskey, true);
			}
		} catch (Exception e) {
			logger.warn("点击失败 " + e.getMessage());
			if (statuskey != null) {
				context.put(statuskey, false);
			}
		}
	}
	
	
	public void selenium_jsclick(String cssSelector, String statuskey) {
		try {
			webdriver.executeScript("document.querySelector(arguments[0]).click()", cssSelector);Thread.sleep(2000);
			context.put("_owner_url", webdriver.getCurrentUrl());
			context.put("_content", webdriver.getPageSource());
			response.setOwnerUrl((String) context.get("_owner_url"));
			((Page)response).setContent((String) context.get("_content"));Thread.sleep(5000);
			if (statuskey != null) {
				context.put(statuskey, true);
			}
		} catch (Exception e) {
			logger.warn("点击失败 " + e.getMessage());
			if (statuskey != null) {
				context.put(statuskey, false);
			}
		}
	}
	
	public void selenium_sendKeys(String cssSelector,String keys,String statuskey) {
		try {
			WebElement element = webdriver.findElementByCssSelector(cssSelector);
			if (element == null) {
				logger.info("没有找到元素"+cssSelector);
				return;
			}
			if (!element.isEnabled()) {
				logger.info("元素不能编辑");
			}
			element.clear();
			element.sendKeys(keys);Thread.sleep(5000);
			if (statuskey != null) {
				context.put(statuskey, true);
			}
		} catch (Exception e) {
			logger.warn("设置文字失败" + e.getMessage());
			if (statuskey != null) {
				context.put(statuskey, false);
			}
		}
	}

	public void selenium_captureElement(String cssSelector, String output, String statuskey) {
		try {
			WebElement element = webdriver.findElementByCssSelector(cssSelector);
			if (element == null) {
				logger.info("没有找到元素"+cssSelector);
				return;
			}
			byte[] body = selenium_ElementToBytes(element);
			if (body != null) {
				context.put(output, body);
			}
			if (statuskey != null) {
				context.put(statuskey, true);
			}
		} catch (Exception e) {
			logger.warn("截图失败" + e.getMessage());
			if (statuskey != null) {
				context.put(statuskey, false);
			}
		}
	}

	// 页面元素截图
	private byte[] selenium_ElementToBytes(WebElement element) throws Exception {
		// 截图整个页面
		byte[] body = webdriver.getScreenshotAs(OutputType.BYTES);
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(body));
		// 获得元素的高度和宽度
		int width = element.getSize().getWidth();
		int height = element.getSize().getHeight();
		// 创建一个矩形使用上面的高度，和宽度
		Rectangle rect = new Rectangle(width, height);
		// 得到元素的坐标
		Point p = element.getLocation();
		BufferedImage dest = img.getSubimage(p.getX(), p.getY(), rect.width, rect.height);
		// 存为png格式
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(dest, "png", baos);
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (baos != null) {
				baos.close();
			}
		}
		return null;
	}

	public static Logger getLogger() {
		return logger;
	}

	public RemoteWebDriver getWebdriver() {
		return webdriver;
	}

	public ContextModle getContext() {
		return context;
	}

	public HttpResponse getResponse() {
		return response;
	}

}
