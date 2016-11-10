package banana.core.download.pool;


import java.util.Iterator;

import org.openqa.selenium.phantomjs.PhantomJSDriver;

import banana.core.download.pool.DriverPoolInterface;

public class PhantomJsDriverPool extends DriverPoolInterface<PhantomJSDriver> {
	
	
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
		return new PhantomJSDriver();
	}

	@Override
	public void returnToPool(PhantomJSDriver driver) {
		queue.add(driver);
	}

}
