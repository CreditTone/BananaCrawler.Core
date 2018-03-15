package banana.core.extractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jayway.jsonpath.JsonPath;

import banana.core.request.HttpRequest;
import banana.core.request.PageRequest;
import banana.core.request.RequestBuilder;

import banana.core.download.impl.DefaultHttpDownloader;
import banana.core.download.impl.HttpsProxy;
import banana.core.modle.ContextModle;

public class Extractor {
	
	private static Logger logger = Logger.getLogger(Extractor.class);
	
	public static Object toDo(String data,String extractorConfig,ContextModle contextModle) throws Exception {
		return doExtractor(data, new ExtractorConfig(JSON.parseObject(extractorConfig)),contextModle);
	}
	
	public static Object doExtractor(String data,ExtractorConfig extractorConfig,ContextModle contextModle) throws Exception {
		Object result = null;
		if (extractorConfig.getOutput_type().equals("map")) {
			if (extractorConfig.getInput_type().equals("html")) {
				Document doc = Jsoup.parse(data);
				result = htmlParseMapResult(doc, (HashMap<String, Object>) extractorConfig.getExtractorConfig().clone(), contextModle);
			} else if (extractorConfig.getInput_type().equals("json")) {
				result = jsonParseMapResult(data, (HashMap<String, Object>) extractorConfig.getExtractorConfig().clone(), contextModle);
			} else if (extractorConfig.getInput_type().equals("string")) {
				result = stringRegexMapResult(data, (HashMap<String, Object>) extractorConfig.getExtractorConfig().clone(), contextModle);
			}
		}else if (extractorConfig.getOutput_type().equals("list")) {
			if (extractorConfig.getInput_type().equals("html")) {
				Document doc = Jsoup.parse(data);
				result = htmlParseListReuslt(doc, (HashMap<String, Object>) extractorConfig.getExtractorConfig().clone(), contextModle);
			} else if (extractorConfig.getInput_type().equals("json")) {
				result = jsonParseListResult(data, (HashMap<String, Object>) extractorConfig.getExtractorConfig().clone(), contextModle);
			} else if (extractorConfig.getInput_type().equals("string")) {
				result = stringRegexListResult(data, (HashMap<String, Object>) extractorConfig.getExtractorConfig().clone(), contextModle);
			}
		}
		return result;
	}
	
	public static void main(String[] args) throws Exception {
		//String url = "https://shopsearch.taobao.com/search?app=shopsearch&q=%E5%94%AF%E6%9F%94%E6%97%97%E8%88%B0%E5%BA%97&imgfile=&js=1&stats_click=search_radio_all%3A1&initiative_id=staobaoz_20180309&ie=utf8";
		//Document doc = Jsoup.connect(url).get();
		//System.out.println(doc);
		//System.out.println("----------");
		DefaultHttpDownloader d = new DefaultHttpDownloader();
		HttpsProxy proxy = new HttpsProxy();
		proxy.setServer("servercountry-CN.zproxy.luminati.io");
		proxy.setPort(22225);
		proxy.setUsername("lum-customer-guozhong-zone-residential-country-cn-session-2896809615");
		proxy.setPassword("74a3a5f2ce9f");
		d.setPorxy(proxy);
		HttpRequest httpRequest = RequestBuilder.custom().setUrl("http://www.ebuy16.com/buy/zg/1055_1.html").build();
		String parse = "{\"_output_type\":\"list\",\n" + 
				"                    \"_root\":\"div.scc ul.other li\",\n" + 
				"                    \"url\":\"a;href\"}";
		String parse2 = "{\"shop_names\":{\n" + 
				"                    \"_root\":\"div.shop_list li\",\n" + 
				"                    \"_output_type\":\"list\",\n" + 
				"                    \"shopname\":\"a\"\n" + 
				"                }}";
		String json = d.download((PageRequest) httpRequest).toString();
		System.out.println(json);
		Object v = Extractor.toDo(json, parse, null);
		System.out.println(v);
	}
	
	public static Elements catchExSelect(Element element,String cssSelector) {
		try {
			if (cssSelector.trim().isEmpty()) {
				return new Elements(element);
			}
			Elements els = element.select(cssSelector);
			return els;
		}catch(Exception e) {
			logger.warn("catchExSelect:"+e.getMessage());
		}
		return null;
	}
	
