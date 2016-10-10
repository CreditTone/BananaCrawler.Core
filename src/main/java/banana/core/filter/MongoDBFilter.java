package banana.core.filter;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class MongoDBFilter implements Filter {
	
	private SimpleBloomFilter bloomFilter = new SimpleBloomFilter();
	
	private String key;
	
	private DBCollection collection;
	
	public MongoDBFilter(String key,DBCollection collection){
		this.key = key;
		this.collection = collection;
		DBCursor cursor = collection.find(new BasicDBObject(), new BasicDBObject(key,1));
		while (cursor.hasNext()){
			String value = cursor.next().get(key).toString();
			bloomFilter.add(value);
		}
	}

	@Override
	public void add(String value) {
		bloomFilter.add(value);
	}

	@Override
	public boolean contains(String value) {
		if (bloomFilter.contains(value)){
			return true;
		}
		int count = collection.find(new BasicDBObject(key, value)).count();
		if (count > 0){
			bloomFilter.add(value);
			return true;
		}
		return false;
	}

	@Override
	public byte[] toBytes() {
		return bloomFilter.toBytes();
	}

	@Override
	public void load(byte[] data) {
		bloomFilter.load(data);
	}

}
