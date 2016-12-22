package banana.core.request;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import banana.core.BytesWritable;

public class Cookies extends BytesWritable {
	
	private List<Cookie> cookies = new ArrayList<Cookie>();
	
	public void addCookie(Cookie cookie){
		cookies.add(cookie);
	}
	
	public Iterator<Cookie> iterator(){
		return cookies.iterator();
	}
	
	@Override
	public void write(DataOutput out) throws IOException {
		String jsonArr = JSON.toJSONString(cookies);
		byte[] body = jsonArr.getBytes();
		out.writeInt(body.length);
		out.write(body);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		int length = in.readInt();
		byte[] body = new byte[length];
		in.readFully(body);
		String jsonArr =  new String(body);
		JSONArray arr = JSON.parseArray(jsonArr);
		for (int i= 0 ; i < arr.size() ; i++){
			cookies.add(JSON.parseObject(arr.getJSONObject(i).toJSONString(), Cookie.class));
		}
	}

}