	private static String filterJSONP(String json) {
		int p0 = json.indexOf("{");
		if (p0 == -1) {
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
	
	public static String catchExRegex(String str,String regex) {
		try {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(str);
			if (matcher.find()) {
				if (matcher.groupCount() > 0) {
					return matcher.group(1);
				}
				return matcher.group();
			}
		}catch(Exception e) {
			logger.warn("catchExRegex:"+e.getMessage());
		}
		return null;
	}
	
	private static Map<String,Object> htmlParseMapResult(Element element, HashMap<String,Object> extractorConfig,ContextModle contextModle) throws Exception {
		ParserLine _root = (ParserLine) extractorConfig.remove("_root");
		if (_root != null) {
			Elements roots = catchExSelect(element, _root.getJsoup_cssSelector());
			if (roots == null || roots.isEmpty()) {
				logger.info("_root "+ _root.getParserLine() + " 找不到元素");
				return null;
			}
			element = roots.first();
		}
		Map<String,Object> result = new HashMap<String, Object>();
		for (Entry<String, Object> entry : extractorConfig.entrySet()) {
			Object parser = entry.getValue();
			if (parser instanceof ParserLine) {
				ParserLine currentParser = (ParserLine)parser;
				if (currentParser.isContextParseObject()) {
					Object value = contextModle.parseObject(currentParser.getParserLine());
					result.put(entry.getKey(), value);
					continue;
				}else if (currentParser.isContextParse()) {
					String value = contextModle.parseString(currentParser.getParserLine());
					result.put(entry.getKey(), convertType(value, currentParser));
					continue;
				}
				Elements els = catchExSelect(element, currentParser.getJsoup_cssSelector());
				if (els != null && !els.isEmpty()) {
					if (currentParser.getJsoup_attr() != null) {
						String value = null;
						if (currentParser.getJsoup_attr().isEmpty() || "html".equals(currentParser.getJsoup_attr())) {
							value = els.outerHtml();
						}else {
							value = els.attr(currentParser.getJsoup_attr());
						}
						if (currentParser.getJsoup_regular() != null) {
							value = catchExRegex(value, currentParser.getJsoup_regular());
						}
						if (value != null) {
							result.put(entry.getKey(), convertType(value, currentParser));
						}else {
							result.put(entry.getKey(), null);
						}
					}else {
						Object value = els.first().text().trim();
						if (value != null) {
							value = convertType(value, currentParser);
						}
						result.put(entry.getKey(), value);
					}
				}else {
					result.put(entry.getKey(), null);
				}
			} else if (parser instanceof ExtractorConfig) {
				result.put(entry.getKey(), doExtractor(element.outerHtml(), (ExtractorConfig) parser,null));
			}
		}
		return result.isEmpty()?null:result;
	}
	
	
	private static List<Object> htmlParseListReuslt(Element element, HashMap<String,Object> extractorConfig,ContextModle contextModle) throws Exception {
		ParserLine _root = (ParserLine) extractorConfig.remove("_root");
		Elements roots = null;
		if (_root != null) {
			roots = catchExSelect(element, _root.getJsoup_cssSelector());
			if (roots == null || roots.isEmpty()) {
				logger.info("_root "+ _root.getParserLine() + " 找不到元素");
				return null;
			}
		}
		List<Object> result = new ArrayList<Object>();
		for (Element root : roots) {
			if (extractorConfig.isEmpty()) {
				result.add(root.text().trim());
				continue;
			}
			result.add(htmlParseMapResult(root, extractorConfig,contextModle));
		}
		return result.isEmpty()?null:result;
	}
	
	private static Object jsonParseMapResult(String json, HashMap<String,Object> extractorConfig,ContextModle contextModle) throws Exception {
		ParserLine _root = (ParserLine) extractorConfig.remove("_root");
		if (_root != null) {
			Object obj = catchExReadJson(json, _root.getParserLine());
			if (obj == null) {
				return null;
			}
			json = obj.toString();
			if (json.startsWith("[")) {
				return jsonParseListResult(json, extractorConfig, contextModle);
			}
		}
		Map<String,Object> result = new HashMap<String, Object>();
		for (Entry<String, Object> entry : extractorConfig.entrySet()) {
			Object parser = entry.getValue();
			if (parser instanceof ParserLine) {
				ParserLine currentParser = (ParserLine) parser;
				if (currentParser.isContextParseObject()) {
					Object value = contextModle.parseObject(currentParser.getParserLine());
					result.put(entry.getKey(), value);
					continue;
				}else if (currentParser.isContextParse()) {
					String value = contextModle.parseString(currentParser.getParserLine());
					result.put(entry.getKey(), convertType(value, currentParser));
					continue;
				}
				Object obj = catchExReadJson(json, currentParser.getParserLine());
				result.put(entry.getKey(), convertType(obj, currentParser));
			} else if (parser instanceof ExtractorConfig) {
				result.put(entry.getKey(), doExtractor(json, (ExtractorConfig) parser,contextModle));
			}
		}
		return result.isEmpty()?null:result;
	}
	
	
	private static Object jsonParseListResult(String json, HashMap<String,Object> extractorConfig,ContextModle contextModle) throws Exception {
		ParserLine _root = (ParserLine) extractorConfig.remove("_root");
		if (_root != null) {
			Object obj = catchExReadJson(json, _root.getParserLine());
			if (obj == null) {
				return null;
			}
			json = obj.toString();
		}
		List<Object> result = new ArrayList<Object>();
		JSONArray array = JSON.parseArray(json);
		for (int x = 0 ; x<array.size(); x++) {
			if (extractorConfig.isEmpty()) {
				result.add(array.get(x));
				continue;
			}
			result.add(jsonParseMapResult(array.get(x).toString(), extractorConfig,contextModle));
		}
		return result.isEmpty()?null:result;
	}
	
	private static Object stringRegexMapResult(String str, HashMap<String,Object> extractorConfig,ContextModle contextModle) throws Exception {
		ParserLine _root = (ParserLine) extractorConfig.remove("_root");
		if (_root != null) {
			str = catchExRegex(str, _root.getParserLine());
			if (str == null) {
				return null;
			}
		}
		Map<String,Object> result = new HashMap<String, Object>();
		for (Entry<String, Object> entry : extractorConfig.entrySet()) {
			Object parser = entry.getValue();
			if (parser instanceof ParserLine) {
				ParserLine currentParser = (ParserLine) parser;
				if (currentParser.isContextParse()) {
					String value = contextModle.parseString(currentParser.getParserLine());
					result.put(entry.getKey(), convertType(value, currentParser));
					continue;
				}
				String obj = catchExRegex(str, currentParser.getParserLine());
				if (obj != null) {
					result.put(entry.getKey(), convertType(obj, currentParser));
				}else {
					result.put(entry.getKey(), null);
				}
			} else if (parser instanceof ExtractorConfig) {
				result.put(entry.getKey(), doExtractor(str, (ExtractorConfig) parser,contextModle));
			}
		}
		return result.isEmpty()?null:result;
	}
	
	private static Object stringRegexListResult(String str, HashMap<String,Object> extractorConfig,ContextModle contextModle) throws Exception {
		ParserLine _root = (ParserLine) extractorConfig.remove("_root");
		List<String> segments = new ArrayList<String>();
		if (_root != null) {
			try {
				Pattern pattern = Pattern.compile(_root.getParserLine());
				Matcher matcher = pattern.matcher(str);
				while (matcher.find()) {
					if (matcher.groupCount() > 0) {
						segments.add(matcher.group(1));
					}else {
						segments.add(matcher.group());
					}
				}
			}catch(Exception e) {
				logger.warn(e.getMessage());
			}
			if (segments.isEmpty()) {
				return null;
			}
		}
		List<Object> result = new ArrayList<Object>();
		for (String segment : segments) {
			if (extractorConfig.isEmpty()) {
				result.add(segment);
				continue;
			}
			result.add(stringRegexMapResult(segment, extractorConfig, contextModle));
		}
		return result.isEmpty()?null:result;
	}
	
	private static Object convertType(Object obj,ParserLine parser) {
		try {
			if (parser.isExpectTypeString()) {
				return obj.toString();
			}else if (parser.isExpectTypeInt()) {
				if (obj instanceof Integer) {
					return obj;
				}else {
					return Integer.parseInt(obj.toString());
				}
			}else if (parser.isExpectTypeBoolean()) {
				if (obj instanceof Boolean) {
					return obj;
				}else {
					return Boolean.parseBoolean(obj.toString());
				}
			}else if (parser.isExpectTypeDouble()) {
				if (obj instanceof Double) {
					return obj;
				}else {
					return Double.parseDouble(obj.toString());
				}
			}else if (parser.isExpectTypeFloat()) {
				if (obj instanceof Float) {
					return obj;
				}else {
					return Float.parseFloat(obj.toString());
				}
			}
		}catch(Exception e) {
			logger.warn(e.getMessage());
			return null;
		}
		return obj;
	}
	
}
