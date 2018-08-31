package banana.core.extractor2.json;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jayway.jsonpath.JsonPath;

public class JsonExtractor {

	private static Logger logger = Logger.getLogger(JsonExtractor.class);
	
	public static final String ARRAY_DEFINE  = "_array";
	public static final String FIRST_DEFINE = "_first";
	
	
	private static String filterJSONP(String json) {
		int p0 = json.indexOf("{");
		if (p0 == -1 || p0 > json.indexOf("[")) {
			p0 = json.indexOf("[");
		}
		int p1 = json.indexOf("(");
		if (p1 < 0) {
			return json;
		} else if (p1 >= 0 && p1 > p0) {
			return json;
		}
		p1 += 1;
		int p2 = json.lastIndexOf(")");
		if (p2 <= p1) {
			return json;
		}
		return json.substring(p1, p2);
	}
	
	public static Object catchExReadJson(String json,String readPath) {
		try {
			json = filterJSONP(json);
			Object obj = JsonPath.read(json, readPath);
			return obj;
		}catch(Exception e) {
			logger.warn("catchExReadJson:"+e.getMessage());
		}
		return null;
	}
	
	public static List<Object> doJsonExtractor(String array_define,String val) throws Exception{
		Object obj = catchExReadJson(val, array_define);
		List<Object> result = new ArrayList<Object>();
		JSONArray array = JSON.parseArray(obj.toString());
		for (int x = 0 ; x<array.size(); x++) {
			result.add(array.get(x));
		}
		return result;
	}
	
	public static Object doJsonOneExtractor(String parseConfig,String val) {
		JsonSelector jsonSelector = new JsonSelector(parseConfig);
		Object obj = catchExReadJson(val, jsonSelector.getJsonPath());
		return obj;
	}
	
}
