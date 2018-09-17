package banana.core.context;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.log4j.Logger;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;

import banana.core.util.URLEncodedUtils;

public class ExpandHandlebars extends Handlebars {
	
	private static Logger logger = Logger.getLogger(ExpandHandlebars.class);

	public ExpandHandlebars() {
		StaticMethod.registerRandomChar(this);
		StaticMethod.registerSum(this);
		StaticMethod.registerSubtract(this);
		StaticMethod.registerMultiply(this);
		StaticMethod.registerDivide(this);
		StaticMethod.registerGt(this);
		StaticMethod.registerLt(this);
		StaticMethod.registerHasPrefix(this);
		StaticMethod.registerHasSuffix(this);
		
		registerHelper("eq", new Helper<Object>() {

			public Object apply(Object context, Options options) throws IOException {
				try {
					Object s1 = options.param(0);
					Object s2 = options.param(1);
					if (s1 == null || s2 == null) {
						return false;
					}
					return s1.equals(s2);
				} catch (Exception e) {
					logger.warn(options.context.model() + e.getMessage() + ",好像有东西没解析到哦");
				}
				return true;
			}
		});
		registerHelper("fixKey", new Helper<Object>() {

			public Object apply(Object context, Options options) throws IOException {
				String url = options.param(0);
				if (!url.contains("?")) {
					return url;
				}
				String[] urlData = url.split("\\?");
				String baseUrl = urlData[0];
				String querys = urlData[1];
				List<NameValuePair> pair = URLEncodedUtils.parse(querys);
				for (int i = 1; i < options.params.length; i++) {
					for (NameValuePair nvPair : pair) {
						if (nvPair.getName().equals(options.param(i))) {
							pair.remove(nvPair);
							break;
						}
					}
				}
				baseUrl += "?";
				if (!pair.isEmpty()) {
					Iterator<NameValuePair> iter = pair.iterator();
					NameValuePair nvPair = null;
					while (iter.hasNext()) {
						nvPair = iter.next();
						baseUrl += nvPair.getName() + "=" + nvPair.getValue();
						if (iter.hasNext()) {
							baseUrl += "&";
						}
					}
				}
				return baseUrl;
			}
		});
		registerHelper("contains", new Helper<Object>() {

			public Object apply(Object context, Options options) throws IOException {
				String content = options.param(0);
				String value = options.param(1);
				if (content.contains(value)) {
					return true;
				}
				if (Pattern.compile(value).matcher(content).find()) {
					return true;
				}
				return false;
			}

		});
		registerHelper("substr", new Helper<Object>() {

			public Object apply(Object context, Options options) throws IOException {
				String content = options.param(0);
				int start = options.param(1);
				int end = options.param(2);
				if (start >= 0 && content.length() >= end) {
					return content.substring(start, end);
				}
				return content;
			}

		});
		registerHelper("or", new Helper<Object>() {

			@Override
			public Object apply(Object context, Options options) throws IOException {
				boolean result = false;
				for (int i = 0; i < options.params.length; i++) {
					result = result || (Boolean)options.param(i);
					if (result){
						return true;
					}
				}
				return result;
			}
			
		});
		registerHelper("and", new Helper<Object>() {

			@Override
			public Object apply(Object context, Options options) throws IOException {
				boolean result = true;
				for (int i = 0; i < options.params.length; i++) {
					result = result && (Boolean)options.param(i);
					if (!result){
						return false;
					}
				}
				return result;
			}
			
		});
		registerHelper("date", new Helper<Object>() {

			public Object apply(Object context, Options options) throws IOException {
				Calendar calendar = Calendar.getInstance();
				String format = options.param(0);
				if (options.params.length > 2){
					Integer addYears = options.param(1);
					Integer addMonths = options.param(2);
					Integer addDays = options.param(3);
					calendar.add(Calendar.YEAR, addYears);
					calendar.add(Calendar.MONTH, addMonths);
					calendar.add(Calendar.DAY_OF_MONTH, addDays);
				}
				if (format.equalsIgnoreCase("TimeMillis")){
					return calendar.getTimeInMillis();
				}else if (format.equalsIgnoreCase("Microseconds")){
					return calendar.getTimeInMillis() * 1000;
				}
				return new SimpleDateFormat(format).format(new Date(calendar.getTimeInMillis()));
			}
		});
		registerHelper("for", new Helper<Object>() {

			public Object apply(Object context, Options options) throws IOException {
				List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
				String key = options.param(0);
				int start = Integer.parseInt(options.param(1).toString());
				int end = Integer.parseInt(options.param(2).toString());
				Map<String, Object> dataDef = (Map<String, Object>) options.context.model();
				Map<String, Object> iter_context = new HashMap<>();
				for (int i = start; i < end; i++) {
					iter_context.put(key, i);
					Map<String, Object> item = new HashMap<String, Object>();
					for (Entry<String, Object> def : dataDef.entrySet()) {
						Template template = ExpandHandlebars.this.compileInline(def.getValue().toString());
						item.put(def.getKey(), template.apply(iter_context));
					}
					result.add(item);
				}
				dataDef.put("_data", result);
				return true;
			}
		});
	}

