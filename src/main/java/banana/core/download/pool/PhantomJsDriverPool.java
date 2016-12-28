package banana.core.download.pool;


import java.util.Iterator;

import org.openqa.selenium.phantomjs.PhantomJSDriver;

import banana.core.download.pool.DriverPoolInterface;
import banana.core.request.Cookie;
import banana.core.request.Cookies;

public class PhantomJsDriverPool extends DriverPoolInterface<PhantomJSDriver> {
	
	
	private Cookies cookies;
	
	public PhantomJsDriverPool(Cookies cookies) {
		this.cookies = cookies;
	}
	
	@Override
	public void open() {
	}

	@Override
	public void closeAll() {
		Iterator<PhantomJSDriver> iter = queue.iterator();
		while(iter.hasNext()){
			PhantomJSDriver driver = iter.next();
			driver.quit();
		}
		queue.clear();
	}

	@Override
	public PhantomJSDriver createDriver() {
		PhantomJSDriver driver = new PhantomJSDriver();
		if (cookies != null){
			Iterator<Cookie> iter = cookies.iterator();
			while(iter.hasNext()){
				driver.manage().addCookie(iter.next().convertSeleniumCookie());
			}
		}
		return driver;
	}

}
