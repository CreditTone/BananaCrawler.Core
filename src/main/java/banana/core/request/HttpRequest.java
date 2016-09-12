package banana.core.request;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSON;

import java.util.Map.Entry;

public abstract class HttpRequest extends AttributeRequest {

	/**
	 * 方法类型
	 */
	public enum Method {
		GET, POST;
	}

	protected Method method;

	protected String url;

	/**
	 * request的参数
	 */
	protected Map<String, String> requestParams = null;

	/**
	 * 请求头
	 */
	protected Map<String, String> headers = null;

	protected String processor;

	protected HttpRequest() {
		method = Method.GET;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		if (url == null) {
			throw new NullPointerException();
		} else {
			this.url = url;
		}
	}
	
	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public void putParams(String name, String value) {
		iniParmaContainer();
		if (name != null && value != null) {
			requestParams.put(name, value);
		}
	}

	private void iniParmaContainer() {
		if (requestParams == null) {
			requestParams = new HashMap<String, String>();
		}
	}

	public Set<Entry<String, String>> getParams() {
		iniParmaContainer();
		return this.requestParams.entrySet();
	}

	public Object getParamsByName(String name) {
		iniParmaContainer();
		return this.requestParams.get(name);
	}

	private void iniHeadersContainer() {
		if (headers == null) {
			headers = new HashMap<String, String>();
		}
	}

	public void putHeader(String name, String value) {
		iniHeadersContainer();
		headers.put(name, value);
	}

	public Map<String, String> getHedaers() {
		iniHeadersContainer();
		return this.headers;
	}
	
	public List<NameValuePair> getNameValuePairs(){
		if (method == Method.GET && !url.contains("?")){
			return new ArrayList<NameValuePair>();
		}
		String[] urlData  = url.split("\\?");
		String querys = urlData.length > 1 ? urlData[1]:urlData[0];
		List<NameValuePair> pair = URLEncodedUtils.parse(querys, Charset.defaultCharset());
		if (method == HttpRequest.Method.POST){
			for (Entry<String,String> entry : getParams()) {
				pair.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
			}
		}
		return pair;
	}

	public String getProcessor() {
		return processor;
	}

	public void setProcessor(String processorCls) {
		if (processorCls == null) {
			throw new NullPointerException("Processor不能为Null");
		} else {
			this.processor = processorCls;
		}
	}
	
	public void baseRequest(HttpRequest baseRequest){
		for (String key : baseRequest.enumAttributeNames()) {
			addAttribute(key, baseRequest.getAttribute(key));
		}
		if (url.startsWith("http")){
			return;
		}
		if (url.startsWith("?")){
			String path = baseRequest.getUrl().split("\\?", 2)[0];
			setUrl(path + url);
		}else if (url.startsWith("//")){
			setUrl("https:" + url);
		}else if(url.startsWith("/")){
			int index = baseRequest.getUrl().indexOf("/",7);
			String baseUrl = baseRequest.getUrl().substring(0, index);
			setUrl(baseUrl + url);
		}else{
			int index = baseRequest.getUrl().indexOf("/",7);
			String baseUrl = baseRequest.getUrl().substring(0, index+1);
			setUrl(baseUrl + url);
		}
	}

	@Override
	public void write(DataOutput out) throws IOException {
		super.write(out);
		out.writeUTF(method.name());
		out.writeUTF(url);
		out.writeUTF(processor);
		String headersJson = JSON.toJSONString(headers);
		String requestParamsJson = JSON.toJSONString(requestParams);
		out.writeUTF(headersJson);
		out.writeUTF(requestParamsJson);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		super.readFields(in);
		String methodName = in.readUTF();
		if (methodName.equals(Method.GET.name())) {
			method = Method.GET;
		} else if (methodName.equals(Method.POST.name())) {
			method = Method.POST;
		}
		url = in.readUTF();
		processor = in.readUTF();
		String headersJson = in.readUTF();
		String requestParamsJson = in.readUTF();
		headers = JSON.parseObject(headersJson, Map.class);
		requestParams = JSON.parseObject(requestParamsJson, Map.class);
	}

}
