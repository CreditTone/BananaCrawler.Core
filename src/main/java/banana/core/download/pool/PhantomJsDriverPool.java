package banana.core.download.pool;

import java.io.File;

import org.openqa.selenium.phantomjs.PhantomJSDriver;

import banana.core.download.pool.DriverPoolInterface;

public class PhantomJsDriverPool extends DriverPoolInterface<PhantomJSDriver> {
	
	
	@Override
	public void open() {
	}

	@Override
	public void closeAll() {
	}

	@Override
	public PhantomJSDriver createDriver() {
		if (System.getProperty("phantomjs.binary.path") == null){
			String bin = PhantomJsDriverPool.class.getClass().getResource("").getPath() + "/bin";
			File file = new File(bin + "/phantomjs");
			if (file.exists()){
				System.setProperty("phantomjs.binary.path", bin + "/phantomjs");
			}else{
				file = new File(bin + "/phantomjs.exe");
				if (file.exists()){
					System.setProperty("phantomjs.binary.path", bin + "/phantomjs.exe");
				}else{
					throw new RuntimeException("phantomjs.binary.path error");
				}
			}
		}
		return new PhantomJSDriver();
	}

}
