package banana.core.download.pool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class DriverPoolInterface<T> {

	protected LinkedBlockingQueue<T> queue = new LinkedBlockingQueue<T>();

	/**
	 * 统计用过的DriverList。好释放
	 * 
	 */
	protected List<T> driverList = Collections.synchronizedList(new ArrayList<T>());

	protected int max_drivers = Integer.MAX_VALUE;

	protected int min_drivers = 1;

	/**
	 * 从池中取得一个Driver
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public final T get() throws InterruptedException {
		T poll = null;
		if (driverList.size() < min_drivers) {
			synchronized (driverList) {
				if (driverList.size() < min_drivers) {
					invokeCreate();
				}
			}
		}
		poll = queue.poll();
		if (poll != null) {
			return poll;
		}
		if (driverList.size() < max_drivers) { //如果Driver使用的数量美誉达到capacity则继续创建Driver
			synchronized (driverList) {
				if (driverList.size() < max_drivers) {
					invokeCreate();
				}
			}
		}
		return queue.take();  //此方法并不保证立即返回WebDriver，有可能等待之前的Driver执行完回到pool
	}

	public void returnToPool(T driver){
		queue.add(driver);
	}

	public void invokeCreate() {
		T poll = createDriver();
		queue.add(poll);
		driverList.add(poll);
	}

	public abstract T createDriver();
	
	public Iterator<T> drivers(){
		return driverList.iterator();
	}

	public final void setMaxDriverCount(int count) {
		this.max_drivers = count;
	}

	public final int getMaxDriverCount() {
		return max_drivers;
	}

	public final void setMinDriverCount(int count) {
		this.min_drivers = count;
	}

	public final int getMinDriverCount() {
		return min_drivers;
	}

	public abstract void open();

	public abstract void closeAll();
}
