package banana.core;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;

import banana.core.util.URLEncodedUtils;

public class ExpandHandlebars extends Handlebars {

	public ExpandHandlebars() {
		registerHelper("add", new Helper<Object>() {

			public Object apply(Object context, Options options) throws IOException {
				int sum = 0;
				int p0 = 0;
				for (int i = 0; i < options.params.length; i++) {
					p0 = Integer.parseInt(options.param(i).toString());
					sum += p0;
				}
				return sum;
			}
		});
		registerHelper("sub", new Helper<Object>() {

			public Object apply(Object context, Options options) throws IOException {
				int p0 = options.param(0);
				for (int i = 1; i < options.params.length; i++) {
					int p = Integer.parseInt(options.param(i).toString());
					p0 -= p;
				}
				return p0;
			}
		});
		registerHelper("multiply", new Helper<Object>() {

			public Object apply(Object context, Options options) throws IOException {
				int product = Integer.parseInt(options.param(0).toString());
				int p0;
				for (int i = 1; i < options.params.length; i++) {
					p0 = Integer.parseInt(options.param(i).toString());
					product *= p0;
				}
				return product;
			}
		});
		registerHelper("gt", new Helper<Object>() {

			public Object apply(Object context, Options options) throws IOException {
				try {
					float p0 = Float.parseFloat(((Object) options.param(0)).toString());
					if (options.param(1) == null) {
						return false;
					}
					float p1 = Float.parseFloat(((Object) options.param(1)).toString());
					return p0 > p1;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}
		});
		registerHelper("lt", new Helper<Object>() {

			public Object apply(Object context, Options options) throws IOException {
				try {
					float p0 = Float.parseFloat(((Object) options.param(0)).toString());
					if (options.param(1) == null) {
						return false;
					}
					float p1 = Float.parseFloat(((Object) options.param(1)).toString());
					return p0 < p1;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}
		});
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
					e.printStackTrace();
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
				String format = options.param(0);
				return new SimpleDateFormat(format).format(new Date());
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
		return new EscapeTemplate(super.compileInline(input));
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
