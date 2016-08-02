package banana.core.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
/**
 * 可计数的线程池。
 * @author 郭钟 
 */
public class CountableThreadPool implements Closeable{
	    private int threadNum;

	    private AtomicInteger threadAlive = new AtomicInteger();

	    private ReentrantLock reentrantLock = new ReentrantLock();

	    private Condition condition = reentrantLock.newCondition();
	    
	    private ExecutorService executorService;

	    public CountableThreadPool(int threadNum) {
	        this.threadNum = threadNum;
	        this.executorService = Executors.newFixedThreadPool(threadNum);
	    }

	    public CountableThreadPool(int threadNum, ExecutorService executorService) {
	        this.threadNum = threadNum;
	        this.executorService = executorService;
	    }

	    public void setExecutorService(ExecutorService executorService) {
	        this.executorService = executorService;
	    }

	    public int getThreadAlive() {
	        return threadAlive.get();
	    }

	    public int getThreadNum() {
	        return threadNum;
	    }

	    public void execute(final Runnable runnable) {


	        if (threadAlive.get() >= threadNum) {
	            try {
	                reentrantLock.lock();
	                while (threadAlive.get() >= threadNum) {
	                    try {
	                        condition.await();
	                    } catch (InterruptedException e) {
	                    }
	                }
	            } finally {
	                reentrantLock.unlock();
	            }
	        }
	        threadAlive.incrementAndGet();
	        executorService.execute(new Runnable() {
	            @Override
	            public void run() {
	                try {
	                    runnable.run();
	                } finally {
	                    try {
	                        reentrantLock.lock();
	                        threadAlive.decrementAndGet();
	                        condition.signal();
	                    } finally {
	                        reentrantLock.unlock();
	                    }
	                }
	            }
	        });
	    }

	    public boolean isShutdown() {
	        return executorService.isShutdown();
	    }

	    public void shutdown() {
	        executorService.shutdown();
	    }
	    
	    public int getIdleThreadCount(){
	    	return threadNum - getThreadAlive();
	    }

		@Override
		public void close()  {
			executorService.shutdown();
			try {
				while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
					//The thread pool has no closing
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
}
