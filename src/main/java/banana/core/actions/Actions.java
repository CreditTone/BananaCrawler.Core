package banana.core.actions;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

import banana.core.modle.ContextModle;
import banana.core.request.HttpRequest;

public class Actions {

	private static Logger logger = Logger.getLogger(Actions.class);
	
	private RemoteWebDriver webdriver;

	private ContextModle context;

	private boolean interrupt;

	public Actions(RemoteWebDriver webdriver,ContextModle context) {
		this.webdriver = webdriver;
		this.context = context;
		init();
	}

	private void init() {
		context.registerHelper("selenium_captureElement", new Helper<Object>() {
			@Override
			public Object apply(Object context, Options options) throws IOException {
				String cssSelector = options.param(0);
				String output = options.param(1);
				selenium_captureElement(cssSelector, output);
				return null;
			}
		});
		context.registerHelper("selenium_sendKeys", new Helper<Object>() {
			@Override
			public Object apply(Object context, Options options) throws IOException {
				String cssSelector = options.param(0);
				String keys = options.param(1);
				selenium_sendKeys(cssSelector, keys);
				return null;
			}
		});
		context.registerHelper("selenium_click", new Helper<Object>() {
			@Override
			public Object apply(Object context, Options options) throws IOException {
				String cssSelector = options.param(0);
				selenium_click(cssSelector);
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
			if (!interrupt) {
				context.parseString(actions[i]);
			}
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
	
	public void selenium_click(String cssSelector) {
		try {
			WebElement element = webdriver.findElementByCssSelector(cssSelector);
			if (element == null) {
				logger.info("没有找到元素"+cssSelector);
				interrupt = true;
				return;
			}
			element.click();
			Thread.sleep(5000);
		} catch (Exception e) {
			interrupt = true;
		}
	}
	
	public void selenium_sendKeys(String cssSelector,String keys) {
		try {
			WebElement element = webdriver.findElementByCssSelector(cssSelector);
			if (element == null) {
				logger.info("没有找到元素"+cssSelector);
				interrupt = true;
				return;
			}
			element.clear();
			element.sendKeys(keys);
			Thread.sleep(5000);
		} catch (Exception e) {
			interrupt = true;
		}
	}

	public void selenium_captureElement(String cssSelector, String output) {
		try {
			WebElement element = webdriver.findElementByCssSelector(cssSelector);
			if (element == null) {
				logger.info("没有找到元素"+cssSelector);
				interrupt = true;
				return;
			}
			byte[] body = selenium_ElementToBytes(element);
			if (body != null) {
				context.put(output, body);
			}
		} catch (Exception e) {
			interrupt = true;
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

}
