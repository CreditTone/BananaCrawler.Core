package banana.core.protocol.processor;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteResult;

import banana.core.modle.CrawlData;
import banana.core.processor.DataProcessor;

public class MongoDBDataProcessor implements DataProcessor {

	private DB db;

	public MongoDBDataProcessor(String url) throws NumberFormatException, UnknownHostException {
		String[] split = url.split(",");
		MongoClient client = null;
		ServerAddress serverAddress = new ServerAddress(split[0], Integer.parseInt(split[1]));
		List<ServerAddress> seeds = new ArrayList<ServerAddress>();
		seeds.add(serverAddress);
		String userName = split[3];
		String dataBase = split[2];
		String password = split[4];
		MongoCredential credentials = MongoCredential.createCredential(userName, dataBase,
				password.toCharArray());
		client = new MongoClient(seeds, Arrays.asList(credentials), getOptions());
		db = client.getDB(split[2]);
	}

	private MongoClientOptions getOptions() {
		MongoClientOptions.Builder build = new MongoClientOptions.Builder();
		// 与数据最大连接数50
		build.connectionsPerHost(50);
		// 如果当前所有的connection都在使用中，则每个connection上可以有50个线程排队等待
		build.threadsAllowedToBlockForConnectionMultiplier(50);
		build.connectTimeout(1 * 60 * 1000);
		build.maxWaitTime(2 * 60 * 1000);
		MongoClientOptions options = build.build();
		return options;
	}

	@Override
	public void handleData(List<CrawlData> objectContainer) {
		int x = 0;
		for (CrawlData data : objectContainer) {
			WriteResult result = db.getCollection(data.getTaskName()).insert(objectContainer.get(x).getData());
			System.out.println(result);
			x++;
		}
	}
	
	//test code
//	public static void main(String[] args) throws NumberFormatException, UnknownHostException {
//		String url = "127.0.0.1,27017,crawler,crawler,crawler";
//		MongoDBDataProcessor dataProcessor = new MongoDBDataProcessor(url);
//		String data = "{\"a\":\"bbb\",\"b\":\"bbb\",\"c\":[{\"a\":\"aa\"},{\"b\":\"cccccc\"}]}";
//		CrawlData crawlData = new CrawlData("taoao_123","http://www.taobao.com",data);
//		dataProcessor.handleData(Arrays.asList(crawlData));
//	}

}
