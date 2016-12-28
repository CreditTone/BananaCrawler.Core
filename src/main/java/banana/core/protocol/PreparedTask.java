package banana.core.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import banana.core.BytesWritable;
import banana.core.protocol.Task.Seed;
import banana.core.protocol.Task.SeedQuery;
import banana.core.request.Cookie;
import banana.core.request.Cookies;

public class PreparedTask extends BytesWritable{
	
	public String name;
	
	public List<Seed> seeds = new ArrayList<>();
	
	public List<Cookie> cookies = new ArrayList<>();
	
	public PreparedTask(){
	}
	
	@Override
	public void write(DataOutput out) throws IOException {    
		String seedJson = JSON.toJSONString(seeds == null?new ArrayList<Seed>():seeds);
		String cookiesJson = cookies == null?"[]":JSON.toJSONString(cookies);
		out.writeUTF(name);
		out.writeUTF(seedJson);
		out.writeUTF(cookiesJson);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		name = in.readUTF();
		String seedJson = in.readUTF();
		String cookiesJson = in.readUTF();
		seeds = new ArrayList<Seed>();
		JSONArray array = JSONArray.parseArray(seedJson);
		for (int i = 0; i < array.size(); i++) {
			Seed seed = JSON.parseObject(array.getJSONObject(i).toString(), Seed.class);
			seeds.add(seed);
		}
		JSONArray arr = JSON.parseArray(cookiesJson);
		for (int i= 0 ; i < arr.size() ; i++){
			cookies.add(JSON.parseObject(arr.getJSONObject(i).toJSONString(), Cookie.class));
		}
	}
	
}
