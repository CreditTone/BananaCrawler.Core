package banana.core.download.impl;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;


import banana.core.download.FileDownloader;
import banana.core.processor.BinaryProcessor;
import banana.core.request.BinaryRequest;
import banana.core.util.CountableThreadPool;


/**
 * 文件下载器
 *
 */
public final class DefaultFileDownloader implements FileDownloader, Runnable,Closeable {
	private final Logger log = Logger.getLogger(DefaultFileDownloader.class);

	private Map<Class<? extends BinaryProcessor>,BinaryProcessor> binaryProccessors = new HashMap<Class<? extends BinaryProcessor>,BinaryProcessor>();

	private final BlockingQueue<BinaryRequest> requestQueue = new LinkedBlockingQueue<BinaryRequest>();

	private int delayTime;

	private long lastDownloadTime;

	private CountableThreadPool downloadThreadPool;

	public DefaultFileDownloader(int threadNum) {
		downloadThreadPool = new CountableThreadPool(threadNum + 1);
		downloadThreadPool.execute(this);
	}
	
	
	@Override
	public void downloadFile(BinaryRequest req) {
		requestQueue.add(req);
	}

	@Override
	public void setDelayTime(int time) {
		this.delayTime = time;
	}

	@Override
	public void run() {
		while (true) {
			if (requestQueue.isEmpty()||System.currentTimeMillis() - lastDownloadTime < delayTime) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			BinaryRequest req = requestQueue.poll();
			if (req != null) {
				downloadThreadPool.execute(new DowloadTask(req));
				lastDownloadTime = System.currentTimeMillis();
			}
		}
	}

	private final class DowloadTask implements Runnable {
		
		private final BinaryRequest request;
		
		public DowloadTask(BinaryRequest req){
			request = req;
		}
		
		@Override
		public void run() {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet method = null;
			InputStream in = null;
			try {
				String url = request.getUrl();
				method = new HttpGet(url);
				HttpResponse response = client.execute(method);
				BinaryProcessor binaryProccessor = findBinaryProccess(request.getBinaryProccessor());
				in = response.getEntity().getContent();
				if(binaryProccessor != null && (response.getStatusLine().getStatusCode() % 200) < 100){
					binaryProccessor.process(request, in,requestQueue);
				}
			}catch(Exception e){
				//e.printStackTrace();
				log.error(e.getMessage()+":"+request.getUrl());
			}finally {
				if(method != null){
					method.abort();
				}
				if(in != null ){
					try {
						in.close();
					} catch (IOException e) {
					}
				}
			}
			request.notify(request.getUuid());
		}
	}
	
	private synchronized BinaryProcessor addBinaryProccess(Class<? extends BinaryProcessor> proccessCls){
		BinaryProcessor proccess = null;
		try {
			proccess = proccessCls.newInstance();
			if(!binaryProccessors.containsKey(proccessCls)){
				binaryProccessors.put( proccessCls, proccess);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return proccess;
	}
	
	public BinaryProcessor findBinaryProccess(
			Class<? extends BinaryProcessor> processorCls) {
		BinaryProcessor pageProcessor = binaryProccessors.get(processorCls);
		if(pageProcessor == null){
			pageProcessor = addBinaryProccess(processorCls);
		}
		return pageProcessor;
	}


	@Override
	public void close() throws IOException {
		downloadThreadPool.close();
		requestQueue.clear();
	}
	
}
