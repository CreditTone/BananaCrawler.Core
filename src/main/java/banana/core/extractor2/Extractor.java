package banana.core.extractor2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import banana.core.modle.ContextModle;

public class Extractor {
	
	
	public static final String ARRAY_DEFINE  = "_array";
	public static final String FIRST_DEFINE = "_first";

	public static boolean isEmptyParse(Map<String,Object> parseConfig) {
		for (Entry<String,Object> entry : parseConfig.entrySet()) {
			if (!entry.getKey().startsWith("_")) {
				return false;
			}
		}
		return true;
	}
	
	public static Object doComplex(Map<String,Object> parseConfig,String body,ContextModle contextModle) throws Exception {
		try {
			String first_define = (String) parseConfig.get(FIRST_DEFINE);
			String array_define = (String) parseConfig.get(ARRAY_DEFINE);
			if (first_define != null) {
				body = doParseFirst(first_define, body, contextModle);
			}
			if (array_define != null) {
				List<Object> array_ret = doParseArray(array_define, body, contextModle);
				if (array_ret == null || isEmptyParse(parseConfig)) {
					return array_ret;
				}
				List<Object> arrayRet = new ArrayList<>();
				for (Object itemBody : array_ret) {
					Map<String,Object> item = new HashMap<>();
					for (Entry<String,Object> parseItem : parseConfig.entrySet()) {
						String key = parseItem.getKey();
						Object parseVal = parseItem.getValue();
						if (key.startsWith("_")) {
							continue;
						}
						if (parseVal instanceof String) {
							item.put(key, doParseFinalResult((String) parseVal, itemBody.toString(), contextModle));
							continue;
						}
						if (parseVal instanceof Map) {
							item.put(key, doComplex((Map<String, Object>) parseVal, itemBody.toString(), contextModle));
						}
					}
					arrayRet.add(item);
				}
				return arrayRet;
			}
			
			Map<String,Object> mapRet = new HashMap<>();
			for (Entry<String,Object> parseItem : parseConfig.entrySet()) {
				String key = parseItem.getKey();
				Object parseVal = parseItem.getValue();
				if (key.startsWith("_")) {
					continue;
				}
				if (parseVal instanceof String) {
					mapRet.put(key, doParseFinalResult((String) parseVal, body, contextModle));
					continue;
				}
				if (parseVal instanceof Map) {
					mapRet.put(key, doComplex((Map<String, Object>) parseVal, body, contextModle));
				}
			}
			return mapRet;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static Object doOne(String parseConfig,String body,ContextModle contextModle) throws Exception {
		try {
			return doParseFinalResult(parseConfig, body, contextModle);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static List<Object> doParseArray(String complexParseLine,String body,ContextModle contextModle) throws Exception {
		if (complexParseLine.contains("{{") && complexParseLine.contains("}}")) {
			complexParseLine = contextModle.parseString(complexParseLine);
		}
		ComplexSelectLine complexSelectLineObj = new ComplexSelectLine(complexParseLine);
		return complexSelectLineObj.doComplexSelectLineArray(body);
	}
	
	private static String doParseFirst(String complexParseLine,String body,ContextModle contextModle) throws Exception {
		if (complexParseLine.startsWith("{{") && complexParseLine.endsWith("}}")) {
			return contextModle.parseString(complexParseLine);
		}
		if (complexParseLine.contains("{{") && complexParseLine.contains("}}")) {
			complexParseLine = contextModle.parseString(complexParseLine);
		}
		ComplexSelectLine complexSelectLineObj = new ComplexSelectLine(complexParseLine);
		return complexSelectLineObj.doComplexSelectLineFirst(body);
	}
	
	private static Object doParseFinalResult(String complexParseLine,String body,ContextModle contextModle) throws Exception {
		if (complexParseLine.startsWith("{{") && complexParseLine.endsWith("}}")) {
			return contextModle.parseObject(complexParseLine);
		}
		if (complexParseLine.contains("{{") && complexParseLine.contains("}}")) {
			complexParseLine = contextModle.parseString(complexParseLine);
		}
		if (!complexParseLine.contains("html(") && !complexParseLine.contains("json(") && !complexParseLine.contains("string(")) {
			return complexParseLine;
		}
		ComplexSelectLine complexSelectLineObj = new ComplexSelectLine(complexParseLine);
		return complexSelectLineObj.doComplexSelectLine(body);
	}
	
}