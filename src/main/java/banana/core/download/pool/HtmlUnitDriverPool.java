package banana.core.download.pool;

import java.util.Iterator;

import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;

import banana.core.request.Cookie;
import banana.core.request.Cookies;

public class HtmlUnitDriverPool extends DriverPoolInterface<HtmlUnitDriver> {

	private Cookies cookies;

	public HtmlUnitDriverPool(Cookies cookies) {
		this.cookies = cookies;
	}

	@Override
	public HtmlUnitDriver createDriver() {
		AccessCookieHtmlUnitDriver driver = new AccessCookieHtmlUnitDriver();
		if (cookies != null) {
			driver.setCookies(cookies);
		}
		return driver;
	}

	@Override
	public void open() {

	}

	@Override
	public void closeAll() {
		Iterator<HtmlUnitDriver> iter = queue.iterator();
		while (iter.hasNext()) {
			HtmlUnitDriver driver = iter.next();
			driver.quit();
		}
		queue.clear();
	}

	public static class AccessCookieHtmlUnitDriver extends HtmlUnitDriver {
		
		public AccessCookieHtmlUnitDriver() {
			super(true);
			WebClient webClient = getWebClient();
			webClient.getOptions().setCssEnabled(false);
			//webClient.getOptions().setTimeout(50000);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);  
			webClient.getOptions().setActiveXNative(false);  //设置是否允许本地ActiveX或没有。默认值是false。
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		}

		public void setCookies(Cookies cookies) {
			CookieManager cookieManager = new CookieManager();
			cookieManager.setCookiesEnabled(true);
			Iterator<Cookie> iter = cookies.iterator();
			while (iter.hasNext()) {
				cookieManager.addCookie(iter.next().convertHtmlunitCookie());
			}
			getWebClient().setCookieManager(cookieManager);
		}
		
	}

}