	public Template compileEscapeInline(String input) throws IOException {
		//语法兼容golang templdate
		String javaTemplate = golangTemplate(input);
		System.out.println(javaTemplate);
		return new EscapeTemplate(super.compileInline(javaTemplate));
	}
	
	public static String golangTemplate(String input) {
		List<String> segments = new ArrayList<String>();
		StringBuilder segment = new StringBuilder();
		for (int i = 0 ;i < input.length() ; i++) {
			if (input.charAt(i) == '{' && input.charAt(i+1) == '{') {
				if (segment.length() > 0) {
					segments.add(segment.toString());
				}
				segment = new StringBuilder();
			}else if (input.charAt(i) == '}' && input.charAt(i-1) == '}') {
				if (segment.length() > 0) {
					segment.append(input.charAt(i));
					segments.add(segment.toString());
				}
				segment = new StringBuilder();
				continue;
			}
			segment.append(input.charAt(i));
		}
		if (segment.length() > 0) {
			segments.add(segment.toString());
		}
		for (int x = 0; x < segments.size() ; x++) {
			String segment2 = segments.get(x);
			if (segment2.startsWith("{{") && segment2.endsWith("}}")) {
				StringBuilder item = new StringBuilder()  ;
				boolean isField = segment2.substring(2, segment2.length()-2).trim().startsWith(".");//标记是方法还是字段
				boolean isPassedFuncName = false;
				for (int i = 0 ;i < segment2.length() ; i++) {
					if (segment2.charAt(i) == '.' && (segment2.charAt(i-1) == ' ' || segment2.charAt(i-1) == '\t' || segment2.charAt(i-1) == '{')) {
						continue;
					}
					if (!isField && !isPassedFuncName && (segment2.charAt(i) == ' ' || segment2.charAt(i) == '\t')) {
						isPassedFuncName = true;
						item.append(" n ");
						continue;
					}
					if (segment2.charAt(i) == '\'') {
						item.append("\"");
					}else {
						item.append(segment2.charAt(i));
					}
					
				}
				segments.set(x, item.toString());
			}else {
				continue;
			}
		}
		StringBuilder result = new StringBuilder();
		for (String segment3 : segments) {
			result.append(segment3);
		}
		return result.toString();
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println(golangTemplate("{{selenium_click 'a[class='right-arrow iconfont icon-btn_right']'}}"));
	}
	
	public String escapeParse(String input, Map<String, Object> context) throws IOException {
		Template template = compileEscapeInline(input);
		return template.apply(context);
	}

	public List<Map<String, Object>> toFor(Map<String, Object> iterDef) throws IOException {
		iterDef = new HashMap<String, Object>(iterDef);
		String forDef = (String) iterDef.remove("for");
		Template template = compileInline(forDef);
		template.apply(iterDef);
		return (List<Map<String, Object>>) iterDef.remove("_data");
	}

}
