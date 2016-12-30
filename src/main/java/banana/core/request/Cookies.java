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
	
	public Cookies() {
	}
	
	public Cookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}
	
	public void addCookie(Cookie cookie){
		cookies.add(cookie);
	}
	
	public Iterator<Cookie> iterator(){
		return cookies.iterator();
	}
	
	@Override
	public void write(DataOutput out) throws IOException {
		String jsonArr = JSON.toJSONString(cookies);
		out.writeUTF(jsonArr);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		JSONArray arr = JSON.parseArray(in.readUTF());
		for (int i= 0 ; i < arr.size() ; i++){
			cookies.add(JSON.parseObject(arr.getJSONObject(i).toJSONString(), Cookie.class));
		}
	}

	@Override
	public String toString() {
		return "Cookies [cookies=" + cookies + "]";
	}

}
